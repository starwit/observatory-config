import axios from "axios";
import CrudRest from "./CrudRest";

class DetectionRest extends CrudRest {
    constructor() {
        super(window.location.pathname + "api/detection");
    }

    findTrajectories(start, end, streamId) {
        return axios.post(this.baseUrl + "/trajectories", {
            start: start.toISOString(),
            end: end.toISOString(),
            streamId
        });
    }

    getObjectCountHistogram(streamId, buckets = 200) {
        return axios.get(this.baseUrl + "/object-count-histogram", { params: { streamId, buckets } });
    }
}

export default DetectionRest;
