import CrudRest from "./CrudRest";
import axios from "axios";

class ParkingAreaRest extends CrudRest {
    constructor() {
        super(window.location.pathname + "api/parkingarea");
    }
}
export default ParkingAreaRest;
