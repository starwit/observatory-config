import React from "react";
import {
    Box,
    CssBaseline,
    Drawer,
    IconButton,
    List,
    ListItem,
    ListItemButton,
    ListItemText,
    Toolbar,
    Typography
} from "@mui/material";
import {useTheme} from "@mui/material/styles";
import {Logout} from "@mui/icons-material";
import {HeaderStyles, AppBar, DrawerHeader} from "../../../assets/styles/HeaderStyles";
import {useTranslation} from "react-i18next";
import {useHistory} from "react-router-dom";
import Divider from "@mui/material/Divider";
import MenuIcon from "@mui/icons-material/Menu";
import ChevronLeftIcon from "@mui/icons-material/ChevronLeft";
import ChevronRightIcon from "@mui/icons-material/ChevronRight";
import ParkingAreaSelect from "./ParkingAreaSelect";

function SidebarNavigation(props) {
    const headerStyles = HeaderStyles();
    const drawerWidth = 240;
    const {t} = useTranslation();
    const history = useHistory();

    const theme = useTheme();
    const [open, setOpen] = React.useState(false);

    const handleDrawerOpen = () => {
        setOpen(true);
    };

    const handleDrawerClose = () => {
        setOpen(false);
    };

    return (
        <Box sx={{display: "flex"}}>
            <CssBaseline />
            <AppBar position="fixed" sx={{zIndex: theme => theme.zIndex.drawer + 1}}>
                <Toolbar className={headerStyles.toolbar}>
                    <IconButton
                        color="inherit"
                        aria-label="open drawer"
                        onClick={handleDrawerOpen}
                        edge="start"
                        sx={{mr: 2, ...(open && {display: "none"})}}
                    >
                        <MenuIcon />
                    </IconButton>
                    <img className={headerStyles.menuLogoImg} src={props.logo} alt="Logo of lirejarp" />
                    <Typography variant="h6" noWrap>
                        {props.title}
                    </Typography>
                    <div className={headerStyles.spacer} />
                    <IconButton color="secondary" disableRipple className={headerStyles.linkButton}
                        onClick={() => history.push("/logout")}><Logout /></IconButton>
                </Toolbar>
            </AppBar>
            <Drawer
                sx={{
                    "& .MuiDrawer-paper": {
                        width: drawerWidth,
                        boxSizing: "border-box"
                    }
                }}
                variant="persistent"
                anchor="left"
                open={open}
            >
                <Toolbar />
                <DrawerHeader>
                    <IconButton onClick={handleDrawerClose}>
                        {theme.direction === "ltr" ? <ChevronLeftIcon /> : <ChevronRightIcon />}
                    </IconButton>
                </DrawerHeader>
                <Divider />
                <Box sx={{overflow: "auto"}}>
                    <List>
                        {props.menuItems.map((menuItem, index) => (
                            <ListItem key={menuItem.title} disablePadding>
                                <ListItemButton onClick={() => history.push(menuItem.link)}>
                                    <ListItemText primary={t(menuItem.title)} />
                                </ListItemButton>
                            </ListItem>
                        ))}
                        <ListItem>
                            <ParkingAreaSelect/>
                        </ListItem>
                    </List>
                </Box>
            </Drawer>
            <Box component="main" sx={{flexGrow: 1, p: 3}}>
                <Toolbar className={headerStyles.toolbar} />
                {props.children}
            </Box>
        </Box>
    );
}

export default SidebarNavigation;
