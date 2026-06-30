import {CssBaseline} from "@mui/material";
import React from "react";
import {SettingsProvider} from "./contexts/SettingsContext";
import ErrorHandler from "./errorHandler/ErrorHandler.jsx";
import MainContentRouter from "./MainContentRouter";


function App() {
    return (
        <React.Fragment>
            <ErrorHandler>
                <SettingsProvider>
                    <CssBaseline />
                    <MainContentRouter />
                </SettingsProvider>
            </ErrorHandler>
        </React.Fragment >
    );
}

export default App;
