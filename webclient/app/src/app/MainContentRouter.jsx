import React, { useEffect } from "react";
import {Route, Routes} from "react-router-dom";
import ImageAnnotate from "./features/imageAnnotate/ImageAnnotate";
import {useTranslation} from "react-i18next";
import Navigation from "./commons/navigation/Navigation";
import logo from "./assets/images/logo-white.png";
import {useLocation, useNavigate} from "react-router-dom";

function MainContentRouter() {

    const {t} = useTranslation();
    const location = useLocation();

    return (
        <>
            <Navigation
  
            >
                <Routes>
                    <Route path={"/:imageId"} element={<ImageAnnotate />}/>
 
                </Routes>
            </Navigation>
        </>
    );
}

export default MainContentRouter;
