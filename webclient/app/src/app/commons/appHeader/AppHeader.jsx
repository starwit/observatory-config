import React from "react";
// Material UI Components
import logo from "../../assets/images/logo-white.png";
import {AppBar, Button, IconButton, Toolbar, Typography} from "@mui/material";
import HeaderStyles from "./../../assets/styles/HeaderStyles";
import {useNavigate} from "react-router-dom";
import {useTranslation} from "react-i18next";
import {Logout} from "@mui/icons-material";
import {styled} from "@mui/material/styles";

function AppHeader(props) {
    const {menuItems, title} = props;
    const navigate = useNavigate();
    const {t} = useTranslation();

    const LirejarpLogo = styled('img')(({theme}) => HeaderStyles.menuLogoImg(theme))
    const Spacer = styled('div')(({theme}) => HeaderStyles.spacer(theme))
    const ContentSpacer = styled('div')(({theme}) => HeaderStyles.contentSpacer(theme))

    return (
        <>
            <AppBar position="fixed" sx={HeaderStyles.appBar}>
                <Toolbar sx={HeaderStyles.toolbar}>
                    <LirejarpLogo src={logo} alt="Logo of lirejarp"/>
                    <Typography sx={HeaderStyles.menuTitle} variant="h2" noWrap>
                        {title}
                    </Typography>
                    <Spacer/>
                    {menuItems.map(item => (
                        <Button key={item.title} color="inherit" disableRipple sx={HeaderStyles.linkButton}
                            onClick={() => navigate(item.link)}>{t(item.title)}</Button>
                    ))}
                    <IconButton color="inherit" disableRipple sx={HeaderStyles.linkButton}
                        onClick={() => navigate.push("/logout")}><Logout/></IconButton>
                </Toolbar>
            </AppBar>
            <ContentSpacer/>
        </>
    );
}

export default AppHeader;