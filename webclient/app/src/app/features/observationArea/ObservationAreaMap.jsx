import {MapView} from '@deck.gl/core';
import {TileLayer} from "@deck.gl/geo-layers";
import {BitmapLayer, IconLayer, ScatterplotLayer} from "@deck.gl/layers";
import DeckGL from "@deck.gl/react";
import ColorFunctions from "../../services/ColorFunctions";
import StreamRest from "../../services/StreamRest";
import WebSocketClient from "../../services/WebSocketClient";
import cameraicon from "./../../assets/images/camera3.png";
import PlayArrowIcon from '@mui/icons-material/PlayArrow';
import StopIcon from '@mui/icons-material/Stop';
import { Box } from '@mui/material';
import { useEffect, useMemo, useRef, useState } from 'react';

const MAP_VIEW = new MapView({repeat: true});
const ICON_MAPPING = {
    marker: {x: 0, y: 0, width: 128, height: 128, mask: false}
};

function ObservationAreaMap(props) {
    const {data, onLoad, viewState, onSelect} = props;

    const streamRest = useMemo(() => new StreamRest(), []);
    const wsClient = useRef(new WebSocketClient());
    const colorFunctions = useRef(new ColorFunctions());

    const [streams, setStreams] = useState({});
    const [markerList, setMarkerList] = useState({});

    useEffect(() => {
        streamRest.getAvailableStreams().then(response => {
            const streams = response.data;
            const colors = colorFunctions.current.generateDistinctColors(Object.keys(response.data).length);
            let streamsAndColors = {}
            streams.forEach((stream, index) => {
                streamsAndColors[stream] = colors[index];
            });
            setStreams(streamsAndColors);

            wsClient.current.setup(handleMessage, streams);
            wsClient.current.connect();
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
    
    function setupLiveLayers() {
        return Object.entries(streams).map(([stream, color]) =>
            createIconLayer(markerList[stream], stream, color)
        );
    }
    
    function createIconLayer(markerArray, streamId, color) {
        return new ScatterplotLayer({
            id: 'IconLayer-' + streamId,
            data: markerArray ?? [],

            getFillColor: d => color,
            getPosition: d => d.coordinates,
            getRadius: .7,
            radiusMinPixels: 7,
            radiusUnits: 'meters',
            iconAtlas: 'https://raw.githubusercontent.com/visgl/deck.gl-data/master/website/icon-atlas.png',
            iconMapping: 'https://raw.githubusercontent.com/visgl/deck.gl-data/master/website/icon-atlas.json',
            pickable: true,
        });
    }

    const layers = [
        new TileLayer({
            data: "https://cartodb-basemaps-a.global.ssl.fastly.net/light_all//{z}/{x}/{y}.png",
            minZoom: 0,
            maxZoom: 19,
            tileSize: 256,

            renderSubLayers: props => {
                const {
                    bbox: {west, south, east, north}
                } = props.tile;

                return new BitmapLayer(props, {
                    data: null,
                    image: props.data,
                    bounds: [west, south, east, north]
                });
            }
        }),
        new IconLayer({
            id: "icon-layer",
            data,
            pickable: true,
            iconAtlas: cameraicon,
            iconMapping: ICON_MAPPING,
            getIcon: d => "marker",

            sizeScale: 10,
            getPosition: d => {
                var c = [2]; 
                c[0] = d.centerlongitude ?? 91; 
                c[1] = d.centerlatitude ?? 181; 
                return c;
            },
            getSize: d => 5,
            getColor: d => [Math.sqrt(d.exits), 140, 0]
        }),
        ...setupLiveLayers()
    ];

    function getTooltip({object}) {
        return (
            object && {
                html: `\
                <div>
                    <img src='${window.location.pathname}api/image/as-file/${object.image.id}' width="auto" height="200rem" />
                </div>
                `
            }
        );
    }

    return (
        <>
            <DeckGL
                layers={layers}
                views={MAP_VIEW}
                initialViewState={viewState}
                controller={{dragRotate: false}}
                onLoad={onLoad}
                onClick={onSelect}
                getTooltip={getTooltip}
            >
            </DeckGL>
        </>
    );
}

export default ObservationAreaMap;

