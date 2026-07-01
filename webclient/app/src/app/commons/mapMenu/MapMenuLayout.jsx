import {ToggleButtonGroup} from "@mui/material";

function MapMenuLayout(props) {
    const {children, value, onChange} = props;

    return (
        <ToggleButtonGroup size='small'
            sx={{
                position: 'fixed',
                right: 10,
                top: 60,
                zIndex: 1,
                gap: 1
            }}
            value={value}
            orientation="vertical"
            onChange={onChange}
            color='success'
        >
            {children}
        </ToggleButtonGroup>
    );
}

export default MapMenuLayout;