import React from "react";
import {Route, Routes, useLocation} from "react-router-dom";
import Navigation from "./commons/navigation/Navigation";
import ImageAnnotate from "./features/imageAnnotate/ImageAnnotate";

function MainContentRouter() {
    // I have absolutely no idea why, but if you remove this, `navigate()` in `ParkingAreaSelect` breaks...
    useLocation();

    return (
        <Navigation>
            <Routes>
                <Route path="/:parkingAreaId" element={<ImageAnnotate />} />
                <Route path="/" element={<ImageAnnotate />} />
            </Routes>
        </Navigation>
    );
}

export default MainContentRouter;
