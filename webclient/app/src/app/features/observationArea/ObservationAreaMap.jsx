import {IconLayer, ScatterplotLayer} from "@deck.gl/layers";
import ColorFunctions from "../../services/ColorFunctions";
import StreamRest from "../../services/StreamRest";
import WebSocketClient from "../../services/WebSocketClient";
import cameraicon from "./../../assets/images/camera3.png";
import { useEffect, useMemo, useRef, useState } from 'react';
import BaseMap from '../../commons/geographicalMaps/BaseMap';
import MapMenuLayout from '../../commons/mapMenu/MapMenuLayout';
import ObservationMapMenu from '../../commons/mapMenu/ObservationMapMenu';

const ICON_MAPPING = {
    marker: {x: 0, y: 0, width: 128, height: 128, mask: false}
};

function ObservationAreaMap(props) {
    const {data, onLoad, viewState, onViewStateChange, onSelect, showLive, onToggleLive} = props;

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
        });
        return () => wsClient.current.disconnect();
    }, []);

    useEffect(() => {
        if(showLive) {
            wsClient.current.connect();
        } else {
            wsClient.current.disconnect();

        }
    },[showLive]);

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
            createScatterPlotLayer(markerList[stream], stream, color)
        );
    }
    
    function createScatterPlotLayer(markerArray, streamId, color) {
        return new ScatterplotLayer({
            id: 'IconLayer-' + streamId,
            data: markerArray ?? [],

            getFillColor: d => color,
            getPosition: d => d.coordinates,
            getRadius: .7,
            radiusMinPixels: 7,
            radiusUnits: 'meters',
            pickable: true,
        });
    }

    const layers = [
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
            <BaseMap
                layers={layers}
                viewState={viewState}
                onViewStateChange={onViewStateChange}
                getTooltip={getTooltip}
                onClick={onSelect}
                onLoad={onLoad}
            />
        </>
    );
}

export default ObservationAreaMap;

