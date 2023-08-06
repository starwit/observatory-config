import CrudRest from "./CrudRest";
import axios from "axios";

class PolygonRest extends CrudRest {
    constructor() {
        super(window.location.pathname + "api/polygon");
    }

    findAllWithoutImage(selected) {
        if (isNaN(selected)) {
            return axios.get(this.baseUrl + "/find-without-image");
        } else {
            return axios.get(this.baseUrl + "/find-without-other-image/" + selected);
        }
    }
}
export default PolygonRest;
