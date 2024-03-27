import CrudRest from "./CrudRest";
import axios from "axios";

class PolygonRest extends CrudRest {
    constructor() {
        super(window.location.pathname + "api/polygon");
    }
}
export default PolygonRest;
