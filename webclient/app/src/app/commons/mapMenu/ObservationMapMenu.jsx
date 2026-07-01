import TimeToLeaveIcon from '@mui/icons-material/TimeToLeave';
import {useContext} from 'react';
import StyledToggleButton from './StyledToggleButton';
import { useTranslation } from 'react-i18next';
import { Box } from '@mui/material';

function ObservationMapMenu(props) {
    const {setToggleLiveTracking, showLive} = props;
    const {t} = useTranslation();

    return (
        <>
            <StyledToggleButton title={t('map.live')} value="live" aria-label="live" onClick={setToggleLiveTracking}  >
                <TimeToLeaveIcon variant="contained" sx={{ color: showLive ? 'green' : 'inherit' }} />
            </StyledToggleButton>
            <Box sx={{paddingBottom: 5}} />
        </>
    );
}

export default ObservationMapMenu;