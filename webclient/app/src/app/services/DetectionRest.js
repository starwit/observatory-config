import axios from "axios";
import CrudRest from "./CrudRest";

class DetectionRest extends CrudRest {
    constructor() {
        super(window.location.pathname + "api/detection");
    }

    findTrajectories(timestamp, windowSize, streamId) {
        return axios.post(this.baseUrl + "/trajectories", { timestamp, windowSize, streamId });
    }
}

export default DetectionRest;
