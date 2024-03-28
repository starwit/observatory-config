import React from "react";
import {Route, Routes, useLocation} from "react-router-dom";
import Navigation from "./commons/navigation/Navigation";
import ObservationAreaOverview from "./features/observationArea/ObservationAreaOverview";
import ObservationAreaDetail from "./features/observationArea/ObservationAreaDetail";

function MainContentRouter() {
    // This needs to be here, because otherwise manual navigation (through `useNavigate()`) does not work properly
    useLocation()

    return (
        <Navigation>
            <Routes>
                <Route path={"/observationarea/:observationAreaId"} element={<ObservationAreaDetail />} />
                <Route path="/" element={<ObservationAreaOverview />} />
            </Routes>
        </Navigation>
    );
}

export default MainContentRouter;
