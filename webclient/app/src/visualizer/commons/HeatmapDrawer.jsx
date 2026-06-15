import { HeatmapLayer } from "@deck.gl/aggregation-layers";
import { COORDINATE_SYSTEM, MapView, OrbitView, OrthographicView } from "@deck.gl/core";
import DeckGL from "@deck.gl/react";
import { Typography } from "@mui/material";
import { useEffect, useRef, useState } from "react";
import WebSocketClient from "../services/WebSocketClient";
import { useSettings } from "../contexts/SettingsContext";

function HeatmapDrawer(props) {
    const { stream, running, label } = props;
    const { heatmapExpiryMs, heatmapRadius, heatmapUseCoordinates, heatmapMinUpdateInterval } = useSettings();

    const wsClient = useRef(new WebSocketClient());

    const deckGlContainer = useRef(null);

    const [detections, setDetections] = useState([]);
    const [shape, setShape] = useState({});
    
    // Throttling state for performance optimization
    const lastUpdateTime = useRef(0);
    const pendingDetections = useRef([]); // array of arrays of detections to be added on next update

    useEffect(() => {
        if (running) {
            setDetections([]);
            wsClient.current.setup(handleMessage, [stream]);
            wsClient.current.connect();
        } else {
            wsClient.current.disconnect();
        }
        return () => wsClient.current.disconnect();
    }, [running]);

    function handleMessage(detectionList) {
        if (detectionList.length > 0 && running) {
            updateShape(detectionList);

            addDetectionsToPending(detectionList);

            // Throttle updates - only update if enough time has passed
            const currentTimestamp = new Date().getTime();
            if (currentTimestamp - lastUpdateTime.current >= heatmapMinUpdateInterval) {
                updateDetections(currentTimestamp);
                lastUpdateTime.current = currentTimestamp;
            }
        }
    }

    function addDetectionsToPending(detectionList) {
        const currentTimestamp = new Date().getTime();

        // Compile new detections and add to pending buffer
        const newDetections = detectionList.map(detection => ({
            x: detection.coordinates.x,
            y: detection.coordinates.y,
            latitude: detection.coordinates.latitude,
            longitude: detection.coordinates.longitude,
            timestamp: currentTimestamp,
        }));
        
        // Add entire array to pending detections buffer
        pendingDetections.current.push(newDetections);
    }

    function updateDetections(currentTimestamp) {
        if (pendingDetections.current.length === 0) return;
        
        setDetections(oldDetections => {
            // Find index of first old detection that is still valid
            const firstValidIndex = oldDetections.findIndex(det => 
                (currentTimestamp - det.timestamp) <= heatmapExpiryMs
            );

            // Remove expired detections
            const filteredOldDetections = oldDetections.slice(firstValidIndex !== -1 ? firstValidIndex : oldDetections.length);
            
            console.log(`Old detections: ${oldDetections.length}, oldest age: ${oldDetections.length > 0 ? (currentTimestamp - oldDetections[0].timestamp) : 'N/A'}`);

            // Add all pending detections
            const result = filteredOldDetections.concat(...pendingDetections.current);
            
            // Clear pending buffer
            pendingDetections.current = [];
            
            return result;
        });
    }
    
    function updateShape(trackedObjectList) {
        const newShape = trackedObjectList[0].shape;
        setShape(oldShape => {
            if (oldShape.width == newShape.width && oldShape.height == newShape.height) {
                return oldShape;
            }
            return newShape;                
        });
    }

    const layers = [];

    if (detections && detections.length > 0) {
        const currentTimestamp = new Date().getTime();
        layers.push(
            new HeatmapLayer({
                id: 'detections',
                data: detections.map(d => ({
                    positionPx: [(d.x - shape.width / 2) / 1000, -(d.y - shape.height / 2) / 1000],
                    positionCoords: [d.longitude, d.latitude],
                    age: currentTimestamp - d.timestamp,
                })),
                getPosition: d => heatmapUseCoordinates ? d.positionCoords : d.positionPx,
                getWeight: d => 1 - (d.age / heatmapExpiryMs), // Weight decreases with age
                aggregation: 'SUM',
                radiusPixels: heatmapRadius,
                coordinateSystem: COORDINATE_SYSTEM.LNGLAT,
                debounceTimeout: 200
            })
        );
    }

    return (
        <>
            <div id={stream} ref={deckGlContainer} style={{backgroundColor: 'rgba(149, 149, 149, 0.09)', position: 'relative'}} >
                <Typography variant="body2" style={{position: 'absolute', top: 10, left: 10, color: 'rgba(149, 149, 149, 0.6)'}}>
                    {label}
                </Typography>
                {detections.length > 0 && (
                    <DeckGL
                    views={new MapView({
                        id: 'view', controller: true,
                    })}
                    layers={layers}
                    initialViewState={{
                        longitude: 0,
                        latitude: 0,
                        zoom: 0,
                    }}
                    />
                )}
            </div>
        </>
    )
}

export default HeatmapDrawer;