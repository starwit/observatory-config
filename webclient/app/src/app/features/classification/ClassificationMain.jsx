import React from "react";
import {Route} from "react-router-dom";
import ClassificationOverview from "./ClassificationOverview";
import ClassificationDetail from "./ClassificationDetail";

function ClassificationMain() {
    return (
        <>
            <React.Fragment>
                <Route exact path="/classification" component={ClassificationOverview}/>
                <Route exact path="/classification/create" component={ClassificationDetail}/>
                <Route exact path="/classification/update/:id" component={ClassificationDetail}/>
            </React.Fragment>
        </>
    );
}

export default ClassificationMain;
