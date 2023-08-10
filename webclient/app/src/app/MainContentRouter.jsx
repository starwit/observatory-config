import React from "react";
import {useTranslation} from "react-i18next";
import {Route, Switch} from "react-router-dom";
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
                <Route path={"/config"} component={ImageAnnotateMain} />
                <Route path={"/point"} component={PointMain} />
                <Route path={"/polygon"} component={PolygonMain} />
                <Route path={"/image"} component={ImageMain} />
                <Route path={"/classification"} component={ClassificationMain} />
                <Route path={"/parkingconfig"} component={ParkingConfigMain} />
                <Route path={"/parkingarea"} component={ParkingAreaMain} />

            </Switch>
        </>
    );
}

export default MainContentRouter;
