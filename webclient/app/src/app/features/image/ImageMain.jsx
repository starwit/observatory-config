import React from "react";
import {Route, Routes} from "react-router-dom";
import ImageOverview from "./ImageOverview";
import ImageDetail from "./ImageDetail";

function ImageMain() {
    return (
        <>
            <React.Fragment>
                <Routes>
                    <Route path="/" element={<ImageOverview />}/>
                    <Route path="create" element={<ImageDetail />}/>
                    <Route path="update/:id" element={<ImageDetail />}/>
                </Routes>
            </React.Fragment>
        </>
    );
}

export default ImageMain;
