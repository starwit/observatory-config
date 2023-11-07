import CrudRest from "./CrudRest";
import axios from "axios";

class ParkingAreaRest extends CrudRest {
    constructor() {
        super(window.location.pathname + "api/parkingarea");
    }

    findAllWithoutSelectedTestConfig(selected) {
        if (isNaN(selected)) {
            return axios.get(this.baseUrl + "/find-without-selectedTestConfig");
        } else {
            return axios.get(this.baseUrl + "/find-without-other-selectedTestConfig/" + selected);
        }
    }

    findAllWithoutSelectedProdConfig(selected) {
        if (isNaN(selected)) {
            return axios.get(this.baseUrl + "/find-without-selectedProdConfig");
        } else {
            return axios.get(this.baseUrl + "/find-without-other-selectedProdConfig/" + selected);
        }
    }
}
export default ParkingAreaRest;
