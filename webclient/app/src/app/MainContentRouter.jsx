import React from "react";
import {Route, Switch, useLocation} from "react-router-dom";
import ParkingConfigMain from "./features/parkingConfig/ParkingConfigMain";
import ParkingAreaMain from "./features/parkingArea/ParkingAreaMain";
import {appItems} from "./AppConfig";
import Navigation from "./commons/navigation/Navigation";
import logo from "./assets/images/logo-white.png";
import {useTranslation} from "react-i18next";
import ImageAnnotate from "./features/imageAnnotate/ImageAnnotate";

function MainContentRouter() {
    const {t} = useTranslation();
    const location = useLocation();

    return (
        <>
            <Switch>
                <Navigation
                    menuItems={appItems}
                    title={t("app.baseName")}
                    logo={logo}
                    removeContentSpacer={location.pathname === "/"}
                >
                    <Route exact path={"/"} component={ImageAnnotate}/>
                    <Route path={"/parkingconfig"} component={ParkingConfigMain}/>
                    <Route path={"/parkingarea"} component={ParkingAreaMain}/>
                </Navigation>
            </Switch>
        </>
    );
}

export default MainContentRouter;
