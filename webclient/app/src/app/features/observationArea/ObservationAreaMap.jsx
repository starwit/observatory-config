import {BitmapLayer, IconLayer, ScatterplotLayer, SolidPolygonLayer} from "@deck.gl/layers";
import {TileLayer} from "@deck.gl/geo-layers";
import {Box} from "@mui/material";
import MessageRest from "../../services/MessageRest";
import WebSocketClient from "../../services/WebSocketClient";
import cameraicon from "./../../assets/images/camera3.png";
import {useEffect, useMemo, useRef, useState} from 'react';
import DeckGL from "@deck.gl/react";
import MapMenuLayout from '../../commons/mapMenu/MapMenuLayout';
import ObservationMapMenu from '../../commons/mapMenu/ObservationMapMenu';
import {MapView} from "@deck.gl/core";
import ObjectClassFilter from "../../commons/objectClassFilter/ObjectClassFilter";
import {classColor, isClassVisible} from "../../commons/objectClassFilter/ObjectClasses";

const MAP_VIEW = new MapView({repeat: true});
const WORLD_BOUNDS = [[-180, -85], [180, -85], [180, 85], [-180, 85]];
const SEMI_TRANSPARENT_OVERLAY_COLOR = [230, 230, 230, 50];

const ICON_MAPPING = {
    marker: {x: 0, y: 0, width: 128, height: 128, mask: false}
};

function ObservationAreaMap(props) {
    const {data, onLoad, viewState, onSelect, showLive, onToggleLive} = props;

    const messageRest = useMemo(() => new MessageRest(), []);
    const wsClient = useRef(new WebSocketClient());

    const [streams, setStreams] = useState([]);
    const [markerList, setMarkerList] = useState({});
    const [selectedClassIds, setSelectedClassIds] = useState([]);
    useEffect(() => {
        messageRest.getAvailableStreams().then(response => {
            const streams = response.data ?? [];
            setStreams(streams);

            wsClient.current.setup(handleMessage, streams);
        });
        return () => wsClient.current.disconnect();
    }, []);

    useEffect(() => {
        if (showLive) {
            wsClient.current.connect();
        } else {
            wsClient.current.disconnect();

        }
    }, [showLive]);

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
        setMarkerList(prevMarkerList => ({...prevMarkerList, [streamId]: newMarkers}));
    }

    function setupLiveLayers() {
        return streams.map(stream => createScatterPlotLayer(markerList[stream], stream));
    }

    function createScatterPlotLayer(markerArray, streamId) {
        return new ScatterplotLayer({
            id: 'IconLayer-' + streamId,
            data: (markerArray ?? []).filter(marker => isClassVisible(marker.class, selectedClassIds)),

            getFillColor: d => classColor(d.class),
            getPosition: d => d.coordinates,
            getRadius: .7,
            radiusMinPixels: 7,
            radiusUnits: 'meters',
            pickable: true,
        });
    }

    const layers = [
        new TileLayer({
            data: "https://c.tile.openstreetmap.org/{z}/{x}/{y}.png",
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
        new SolidPolygonLayer({
            id: 'semi-transparent-overlay',
            data: [{polygon: WORLD_BOUNDS}],
            getPolygon: d => d.polygon,
            getFillColor: SEMI_TRANSPARENT_OVERLAY_COLOR,
            stroked: false,
            pickable: false,
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
            object?.image && {
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
            <MapMenuLayout>
                <ObservationMapMenu setToggleLiveTracking={onToggleLive} showLive={showLive} />
            </MapMenuLayout>
            {showLive && (
                <Box sx={{
                    position: 'fixed',
                    top: 60,
                    right: 'calc(2% + 3.5rem)',
                    zIndex: 1,
                    backgroundColor: 'background.paper',
                    borderRadius: 1,
                    boxShadow: 1
                }}>
                    <ObjectClassFilter
                        selectedClassIds={selectedClassIds}
                        onChange={setSelectedClassIds}
                    />
                </Box>
            )}
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

