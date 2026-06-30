class ObjectTracker {
    constructor(maxTrajectoryPoints = 500, maxPassiveTrajectoriesAge = 60000) {
      this.trajectories = new Map(); // Map of objectId to {points: [], stationary: boolean}
      this.passiveTrajectories = [];
      this.maxTrajectoryPoints = maxTrajectoryPoints; // Maximum number of points to keep in a trajectory
      this.maxPassiveTrajectoriesAge = maxPassiveTrajectoriesAge; // Maximum age of passive trajectories in milliseconds
      this.activeTTL = 1000; // Time to live in milliseconds before becoming passive
      this.stationaryWindow = 10000; // Time window in milliseconds to consider an object stationary
      this.stationaryThreshold = 0.05; // Threshold for considering an object stationary (factor of bounding box diagonal)
    }
    
    // Process new detections and update trajectories
    updateTrajectories(detections, timestamp) {
      const currentTime = new Date(timestamp).getTime();
      
      // Process new detections
      detections.forEach(detection => {
        const { objectId } = detection;
        
        // Create a new point
        const point = {
          x: detection.coordinates.x,
          y: detection.coordinates.y,
          timestamp: currentTime,
          objectClass: detection.classId,
          objectId,
          confidence: detection.confidence,
          boundingBox: detection.boundingBox,
        };
        
        // Update trajectory for this object
        if (!this.trajectories.has(objectId)) {
          this.trajectories.set(objectId, {points: [point], stationary: false});
        } else {
          const {points: trajPoints, stationary} = this.trajectories.get(objectId);
          
          // Add the new point
          trajPoints.push(point);
          
          // Truncate if exceeding max points
          if (trajPoints.length > this.maxTrajectoryPoints) {
            trajPoints.shift();
          }

          // Check object for stationary status
          this.processStationaryObject(objectId, currentTime, stationary);
        }
      });

      this.processInactiveObjects(currentTime);
  
      this.purgeStaleTrajectories();
      
      return this.getAllTrajectoryData();
    }

    processStationaryObject(objectId, currentTime, stationaryBefore) {
      // If now stationary (and wasn't before) 
      const trajectory = this.trajectories.get(objectId);
      const points = trajectory.points;
      const isNowStationary = this.isObjectStationary(points);

      if (isNowStationary && !stationaryBefore) {
        this.addPassiveTrajectory(trajectory);
        this.truncateTrajectoryToStationaryWindow(trajectory, currentTime);
        trajectory.stationary = true;
      } else if (isNowStationary && stationaryBefore) {
        this.truncateTrajectoryToStationaryWindow(trajectory, currentTime);
      } else if (!isNowStationary && stationaryBefore) {
        trajectory.stationary = false;
      }
    }

    isObjectStationary(points) {
      // Check if we have enough data for a meaningful calculation
      if (points[points.length-1].timestamp - points[0].timestamp < this.stationaryWindow * 0.8) {
        return false;
      }

      // Filter points to only those within the stationary window
      const windowPoints = points.filter(p => points[points.length-1].timestamp - p.timestamp < this.stationaryWindow);

      // Calculate average position
      let sumX = 0, sumY = 0;
      windowPoints.forEach(point => {
        sumX += point.x;
        sumY += point.y;
      });
      
      const avgX = sumX / windowPoints.length;
      const avgY = sumY / windowPoints.length;
      
      // Calculate average distance from average position
      let sumDistance = 0;
      windowPoints.forEach(point => {
        const dx = point.x - avgX;
        const dy = point.y - avgY;
        sumDistance += Math.sqrt(dx*dx + dy*dy);
      });
      
      const avgDistance = sumDistance / windowPoints.length;
      
      // Get the latest bounding box to calculate the threshold
      const lastPoint = windowPoints[windowPoints.length - 1];
      if (!lastPoint.boundingBox) return false;
      
      const bb = lastPoint.boundingBox;
      const bbDiagonal = Math.sqrt(
        Math.pow(bb.maxX - bb.minX, 2) + 
        Math.pow(bb.maxY - bb.minY, 2)
      );

      const isStationary = avgDistance <= (bbDiagonal * this.stationaryThreshold);

      // Object is stationary if average distance is less than threshold depending on bounding box size
      return isStationary
    }

    // Check all trajectories for objects that have not been seen for activeTTL milliseconds then move them to passive trajectories
    processInactiveObjects(currentTime) {
      this.trajectories.forEach((trajectory, objectId) => {
        const points = trajectory.points;
        const lastPointTime = points[points.length-1].timestamp;
        if (currentTime - lastPointTime > this.activeTTL) {
          this.addPassiveTrajectory(trajectory);
          this.trajectories.delete(objectId);
        }
      });
    }

    addPassiveTrajectory(trajectory) {
      this.passiveTrajectories.push({
        path: trajectory.points.map(p => [p.x, p.y]),
        timestamps: trajectory.points.map(p => p.timestamp),
        isStationary: trajectory.stationary,
        createdAt: new Date().getTime(),
      });
    }

    // Create a method to truncate the trajectory to the stationary window and update the trajectory
    truncateTrajectoryToStationaryWindow(trajectory, currentTime) {
      const stationaryWindow = trajectory.points.filter(p => currentTime - p.timestamp < this.stationaryWindow);
      trajectory.points = stationaryWindow;
    }
    
    purgeStaleTrajectories() {
      // Remove passive trajectories that are too old
      this.passiveTrajectories = this.passiveTrajectories.filter(traj => new Date().getTime() - traj.createdAt < this.maxPassiveTrajectoriesAge);
    }
    
    // Get active trajectory data in a format suitable for deck.gl
    getActiveTrajectoryData() {
      const trajectoryData = [];
      
      this.trajectories.forEach(({points, stationary}, objectId) => {
        // Only include trajectories with at least 2 points
        if (points.length >= 2) {
          trajectoryData.push({
            id: objectId,
            path: points.map(p => [p.x, p.y]),
            timestamps: points.map(p => p.timestamp),
            lastPoint: points[points.length - 1],
            isActive: true,
            isStationary: stationary,
          });
        }
      });
      
      return trajectoryData;
    }
    
    // Get passive trajectory data
    getPassiveTrajectoryData() {
      return this.passiveTrajectories.map((traj, index) => ({
        id: `passive-${index}`,
        path: traj.path,
        timestamps: traj.timestamps,
        isActive: false,
        createdAt: traj.createdAt,
      }));
    }
    
    // Get all trajectory data (both active and passive)
    getAllTrajectoryData() {
      return [
        ...this.getActiveTrajectoryData(),
        ...this.getPassiveTrajectoryData()
      ];
    }
    
    // Clear all trajectories (both active and passive)
    clearTrajectories() {
      this.trajectories.clear();
      this.passiveTrajectories = [];
    }
  }
  
  export default ObjectTracker;