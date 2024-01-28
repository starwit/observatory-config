import CrudRest from "./CrudRest";
import axios from "axios";

class ImageRest extends CrudRest {
    constructor() {
        super(window.location.pathname + "api/image");
    }

    findAllWithoutParkingConfig(selected) {
        if (isNaN(selected)) {
            return axios.get(this.baseUrl + "/find-without-parkingConfig");
        } else {
            return axios.get(this.baseUrl + "/find-without-other-parkingConfig/" + selected);
        }
    }

    findWithPolygons(selected) {
        return axios.get(this.baseUrl + "/find-with-polygons/" + selected);
    }

    savePolygons(entityList) {
        return axios.post(this.baseUrl + "/save-polygons", entityList);
    }

    upload(data, id) {
        const config = {headers: {"Content-Type": "multipart/form-data"}};

        return axios.post(this.baseUrl + "/upload/" + id, data, config);
    }
}
export default ImageRest;
