import CrudRest from "./CrudRest";
import axios from "axios";

class ObservationAreaRest extends CrudRest {
    constructor() {
        super(window.location.pathname + "api/observationarea");
    }

    savePolygons(selected, entityList) {
        return axios.post(this.baseUrl + "/save-polygons/" + selected, entityList);
    }

    copyPolygons(targetAreaId, srcAreaId) {
        return axios.post(this.baseUrl + "/copy-polygons/src/" + srcAreaId + "/target/" + targetAreaId);
    }

    exportAreas(ids) {
        return axios.get(this.baseUrl + "/export", {params: {ids: ids.join(",")}});
    }

    updateProcessingStatus(id, processingEnabledStatus) {
        return axios.post(this.baseUrl + "/update-processing-status/" + id + "/" +processingEnabledStatus);
    }
}
export default ObservationAreaRest;
