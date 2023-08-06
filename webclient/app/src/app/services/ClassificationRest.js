import CrudRest from "./CrudRest";

class ClassificationRest extends CrudRest {
    constructor() {
        super(window.location.pathname + "api/classification");
    }
}
export default ClassificationRest;
