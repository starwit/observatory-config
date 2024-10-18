import {
    Box,
    CssBaseline
} from "@mui/material";

import React from "react";

function SidebarNavigation(props) {
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
