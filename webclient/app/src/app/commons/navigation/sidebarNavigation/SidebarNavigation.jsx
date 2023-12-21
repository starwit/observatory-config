import React from "react";
import {
    Box,
    CssBaseline,
    Drawer,
    IconButton,
    List,
    ListItem,
    ListItemButton,
    ListItemText
} from "@mui/material";
import {useTheme} from "@mui/material/styles";
import {DrawerHeader} from "../../../assets/styles/HeaderStyles";
import {useTranslation} from "react-i18next";
import {useNavigate} from "react-router-dom";
import Divider from "@mui/material/Divider";
import MenuIcon from "@mui/icons-material/Menu";
import ChevronLeftIcon from "@mui/icons-material/ChevronLeft";
import ChevronRightIcon from "@mui/icons-material/ChevronRight";
import ParkingAreaSelect from "../../../features/parkingArea/ParkingAreaSelect";
import Fab from "@mui/material/Fab";

function SidebarNavigation(props) {
    const drawerWidth = 240;
    const {t} = useTranslation();
    const navigate = useNavigate();

    const theme = useTheme();
    const [open, setOpen] = React.useState(false);

    const handleDrawerOpen = () => {
        setOpen(true);
    };

    const handleDrawerClose = () => {
        setOpen(false);
    };

    return (
        <Box sx={{display: "flex", p: 0.5, height: "100%"}}>
            <CssBaseline />
            <Fab color="primary" onClick={handleDrawerOpen} >
                <MenuIcon />
            </Fab>
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
                                <ListItemButton onClick={() => navigate(menuItem.link)}>
                                    <ListItemText primary={t(menuItem.title)} />
                                </ListItemButton>
                            </ListItem>
                        ))}
                        <ListItem>
                            <ParkingAreaSelect />
                        </ListItem>
                    </List>
                </Box>
            </Drawer>
            <Box component="main" sx={{flexGrow: 1, p: 3}}>
                {props.children}
            </Box>
        </Box>
    );
}

export default SidebarNavigation;
