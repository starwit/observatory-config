import {MapView} from '@deck.gl/core';
import {TileLayer} from "@deck.gl/geo-layers";
import {BitmapLayer, IconLayer} from "@deck.gl/layers";
import DeckGL from "@deck.gl/react";
import cameraicon from "./../../assets/images/camera3.png";

const MAP_VIEW = new MapView({repeat: true});
const ICON_MAPPING = {
    marker: {x: 0, y: 0, width: 128, height: 128, mask: false}
};

function ObservationAreaMap(props) {
    const {data, onLoad, viewState, onSelect} = props;

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
        })
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

