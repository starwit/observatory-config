import React from "react";
import {Route} from "react-router-dom";
import PointOverview from "./PointOverview";
import PointDetail from "./PointDetail";

function PointMain() {
    return (
        <>
            <React.Fragment>
                <Route exact path="/point" component={PointOverview}/>
                <Route exact path="/point/create" component={PointDetail}/>
                <Route exact path="/point/update/:id" component={PointDetail}/>
            </React.Fragment>
        </>
    );
}

export default PointMain;
