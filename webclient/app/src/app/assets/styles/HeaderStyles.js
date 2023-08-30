import {makeStyles} from "@mui/styles";
import MuiAppBar from "@mui/material/AppBar";
import {styled} from "@mui/material/styles";

const drawerWidth = 240;
const appBarHeight = "3rem";

const AppBar = styled(MuiAppBar, {
    shouldForwardProp: prop => prop !== "open"
})(({theme, open}) => ({
    transition: theme.transitions.create(["margin", "width"], {
        easing: theme.transitions.easing.sharp,
        duration: theme.transitions.duration.leavingScreen
    }),
    ...(open && {
        width: `calc(100% - ${drawerWidth}px)`,
        marginLeft: `${drawerWidth}px`,
        transition: theme.transitions.create(["margin", "width"], {
            easing: theme.transitions.easing.easeOut,
            duration: theme.transitions.duration.enteringScreen
        })
    })
}));

const DrawerHeader = styled("div")(({theme}) => ({
    display: "flex",
    alignItems: "center",
    padding: theme.spacing(0, 1),
    // necessary for content to be below app bar
    ...theme.mixins.toolbar,
    justifyContent: "flex-end"
}));

const HeaderStyles = makeStyles(theme => ({
    root: {
        display: "flex"
    },
    appBar: {
        zIndex: theme.zIndex.modal + 1,
        height: "3rem"
    },
    drawer: {
        [theme.breakpoints.up("sm")]: {
            width: drawerWidth,
            flexShrink: 0
        }
    },
    menuTitle: {
        display: "flex",
        justifyContent: "left",
        paddingLeft: 20,
        [theme.breakpoints.up("sm")]: {
            display: "flex",
            width: "20%",
            justifyContent: "center"
        }
    },
    menuLogo: {
        display: "none",
        [theme.breakpoints.up("sm")]: {
            display: "flex",
            width: 350
        }
    },
    menuLogoResponsive: {
        display: "flex",
        width: 50,
        [theme.breakpoints.up("sm")]: {
            display: "none"
        }
    },
    menuLogoImg: {
        height: "70%",
        width: "auto"
    },
    spacer: {
        flexGrow: 1
    },
    menuButton: {
        marginRight: 20,
        [theme.breakpoints.up("sm")]: {
            display: "none"
        }
    },
    content: {
        flexGrow: 1,
        padding: theme.spacing.unit * 3
    },
    toolbar: {
        ...theme.mixins.toolbar,
        height: "2rem"
    },
    linkButton: {
        marginRight: theme.spacing(3),
        marginLeft: theme.spacing(3)
    },
    contentSpacer: {
        height: `calc(${appBarHeight} + ${theme.spacing(3)})`
    }
}));
export default HeaderStyles;
export {HeaderStyles, AppBar, DrawerHeader};
