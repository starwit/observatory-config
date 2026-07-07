import axios from "axios";

class RecordingRest {
    constructor() {
        this.baseUrl = window.location.pathname + "api/recorder";
    }

    getRecordingStreams() {
        return axios.get(this.baseUrl);
    }

    getAllAvailableStreams() {
        return axios.get(this.baseUrl + "/all");
    }

    startRecording(streamName) {
        return axios.post(this.baseUrl + "/start", null, { params: { streamName } });
    }

    stopRecording(streamName) {
        return axios.post(this.baseUrl + "/stop", null, { params: { streamName } });
    }

    stopAllRecordings() {
        return axios.post(this.baseUrl + "/stopall");
    }
}

export default RecordingRest;
