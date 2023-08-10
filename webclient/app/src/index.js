import React from "react";
import ReactDOM from "react-dom";
import "./index.css";
import App from "./app/App";
import {HashRouter as Router} from "react-router-dom";
import "./localization/i18n";
import {SnackbarProvider} from "notistack";
import MainTheme from "./app/assets/themes/MainTheme";

ReactDOM.render((
    <Router>
        <MainTheme>
            <SnackbarProvider maxSnack={5}>
                <App />
            </SnackbarProvider>
        </MainTheme>
    </Router>
),
    document.getElementById("root")
);
