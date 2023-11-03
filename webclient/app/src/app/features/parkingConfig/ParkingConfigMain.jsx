import React from "react";
import {Route, Routes} from "react-router-dom";
import ParkingConfigOverview from "./ParkingConfigOverview";
import ParkingConfigDetail from "./ParkingConfigDetail";

function ParkingConfigMain() {
    return (
        <>
            <React.Fragment>
                <Routes>
                    <Route path="/" element={<ParkingConfigOverview />}/>
                    <Route path="create" element={<ParkingConfigDetail />}/>
                    <Route path="update/:id" element={<ParkingConfigDetail />}/>
                </Routes>
            </React.Fragment>
        </>
    );
}

export default ParkingConfigMain;
