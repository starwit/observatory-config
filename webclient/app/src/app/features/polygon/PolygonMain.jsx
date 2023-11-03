import React from "react";
import {Route, Routes} from "react-router-dom";
import PolygonOverview from "./PolygonOverview";
import PolygonDetail from "./PolygonDetail";

function PolygonMain() {
    return (
        <>
            <React.Fragment>
                <Routes>
                    <Route path="/" element={<PolygonOverview />}/>
                    <Route path="create" element={<PolygonDetail />}/>
                    <Route path="update/:id" element={<PolygonDetail />}/>
                </Routes>
            </React.Fragment>
        </>
    );
}

export default PolygonMain;
