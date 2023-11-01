import React from "react";
import SidebarNavigation from "./sidebarNavigation/SidebarNavigation";
import NavigationStyles from "./NavigationStyles";

function Navigation(props) {
    const {menuItems, title, logo, removeContentSpacer} = props;
    const navigationStyles = NavigationStyles();

    function renderCorrectNavigation() {
        return (
            <SidebarNavigation menuItems={menuItems} title={title} logo={logo} focusMode={removeContentSpacer}>
                {props.children}
            </SidebarNavigation>
        );
    }

    return (
        <div style={{height: "100%"}} className={removeContentSpacer ? null : navigationStyles.contentSpacer}>
            {renderCorrectNavigation()}
        </div>
    );
}

Navigation.defaultProps = {
    switchLength: 4,
    title: "New App",
    menuItems: []

};

export default Navigation;
