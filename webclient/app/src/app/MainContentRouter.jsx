import React from "react";
import {Route, Routes, useLocation} from "react-router-dom";
import Navigation from "./commons/navigation/Navigation";
import ImageAnnotate from "./features/observationArea/ImageAnnotate";
import ObservationAreaOverview from "./features/home/ObservationAreaOverview";
import ObservationAreaSingle from "./features/observationArea/ObservationAreaSingle";

function MainContentRouter() {
    // This needs to be here, because otherwise manual navigation (through `useNavigate()`) does not work properly
    useLocation()

    return (
        <Navigation>
            <Routes>
                <Route path={"/observationarea/:observationAreaId"} element={<ObservationAreaSingle />} />
                <Route path="/" element={<ObservationAreaOverview />} />
            </Routes>
        </Navigation>
    );
}

export default MainContentRouter;
