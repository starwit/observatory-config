import CrudRest from "./CrudRest";
import axios from "axios";

class ObservationAreaRest extends CrudRest {
    constructor() {
        super(window.location.pathname + "api/observationarea");
    }
}
export default ObservationAreaRest;
