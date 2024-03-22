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
import ObservationAreaSelect from "../../../features/observationArea/ObservationAreaSelect";
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
        <Box sx={{display: "flex", height: "100%"}}>
            <CssBaseline />
            <Box component="main" sx={{flexGrow: 1}}>
                {props.children}
            </Box>
        </Box>
    );
}

export default SidebarNavigation;
