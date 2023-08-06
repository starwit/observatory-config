import React from "react";
import {Route} from "react-router-dom";
import PolygonOverview from "./PolygonOverview";
import PolygonDetail from "./PolygonDetail";

function PolygonMain() {
    return (
        <>
            <React.Fragment>
                <Route exact path="/polygon" component={PolygonOverview}/>
                <Route exact path="/polygon/create" component={PolygonDetail}/>
                <Route exact path="/polygon/update/:id" component={PolygonDetail}/>
            </React.Fragment>
        </>
    );
}

export default PolygonMain;
