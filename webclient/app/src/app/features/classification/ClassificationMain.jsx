import React from "react";
import {Route, Routes} from "react-router-dom";
import ClassificationOverview from "./ClassificationOverview";
import ClassificationDetail from "./ClassificationDetail";

function ClassificationMain() {
    return (
        <>
            <React.Fragment>
                <Routes>
                    <Route path="/" element={<ClassificationOverview />}/>
                    <Route path="create" element={<ClassificationDetail />}/>
                    <Route path="update/:id" element={<ClassificationDetail />}/>
                </Routes>
            </React.Fragment>
        </>
    );
}

export default ClassificationMain;
