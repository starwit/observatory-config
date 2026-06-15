package de.starwit.service.sae;

import java.time.LocalDateTime;

public class TrajectoryDto {

	/**
	 * From which stream is this data coming from?
	 */
	private String streamId;

	/**
	 * This ID defines, what kind of object this is.
	 */
	private int classId;

	/**
	 * Unique ID to track an object over time.
	 */
	private String objectId;

	private boolean hasGeoCoordinates = false;

	private Coordinates coordinates = new Coordinates();

	private Shape shape;

	private LocalDateTime receiveTimestamp;

	private BoundingBox boundingBox = new BoundingBox();

	public String getStreamId() {
		return streamId;
	}

	public void setStreamId(String streamId) {
		this.streamId = streamId;
	}

	public int getClassId() {
		return classId;
	}

	public void setClassId(int classId) {
		this.classId = classId;
	}

	public String getObjectId() {
		return objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	public boolean isHasGeoCoordinates() {
		return hasGeoCoordinates;
	}

	public void setHasGeoCoordinates(boolean hasGeoCoordinates) {
		this.hasGeoCoordinates = hasGeoCoordinates;
	}

	public Coordinates getCoordinates() {
		return coordinates;
	}

	public void setCoordinates(Coordinates coordinates) {
		this.coordinates = coordinates;
	}

	public LocalDateTime getReceiveTimestamp() {
		return receiveTimestamp;
	}

	public void setReceiveTimestamp(LocalDateTime receiveTimestamp) {
		this.receiveTimestamp = receiveTimestamp;
	}

	public Shape getShape() {
		return shape;
	}

	public void setShape(int width, int height) {
		this.shape = new Shape(width, height);
	}

	public BoundingBox getBoundingBox() {
		return boundingBox;
	}

	public void setBoundingBox(BoundingBox boundingBox) {
		this.boundingBox = boundingBox;
	}

	@Override
	public String toString() {
		return "TrajectoryDTO [streamId=" + streamId + ", classId=" + classId + ", objectId=" + objectId
				+ ", hasGeoCoordinates=" + hasGeoCoordinates + ", coordinates=" + coordinates + ", boundingBox=" + boundingBox 
				+ ", receiveTimestamp=" + receiveTimestamp + "]";
	}

	class Coordinates {
		private double latitude;
		private double longitude;
		private double x;
		private double y;

		public double getLatitude() {
			return latitude;
		}

		public void setLatitude(double latitude) {
			this.latitude = latitude;
		}

		public double getLongitude() {
			return longitude;
		}

		public void setLongitude(double longitude) {
			this.longitude = longitude;
		}

		public double getX() {
			return x;
		}

		public void setX(double x) {
			this.x = x;
		}

		public double getY() {
			return y;
		}

		public void setY(double y) {
			this.y = y;
		}

		@Override
		public String toString() {
			return "Coordinates [latitude=" + latitude + ", longitude=" + longitude + ", x=" + x + ", y=" + y + "]";
		}
	}

	class Shape {
		private int width;
		private int height;

		public Shape(int width, int height) {
			this.width = width;
			this.height = height;
		}

		public int getWidth() {
			return width;
		}

		public void setWidth(int width) {
			this.width = width;
		}

		public int getHeight() {
			return height;
		}

		public void setHeight(int height) {
			this.height = height;
		}

		@Override
		public String toString() {
			return "Shape [width=" + width + ", height=" + height + "]";
		}
	}

	class BoundingBox {
		private double minX;
		private double minY;
		private double maxX;
		private double maxY;

		public double getMinX() {
			return minX;
		}

		public void setMinX(double minX) {
			this.minX = minX;
		}

		public double getMinY() {
			return minY;
		}

		public void setMinY(double minY) {
			this.minY = minY;
		}

		public double getMaxX() {
			return maxX;
		}

		public void setMaxX(double maxX) {
			this.maxX = maxX;
		}

		public double getMaxY() {
			return maxY;
		}

		public void setMaxY(double maxY) {
			this.maxY = maxY;
		}

		@Override
		public String toString() {
			return "BoundingBoxCoordinates [minX=" + minX + ", minY=" + minY + ", maxX=" + maxX + ", maxY=" + maxY + "]";
		}
	}
}
