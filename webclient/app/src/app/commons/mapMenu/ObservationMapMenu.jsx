import {useContext} from 'react';
import StyledToggleButton from './StyledToggleButton';
import { useTranslation } from 'react-i18next';
import { Box } from '@mui/material';
import CameraIcon from '@mui/icons-material/Camera';

function ObservationMapMenu(props) {
    const {setToggleLiveTracking, showLive} = props;
    const {t} = useTranslation();

    return (
        <>
            <StyledToggleButton title={t('map.live')} value="live" aria-label="live" onClick={setToggleLiveTracking}  >
                <CameraIcon variant="contained" sx={{ color: showLive ? 'green' : 'inherit' }} />
            </StyledToggleButton>
            <Box sx={{paddingBottom: 5}} />
        </>
    );
}

export default ObservationMapMenu;