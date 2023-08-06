import React from "react";
import {Route} from "react-router-dom";
import ParkingConfigOverview from "./ParkingConfigOverview";
import ParkingConfigDetail from "./ParkingConfigDetail";

function ParkingConfigMain() {
    return (
        <>
            <React.Fragment>
                <Route exact path="/parkingconfig" component={ParkingConfigOverview}/>
                <Route exact path="/parkingconfig/create" component={ParkingConfigDetail}/>
                <Route exact path="/parkingconfig/update/:id" component={ParkingConfigDetail}/>
            </React.Fragment>
        </>
    );
}

export default ParkingConfigMain;
