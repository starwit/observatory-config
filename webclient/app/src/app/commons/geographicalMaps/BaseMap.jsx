import {MapboxOverlay} from '@deck.gl/mapbox';
import * as maplibregl from 'maplibre-gl';
import 'maplibre-gl/dist/maplibre-gl.css';
import {Map, useControl} from '@vis.gl/react-maplibre';
import {Box} from '@mui/material';

function DeckGLOverlay(props) {
    const overlay = useControl(() => new MapboxOverlay(props));
    overlay.setProps(props);
    return null;
}

function BaseMap(props) {
    const {
        viewState,
        onViewStateChange = null,
        layers = [],
        getTooltip = null,
        onClick = null,
        onLoad = null,
        topOffset = 0,
    } = props;

    return (
        <Box sx={{
            width: "100vw",
            height: topOffset ? `calc(100vh - ${topOffset})` : "100vh",
            position: "fixed",
            top: topOffset || 0,
            left: 0,
            "& .maplibregl-canvas:focus": {outline: "none"},
        }}>
            <Map
                mapLib={maplibregl}
                initialViewState={viewState}
                mapStyle="https://tiles.openfreemap.org/styles/positron"
                onMove={onViewStateChange ? evt => onViewStateChange(evt.viewState) : undefined}
                onLoad={onLoad}
            >
                <DeckGLOverlay
                    layers={layers}
                    getTooltip={getTooltip}
                    onClick={onClick}
                />
            </Map>
        </Box>
    );
}

export default BaseMap;
