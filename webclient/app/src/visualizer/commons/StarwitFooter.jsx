import {
    AppBar,
    Container,
    Toolbar,
    Typography,
} from "@mui/material";

import React from "react";
import {useTranslation} from 'react-i18next';
import general from "../assets/images/general_LogoFooter.png";

function AlertFooter() {

    const DynamicLogo = general;
    const {t} = useTranslation();
    return (
        <Container color="secondary">
            <AppBar color="secondary" sx={{position: "fixed", top: "auto", bottom: 0}}>
                <Toolbar sx={{justifyContent: "center"}}>
                    <img src={DynamicLogo} height={30} alt="" />
                    <Typography sx={{marginLeft: 1}}>{t('home.copyright')}</Typography>
                </Toolbar >
            </AppBar>
        </Container >
    );
}

export default AlertFooter;
