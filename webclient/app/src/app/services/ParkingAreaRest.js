import CrudRest from "./CrudRest";

class ParkingAreaRest extends CrudRest {
    constructor() {
        super(window.location.pathname + "api/parkingarea");
    }
}
export default ParkingAreaRest;
