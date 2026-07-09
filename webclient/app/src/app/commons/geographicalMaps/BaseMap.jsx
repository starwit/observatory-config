import {MapboxOverlay} from '@deck.gl/mapbox';
import 'maplibre-gl/dist/maplibre-gl.css';
import {Map, useControl} from 'react-map-gl/maplibre';
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
    } = props;

    return (
        <Box sx={{width: "100vw", height: "100vh", position: "fixed", top: 0, left: 0,
            "& .maplibregl-canvas:focus": { outline: "none" },
        }}>
            <Map
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
