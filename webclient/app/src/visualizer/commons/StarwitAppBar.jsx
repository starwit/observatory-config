import BlurOnIcon from '@mui/icons-material/BlurOn';
import GridViewIcon from '@mui/icons-material/GridView';
import MapIcon from "@mui/icons-material/Map";
import TrendingUpIcon from '@mui/icons-material/TrendingUp';
import PermDataSettingIcon from '@mui/icons-material/PermDataSetting';

import {
    AppBar,
    Container,
    IconButton,
    Toolbar,
    Tooltip,
    Typography,
    Divider,
} from "@mui/material";

import {useTranslation} from 'react-i18next';
import general from "../assets/images/logo_color.png";
import SettingsMenu from './SettingsMenu';

function MyAppBar() {
    const {t} = useTranslation();
    const DynamicLogo = general;

    return (
        <>
            <Container>
                <AppBar color="secondary">
                    <Toolbar>
                        <IconButton
                            size="large"
                            edge="start"
                            color="inherit"
                            href="#/visualizer/map"
                            aria-label="menu"
                            sx={{m: 0, p: 0, mr: 2}}
                        >
                            <img src={DynamicLogo} height={40} alt="Alert Viewer" />
                        </IconButton>
                        <Typography variant="h1" component="div" sx={{flexGrow: 1}}>
                            {t('app.title')}
                        </Typography>
                        <Tooltip title={t('map.tooltip')}>
                            <IconButton
                                href="#/visualizer/map"
                                variant="outlined">
                                <MapIcon />
                            </IconButton>
                        </Tooltip>
                        <Tooltip title={t('trajectory.tooltip')}>
                            <IconButton
                                href="#/visualizer/trajectory"
                                variant="outlined">
                                <TrendingUpIcon />
                            </IconButton>
                        </Tooltip>
                        <Tooltip title={t('heatmap.tooltip')}>
                            <IconButton
                                href="#/visualizer/heatmap"
                                variant="outlined">
                                <BlurOnIcon />
                            </IconButton>
                        </Tooltip>
                        <Tooltip title={t('grid.tooltip')}>
                            <IconButton
                                href="#/visualizer/grid"
                                variant="outlined">
                                <GridViewIcon />
                            </IconButton>
                        </Tooltip>
                        <Divider orientation='vertical' flexItem sx={{m: "10px"}} />
                        {/* Single entry point back to the observatory-config app */}
                        <Tooltip title={t('observatoryConfig.tooltip')}>
                            <IconButton
                                href="#/"
                                variant="outlined">
                                <PermDataSettingIcon />
                            </IconButton>
                        </Tooltip>
                        <SettingsMenu />
                    </Toolbar>
                </AppBar>
            </Container>
        </>
    );
}

export default MyAppBar;