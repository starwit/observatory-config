import React from "react";
import ReactDOM from "react-dom/client";
import "./index.css";
import App from "./app/App";
import VisualizerApp from "./visualizer/App";
import * as serviceWorker from "./serviceWorker";
import {HashRouter as Router, Route, Routes} from "react-router-dom";
import "./localization/i18n";
import {ToastContainer} from "react-toastify";
import MainTheme from "./app/assets/themes/MainTheme";

const root = ReactDOM.createRoot(document.getElementById("root"));
root.render(
    <Router>
        <Routes>
            {/* The SAE visualizer lives under its own router path with its own MUI theme. */}
            <Route path="/visualizer/*" element={<VisualizerApp />} />
            {/* Everything else is the observatory-config app, unchanged internally. */}
            <Route path="/*" element={<MainTheme><App /></MainTheme>} />
        </Routes>
        <ToastContainer
            position="bottom-left"
            closeOnClick
            limit={1}
            pauseOnFocusLoss
            draggable={false}
            theme="colored"
            // Toastify defaults to 9999, which is below the dialogs and the annotator overlay
            style={{zIndex: 100000}}
        />
    </Router>
);

// If you want your app to work offline and load faster, you can change
// unregister() to register() below. Note this comes with some pitfalls.
// Learn more about service workers: http://bit.ly/CRA-PWA
serviceWorker.unregister();
