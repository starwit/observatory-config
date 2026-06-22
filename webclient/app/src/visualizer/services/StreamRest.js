import axios from "axios";

class StreamRest  {
    constructor() {
        this.baseUrl = window.location.pathname + "api/messages";
    }

    getAvailableStreams = () => {
        return axios.get(this.baseUrl + "/streams");
    };
}
export default StreamRest;