import axios from "axios";

class StreamRest {
    constructor() {
        this.baseUrl = window.location.pathname + "api/message";
    }

    getAvailableStreams = () => {
        return axios.get(this.baseUrl + "/streams");
    };
}
export default StreamRest;