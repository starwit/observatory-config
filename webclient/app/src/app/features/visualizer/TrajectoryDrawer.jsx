import React, {useEffect, useRef, useState} from "react";
import {Box} from "@mui/material";
import DeckGL from "@deck.gl/react";
import {OrthographicView} from "@deck.gl/core";
import {PathLayer, ScatterplotLayer} from "@deck.gl/layers";
import WebSocketClient from "../../services/WebSocketClient";
import {useSettings} from "../../contexts/SettingsContext";
import ObjectTracker from "../../services/ObjectTracker";


const ACTIVE_PATH_COLOR = [100, 200, 0, 255]; // Green for active trajectories
const PASSIVE_PATH_COLOR = [0, 128, 255, 200]; // Blue with some transparency for passive trajectories
const MARKER_COLOR = [90, 190, 0, 255]; // Green for active markers
const STATIONARY_MARKER_COLOR = [137, 196, 255, 255]; // Blue for stationary markers

// Renders live trajectories as a purely-visual deck.gl overlay. It is mounted by the annotator's
// `renderImageOverlay` slot, which positions and sizes a box to exactly cover the image; `width` is
// that box's current on-screen width in CSS pixels. Trajectory coordinates arrive in SAE frame-pixel
// space (the message `shape`), which is deck.gl's coordinate space here — this may differ from the
// annotator image's intrinsic resolution, so we map the frame (not `naturalWidth`) onto the box.
function TrajectoryDrawer(props) {
    const {stream, width} = props;
    const {trajectoryDecayMs} = useSettings();

    const wsClient = useRef(new WebSocketClient());

    const [trajectories, setTrajectories] = useState([]);
    const [shape, setShape] = useState(null);
    const [objectTracker, setObjectTracker] = useState(new ObjectTracker(500, trajectoryDecayMs));

    const layers = [];

    useEffect(() => {
        setTrajectories([]);
        setObjectTracker(new ObjectTracker(500, trajectoryDecayMs));
        wsClient.current.setup(handleMessage, [stream]);
        wsClient.current.connect();
        return () => wsClient.current.disconnect();
    }, [stream, trajectoryDecayMs]);

    // Map the frame-pixel space [0..shape.width] x [0..shape.height] onto the on-screen box.
    const viewState = shape ? {
        target: [shape.width / 2, shape.height / 2, 0],
        zoom: Math.log2(width / shape.width),
    } : null;

    function handleMessage(trackedObjectList) {
        if (trackedObjectList.length > 0) {
            updateShape(trackedObjectList);
            const updatedTrajectories = objectTracker.updateTrajectories(trackedObjectList, trackedObjectList[0].receiveTimestamp);
            setTrajectories(updatedTrajectories);
        }
    }

    function updateShape(trackedObjectList) {
        const newShape = trackedObjectList[0].shape;
        setShape(oldShape => {
            if (oldShape && oldShape.width === newShape.width && oldShape.height === newShape.height) {
                return oldShape;
            }
            return newShape;
        });
    }

    // Get passive color based on age
    function getColorForAge(createdAt) {
        const age = new Date().getTime() - createdAt;
        const alphaFactor = Math.pow(1 - (age / trajectoryDecayMs), 3); // Non-linear fade out
        const alpha = Math.max(0, 255 * alphaFactor);
        return [...PASSIVE_PATH_COLOR.slice(0, 3), alpha];
    };

    if (trajectories && trajectories.length > 0) {
        // First, separate active and passive trajectories
        const activeTrajectories = trajectories.filter(t => t.isActive && !t.isStationary);
        const stationaryTrajectories = trajectories.filter(t => t.isActive && t.isStationary);
        const passiveTrajectories = trajectories.filter(t => !t.isActive);

        // Add paths for passive trajectories (render these first, so they appear below active ones)
        if (passiveTrajectories.length > 0) {
            layers.push(
                new PathLayer({
                    id: 'passive-trajectory-paths',
                    data: passiveTrajectories,
                    getPath: d => d.path,
                    getColor: d => getColorForAge(d.createdAt),
                    getWidth: 2, // Slightly thinner than active trajectories
                    widthUnits: 'pixels',
                    jointRounded: true,
                    capRounded: true,
                    billboard: false,
                    miterLimit: 2
                })
            );
        }

        // Add paths for active trajectories
        if (activeTrajectories.length > 0) {
            layers.push(
                new PathLayer({
                    id: 'active-trajectory-paths',
                    data: activeTrajectories,
                    getPath: d => d.path,
                    getColor: ACTIVE_PATH_COLOR,
                    getWidth: 3,
                    widthUnits: 'pixels',
                    jointRounded: true,
                    capRounded: true,
                    billboard: false,
                    miterLimit: 2,
                })
            );

            // Add points for the current positions (only for active trajectories)
            layers.push(
                new ScatterplotLayer({
                    id: 'active-positions',
                    data: activeTrajectories.map(t => ({
                        position: t.path[t.path.length - 1],
                        id: t.id
                    })),
                    getPosition: d => d.position,
                    getLineColor: [255, 255, 255], // White outline for all markers
                    getFillColor: MARKER_COLOR,
                    getRadius: 6,
                    radiusUnits: 'pixels',
                    stroked: true,
                    lineWidthMinPixels: 1,
                })
            );
        }

        // Add only markers for stationary trajectories
        if (stationaryTrajectories.length > 0) {
            layers.push(
                new ScatterplotLayer({
                    id: 'stationary-positions',
                    data: stationaryTrajectories.map(t => ({
                        position: t.path[t.path.length - 1],
                        id: t.id
                    })),
                    getPosition: d => d.position,
                    getLineColor: [255, 255, 255], // White outline for all markers
                    getFillColor: STATIONARY_MARKER_COLOR,
                    getRadius: 6,
                    radiusUnits: 'pixels',
                    stroked: true,
                    lineWidthMinPixels: 1,
                })
            );
        }
    }

    // Slightly dim the image underneath so the live trajectories stand out.
    return (
        <Box sx={{width: '100%', height: '100%', backgroundColor: 'rgba(0, 0, 0, 0.4)'}}>
            {viewState && (
                <DeckGL
                    views={new OrthographicView({
                        id: 'ortho',
                        flipY: true // Y increases from top to bottom in image space
                    })}
                    viewState={viewState}
                    controller={false}
                    layers={layers}
                    getCursor={() => 'default'}
                    _pickable={false}
                />
            )}
        </Box>
    )
}

export default TrajectoryDrawer;
