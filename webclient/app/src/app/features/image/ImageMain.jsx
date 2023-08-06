import React from "react";
import {Route} from "react-router-dom";
import ImageOverview from "./ImageOverview";
import ImageDetail from "./ImageDetail";

function ImageMain() {
    return (
        <>
            <React.Fragment>
                <Route exact path="/image" component={ImageOverview}/>
                <Route exact path="/image/create" component={ImageDetail}/>
                <Route exact path="/image/update/:id" component={ImageDetail}/>
            </React.Fragment>
        </>
    );
}

export default ImageMain;
