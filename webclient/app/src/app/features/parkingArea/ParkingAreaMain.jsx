import React from "react";
import {Route, Routes} from "react-router-dom";
import ParkingAreaOverview from "./ParkingAreaOverview";
import ParkingAreaDetail from "./ParkingAreaDetail";

function ParkingAreaMain() {
    return (
        <>
            <React.Fragment>
                <Routes>
                    <Route path="/" element={<ParkingAreaOverview />}/>
                    <Route path="create" element={<ParkingAreaDetail />}/>
                    <Route path="update/:id" element={<ParkingAreaDetail />}/>
                </Routes>
            </React.Fragment>
        </>
    );
}

export default ParkingAreaMain;
