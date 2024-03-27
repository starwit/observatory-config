import React from "react";
import {Route, Routes, useLocation} from "react-router-dom";
import Navigation from "./commons/navigation/Navigation";
import ImageAnnotate from "./features/imageAnnotate/ImageAnnotate";
import ObservationAreaOverview from "./features/observationArea/ObservationAreaOverview";

function MainContentRouter() {
    // I have absolutely no idea why, but if you remove this, `navigate()` in `ObservationAreaSelect` breaks...
    useLocation();

    return (
        <Navigation>
            <Routes>
                <Route path={"/observationarea/:observationAreaId"} element={<ImageAnnotate />} />
                <Route path="/" element={<ObservationAreaOverview />} />
            </Routes>
        </Navigation>
    );
}

export default MainContentRouter;
