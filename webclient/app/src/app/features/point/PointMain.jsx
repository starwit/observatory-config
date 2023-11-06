import React from "react";
import {Route, Routes} from "react-router-dom";
import PointOverview from "./PointOverview";
import PointDetail from "./PointDetail";

function PointMain() {
    return (
        <>
            <React.Fragment>
                <Routes>
                    <Route path="/" element={<PointOverview />}/>
                    <Route path="create" element={<PointDetail />}/>
                    <Route path="update/:id" element={<PointDetail />}/>
                </Routes>
            </React.Fragment>
        </>
    );
}

export default PointMain;
