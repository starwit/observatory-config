import CrudRest from "./CrudRest";
import axios from "axios";

class ParkingConfigRest extends CrudRest {
    constructor() {
        super(window.location.pathname + "api/parkingconfig");
    }

    findAllWithoutParkingArea(selected) {
        if (isNaN(selected)) {
            return axios.get(this.baseUrl + "/find-without-parkingArea");
        } else {
            return axios.get(this.baseUrl + "/find-without-other-parkingArea/" + selected);
        }
    }

    savePolygons = entity => {
        return axios.post(this.baseUrl + "/save-polygons", entity);
    };
}
export default ParkingConfigRest;
