import React from "react";
import {Route} from "react-router-dom";
import ImageAnnotate from "./ImageAnnotate";

function ImageAnnotateMain() {
    return (
        <>
            <React.Fragment>
                <Route exact path="/config" component={ImageAnnotate} />
            </React.Fragment>
        </>
    );
}
export default ImageAnnotateMain;
