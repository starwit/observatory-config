import React from "react";
import MainContentRouter from "./MainContentRouter";
import {CssBaseline} from "@mui/material";
import {ErrorHandler} from "@starwit/react-starwit";
import {useTranslation} from "react-i18next";


function App() {
    const {t} = useTranslation();

    return (
        <React.Fragment>
            <ErrorHandler>
                <CssBaseline/>
                <MainContentRouter/>
            </ErrorHandler>
        </React.Fragment>
    );
}

export default App;
