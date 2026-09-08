import axios from "axios";

class MessageRest {
    constructor() {
        this.baseUrl = window.location.pathname + "api/message";
    }

    getAvailableStreams = () => {
        return axios.get(this.baseUrl + "/streams");
    };
}

export default MessageRest;