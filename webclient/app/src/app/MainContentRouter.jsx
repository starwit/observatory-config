import React from "react";
import {Route, Routes} from "react-router-dom";
import ImageAnnotate from "./features/imageAnnotate/ImageAnnotate";
import Navigation from "./commons/navigation/Navigation";
import {useLocation} from "react-router-dom";

function MainContentRouter() {

    // I have absolutely no idea why, but if you remove this, `navigate()` in `ParkingAreaSelect` breaks...
    useLocation();

    return (
        <>
            <Navigation>
                <Routes>
                    <Route path={"/:imageId"} element={<ImageAnnotate />}/>
                </Routes>
            </Navigation>
        </>
    );
}

export default MainContentRouter;
