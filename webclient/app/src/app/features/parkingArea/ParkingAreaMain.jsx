import React from "react";
import {Route} from "react-router-dom";
import ParkingAreaOverview from "./ParkingAreaOverview";
import ParkingAreaDetail from "./ParkingAreaDetail";

function ParkingAreaMain() {
    return (
        <>
            <React.Fragment>
                <Route exact path="/parkingarea" component={ParkingAreaOverview}/>
                <Route exact path="/parkingarea/create" component={ParkingAreaDetail}/>
                <Route exact path="/parkingarea/update/:id" component={ParkingAreaDetail}/>
            </React.Fragment>
        </>
    );
}

export default ParkingAreaMain;
