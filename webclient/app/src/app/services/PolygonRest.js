import CrudRest from "./CrudRest";
import axios from "axios";

class PolygonRest extends CrudRest {
    constructor() {
        super(window.location.pathname + "api/polygon");
    }

    findAllWithoutObservationArea(selected) {
        if (isNaN(selected)) {
            return axios.get(this.baseUrl + "/find-without-observationarea");
        } else {
            return axios.get(this.baseUrl + "/find-without-other-observationarea/" + selected);
        }
    }
}
export default PolygonRest;
