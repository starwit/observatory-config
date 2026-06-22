import { WebMercatorViewport } from '@math.gl/web-mercator';
import PlayArrowIcon from '@mui/icons-material/PlayArrow';
import StopIcon from '@mui/icons-material/Stop';
import { Box, Fab, Typography } from "@mui/material";
import React, { useEffect, useMemo, useRef, useState } from "react";
import { useTranslation } from 'react-i18next';
import ColorFunctions from "../services/ColorFunctions";
import StreamRest from "../services/StreamRest";
import WebSocketClient from "../services/WebSocketClient";
import LiveMapView from "./LiveMapView";

function findMax(arr) {
    return arr.reduce((a, b) => Math.max(a, b), -Infinity);
}

function findMin(arr) {
    return arr.reduce((a, b) => Math.min(a, b), Infinity);
}

function markerListToLatitudes(markerList) {
    return Object.keys(markerList).flatMap(stream => markerList[stream].map(m => m.coordinates[1]));
}

function markerListToLongitudes(markerList) {
    return Object.keys(markerList).flatMap(stream => markerList[stream].map(m => m.coordinates[0]));
}

function TrajectoryMap() {
    const { t } = useTranslation();
    const streamRest = useMemo(() => new StreamRest(), []);
    const wsClient = useRef(new WebSocketClient());

    const [streams, setStreams] = useState({});
    const [markerList, setMarkerList] = useState({});
    const [showMap, setShowMap] = useState(false);
    const [started, setStarted] = useState(false);
    const colorFunctions = useRef(new ColorFunctions());

    const minMaxCoords = useRef(null);
    const firstDataTime = useRef(null);
    
    const [mapInitDone, setMapInitDone] = useState(false);
    const [initialViewState, setInitialViewState] = useState({
        longitude: 10.716988775029739,
        latitude: 52.41988232741599,
        zoom: 5,
    });

    function reinitAutoCentering() {
        setMapInitDone(false);
        firstDataTime.current = null;
        minMaxCoords.current = {
            minLat: Infinity, maxLat: -Infinity, minLon: Infinity, maxLon: -Infinity
        };
    }

    // Automatically center the map: Collect some data points first and then calculate useful bounds
    useEffect(() => {
        if (!mapInitDone && minMaxCoords.current !== null) {
            if (firstDataTime.current == null || Date.now() - firstDataTime.current < 1000) {
                const longitudes = markerListToLongitudes(markerList);
                const latitudes = markerListToLatitudes(markerList);
    
                if (latitudes.length > 0 && firstDataTime.current == null) {
                    firstDataTime.current = Date.now();
                    console.log('First data received');
                }
    
                const minLat = findMin(latitudes);
                const maxLat = findMax(latitudes);
                const minLon = findMin(longitudes);
                const maxLon = findMax(longitudes);
    
                const currentMinMax = minMaxCoords.current;
                if (minLon < currentMinMax.minLon) currentMinMax.minLon = minLon;
                if (maxLon > currentMinMax.maxLon) currentMinMax.maxLon = maxLon;
                if (minLat < currentMinMax.minLat) currentMinMax.minLat = minLat;
                if (maxLat > currentMinMax.maxLat) currentMinMax.maxLat = maxLat;
            } else {
                const fitViewport = new WebMercatorViewport().fitBounds(
                    [
                        [minMaxCoords.current.minLon, minMaxCoords.current.minLat], 
                        [minMaxCoords.current.maxLon, minMaxCoords.current.maxLat]
                    ],
                    {
                        width: window.innerWidth, 
                        height: window.innerHeight, 
                        padding: Math.min(window.innerWidth, window.innerHeight) / 5, 
                        minExtent: 0.002
                    }
                );
                setInitialViewState({
                    longitude: fitViewport.longitude,
                    latitude: fitViewport.latitude,
                    zoom: fitViewport.zoom,
                });
                setMapInitDone(true);
                console.log('Map auto-centered');
            }
        }
    }, [markerList]);

    // Load available streams and set up WebSocket connection on component mount
    useEffect(() => {
        streamRest.getAvailableStreams().then(response => {
            const streams = response.data;
            const colors = colorFunctions.current.generateDistinctColors(Object.keys(response.data).length);
            let streamsAndColors = {}
            streams.forEach((stream, index) => {
                console.log(stream);
                streamsAndColors[stream] = colors[index];
            });
            setStreams(streamsAndColors);
            setShowMap(true);

            wsClient.current.setup(handleMessage, streams);
        });
        return () => wsClient.current.disconnect();
    }, []);

    // Handle incoming messages from WebSocket
    function handleMessage(trackedObjectList, streamId) {
        let newMarkers = [];
        trackedObjectList.forEach(trackedObject => {
            if (trackedObject.hasGeoCoordinates) {
                let newMarker = {}
                newMarker.streamId = trackedObject.streamId;
                newMarker.id = trackedObject.objectId;
                newMarker.name = trackedObject.objectId + ' c' + trackedObject.classId;
                newMarker.class = trackedObject.classId;
                newMarker.timestamp = trackedObject.receiveTimestamp;
                newMarker.coordinates = [trackedObject.coordinates.longitude, trackedObject.coordinates.latitude];
                newMarkers.push(newMarker);
            } else {
            }
        });
        setMarkerList(prevMarkerList => ({ ...prevMarkerList, [streamId]: newMarkers }));
    }

    function startStream() {
        wsClient.current.connect();
        reinitAutoCentering();
        setStarted(true);
    }

    function stopStream() {
        wsClient.current.disconnect();
        setStarted(false);
    }

    return (
        <>
            {showMap ? (
                <LiveMapView
                    streams={streams}
                    markerList={markerList}
                    initialViewState={initialViewState}
                />
            ) : (
                <></>
            )}
            <Box sx={{
                position: 'fixed',
                top: 60,
                right: 10
            }}>
                <Typography variant="h1">
                    {t('map.title')}
                </Typography>
            </Box>
            <Box sx={{
                position: 'fixed',
                bottom: 60,
                right: 10
            }}>
                <Fab color="primary">
                    {!started ?
                        <PlayArrowIcon onClick={startStream} /> :
                        <StopIcon onClick={stopStream} />}
                </Fab>
            </Box>
        </>
    )
}

export default TrajectoryMap;