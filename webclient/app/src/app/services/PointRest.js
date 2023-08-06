import CrudRest from "./CrudRest";
import axios from "axios";

class PointRest extends CrudRest {
    constructor() {
        super(window.location.pathname + "api/point");
    }

    findAllWithoutPolygon(selected) {
        if (isNaN(selected)) {
            return axios.get(this.baseUrl + "/find-without-polygon");
        } else {
            return axios.get(this.baseUrl + "/find-without-other-polygon/" + selected);
        }
    }
}
export default PointRest;
