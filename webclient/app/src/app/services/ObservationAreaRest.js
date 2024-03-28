import CrudRest from "./CrudRest";
import axios from "axios";

class ObservationAreaRest extends CrudRest {
    constructor() {
        super(window.location.pathname + "api/observationarea");
    }

    savePolygons(selected, entityList) {
        return axios.post(this.baseUrl + "/save-polygons/" + selected, entityList);
    }
}
export default ObservationAreaRest;
