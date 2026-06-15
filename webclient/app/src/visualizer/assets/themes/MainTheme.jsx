import React from "react";
import {ThemeProvider} from "@mui/material";
import general from "./general/ComponentTheme";

function MainTheme(props) {
    const DynamicTheme = general;
    return (
        <ThemeProvider theme={DynamicTheme}>
            {props.children}

        </ThemeProvider>
    )
}

export default MainTheme;
