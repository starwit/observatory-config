import React from "react";
import {useTranslation} from "react-i18next";
import {Route, Switch} from "react-router-dom";
import {appItems} from "./AppConfig";
import logo from "./assets/images/logo-white.png";
import Navigation from "./commons/navigation/Navigation";
import ClassificationMain from "./features/classification/ClassificationMain";
import ImageMain from "./features/image/ImageMain";
import ParkingAreaMain from "./features/parkingArea/ParkingAreaMain";
import ParkingConfigMain from "./features/parkingConfig/ParkingConfigMain";
import PointMain from "./features/point/PointMain";
import PolygonMain from "./features/polygon/PolygonMain";
import ImageAnnotateMain from "./features/imageAnnotate/ImageAnnotateMain";

function MainContentRouter() {
    const {t} = useTranslation();

    return (
        <>
            <Switch>
                <Navigation menuItems={appItems} title={t("app.baseName")} logo={logo}>
                    <Route exact path={"/config"} component={ImageAnnotateMain} />
                    <Route path={"/point"} component={PointMain} />
                    <Route path={"/polygon"} component={PolygonMain} />
                    <Route path={"/image"} component={ImageMain} />
                    <Route path={"/classification"} component={ClassificationMain} />
                    <Route path={"/parkingconfig"} component={ParkingConfigMain} />
                    <Route path={"/parkingarea"} component={ParkingAreaMain} />
                </Navigation>
            </Switch>
        </>
    );
}

export default MainContentRouter;
