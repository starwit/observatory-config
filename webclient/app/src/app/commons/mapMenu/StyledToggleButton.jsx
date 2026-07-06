import {styled, ToggleButton, Tooltip} from "@mui/material";

const ToggleButtonStyling = styled(ToggleButton)(({theme}) => ({
    "&.Mui-selected:hover, :hover": {
        backgroundColor: theme.palette.background.grey
    },
    "&.Mui-selected": {
        backgroundColor: theme.palette.background.green
    },
    backgroundColor: theme.palette.background.paper,
    boxShadow: theme.shadows[4],
    border: "none",
    color: theme.palette.text.primary
}));

function StyledToggleButton(props) {

    const {children, value, "aria-label": ariaLabel, title, onClick} = props;

    return (
        <Tooltip title={title}>
            <ToggleButtonStyling size='small' value={value} aria-label={ariaLabel} onClick={onClick} color='success'>
                {children}
            </ToggleButtonStyling>
        </Tooltip>
    );
}

export default StyledToggleButton;