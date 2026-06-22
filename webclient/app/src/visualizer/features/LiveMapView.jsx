import { MapView } from '@deck.gl/core';
import { TileLayer } from "@deck.gl/geo-layers";
import { BitmapLayer, IconLayer, ScatterplotLayer } from "@deck.gl/layers";
import { DeckGL } from "@deck.gl/react";

function LiveMapView(props) {
    const { markerList, streams, initialViewState } = props;

    const MAP_VIEW = new MapView({ repeat: true });

    const layers = setupLayers();

    function setupLayers() {
        let layers = [
            createBaseMapLayer()
        ];

        for (let stream in streams) {
            let color = streams[stream];
            layers.push(createIconLayer(markerList[stream], stream, color));
        }
        return layers;
    }   

    function createBaseMapLayer() {
        return new TileLayer({
            data: "https://c.tile.openstreetmap.org/{z}/{x}/{y}.png",
            minZoom: 0,     // Minimum zoom level
            maxZoom: 19,    // Maximum zoom level
            tileSize: 256,  // Size of each map tile

            renderSubLayers: props => {
                // Get geographical boundaries of the current tile
                const {
                    bbox: { west, south, east, north }
                } = props.tile;

                return new BitmapLayer(props, {
                    data: null,
                    image: props.data,
                    bounds: [west, south, east, north]
                });
            }
        })
    }

    function createIconLayer(markerArray, streamId, color) {
        return new ScatterplotLayer({
            id: 'IconLayer-' + streamId,
            data: markerArray,

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

    return (
        <DeckGL
            layers={layers}               // Add map layers
            views={MAP_VIEW}              // Add map view settings
            initialViewState={initialViewState}  // Set initial position
            controller={{ dragRotate: false }}       // Disable rotation
        />
    );
}

export default LiveMapView;