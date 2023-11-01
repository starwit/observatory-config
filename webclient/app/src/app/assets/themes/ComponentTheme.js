import {createTheme} from "@mui/material";
import ColorTheme from "./ColorTheme";

const ComponentTheme = createTheme(ColorTheme,
    {
        components: {
            MuiContainer: {},
            MuiCard: {
                defaultProps: {
                    elevation: 10
                }
            },
            MuiFab: {
                defaultProps: {
                    color: "secondary"
                }
            },
            MuiStack: {
                defaultProps: {
                    spacing: 2
                }
            },
            MuiIconButton: {
                defaultProps: {
                    color: "inherit"
                }
            },

            MuiDialogTitle: {
                styleOverrides: {
                    // Name of the slot
                    root: {
                        // Some CSS
                        paddingTop: "10px",
                        paddingBottom: "10px",
                        marginBottom: "2px"
                    }
                }
            }

        }
    });
export default ComponentTheme;
