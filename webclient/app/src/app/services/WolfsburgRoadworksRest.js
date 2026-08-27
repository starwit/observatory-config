import {BitmapLayer} from "@deck.gl/layers";
import {TileLayer} from "@deck.gl/geo-layers";

const WOLFSBURG_ROADWORKS_WMS_URL = `${window.location.pathname}api/wolfsburg-roadworks/wms`;
const WOLFSBURG_ROADWORKS_LAYER = "baustellen";
const WOLFSBURG_ROADWORKS_BOUNDS = [10.612709, 52.282052, 10.96071, 52.535675];

function buildWmsUrl({west, south, east, north}) {
    const params = new URLSearchParams({
        SERVICE: "WMS",
        VERSION: "1.1.1",
        REQUEST: "GetMap",
        SRS: "EPSG:4326",
        BBOX: `${west},${south},${east},${north}`,
        WIDTH: "256",
        HEIGHT: "256",
        LAYERS: WOLFSBURG_ROADWORKS_LAYER,
        STYLES: "",
        FORMAT: "image/png",
        TRANSPARENT: "true"
    });

    return `${WOLFSBURG_ROADWORKS_WMS_URL}?${params.toString()}`;
}

function loadTileImage({bbox}) {
    const image = new Image();
    image.crossOrigin = "anonymous";
    image.src = buildWmsUrl({west: bbox.west, south: bbox.south, east: bbox.east, north: bbox.north});

    return new Promise((resolve, reject) => {
        image.onload = () => resolve(image);
        image.onerror = reject;
    });
}

class WolfsburgRoadworksRest {
    createLayer(showRoadworks) {
        if (!showRoadworks) {
            return null;
        }

        return new TileLayer({
            id: "wolfsburg-roadworks-wms",
            data: null,
            tileSize: 256,
            minZoom: 10,
            maxZoom: 22,
            extent: WOLFSBURG_ROADWORKS_BOUNDS,
            pickable: false,
            getTileData: loadTileImage,
            renderSubLayers: props => {
                const {west, south, east, north} = props.tile.bbox;

                return new BitmapLayer(props, {
                    image: props.data,
                    bounds: [west, south, east, north],
                    transparent: true
                });
            }
        });
    }
}

export default WolfsburgRoadworksRest;