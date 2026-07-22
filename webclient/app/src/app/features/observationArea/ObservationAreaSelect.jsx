import {Camera, Home} from "@mui/icons-material";
import EditRoundedIcon from "@mui/icons-material/EditRounded";
import EmergencyRecordingIcon from '@mui/icons-material/EmergencyRecording';
import PlayCircleFilledWhiteIcon from "@mui/icons-material/PlayCircleFilledWhite";
import RefreshIcon from "@mui/icons-material/Refresh";
import SaveIcon from '@mui/icons-material/Save';
import SsidChartIcon from '@mui/icons-material/SsidChart';
import StopCircleIcon from "@mui/icons-material/StopCircle";
import {CircularProgress, Divider, IconButton, Stack, Tooltip, Typography} from "@mui/material";
import FormControl from "@mui/material/FormControl";
import MenuItem from "@mui/material/MenuItem";
import Select from "@mui/material/Select";
import {useMemo, useState} from "react";
import {useTranslation} from "react-i18next";
import ConfirmationDialog from "../../commons/dialog/ConfirmationDialog";
import ImageRest from "../../services/ImageRest";
import ObservationAreaRest from "../../services/ObservationAreaRest";

function ObservationAreaSelect(props) {
    const {
        observationAreas,
        selectedArea,
        onHomeClick,
        onEditClick,
        onAreaChange,
        onShowSavedTrajectoriesClicked,
        onLiveTrajectoriesClick,
        onStartRecordingClick,
        onImageRenewed,
        onSaveClick,
        showTrajectories = false,
        showSavedTrajectories = false,
        showRecordedTrajectories = false
    } = props;

    const {t} = useTranslation();

    const [processingPromptOpen, setProcessingPromptOpen] = useState(false);
    const [processingEnabled, setProcessingEnabled] = useState(selectedArea.processingEnabled);
    const [refreshing, setRefreshing] = useState(false);
    const observationAreaRest = useMemo(() => new ObservationAreaRest(), []);
    const imageRest = useMemo(() => new ImageRest(), []);

    const renewImageDisabled = refreshing || selectedArea.geoReferenced;

    function handleRefreshImage() {
        setRefreshing(true);
        imageRest.renewImage(selectedArea.id)
            .then(() => onImageRenewed?.())
            .catch(() => {/* error is reported by the global error handler */})
            .finally(() => setRefreshing(false));
    }

    const handleChange = event => {
        const newObservationAreaId = event.target.value;
        onAreaChange(newObservationAreaId);
    };

    function openProcessingPrompt() {
        setProcessingPromptOpen(true);
    }

    function closeProcessingPrompt() {
        setProcessingPromptOpen(false);
    }

    function toggleProcessing() {
        const tempProcessingEnabled = !processingEnabled;
        observationAreaRest.updateProcessingStatus(selectedArea.id, tempProcessingEnabled).then(response => {
            if (response.data == null) {
                return;
            }
            setProcessingEnabled(response.data);
        });
        closeProcessingPrompt();
    }

    function renderProcessingIcon() {
        if (processingEnabled) {
            return (
                <StopCircleIcon color="error" />
            );
        }
        return (
            <PlayCircleFilledWhiteIcon color="primary" />
        );
    }

    function renderProcessingText() {
        if (processingEnabled) {
            return (
                <Typography variant="body2" component="span" noWrap sx={{marginTop: "0.2rem"}}>{t("button.tracking")}</Typography>
            );
        }
        return;
    }

    return (
        <Stack
            direction="row"
            sx={{marginTop: "0.4rem", height: "2.4rem", color: "dimgrey"}}
            useFlexGap
            flexWrap="nowrap"
        >
            <FormControl sx={{paddingLeft: "0.5rem"}}>
                <IconButton sx={{height: "2rem"}}
                    onClick={onHomeClick}>
                    <Home color="primary" />
                </IconButton>
            </FormControl>
            <FormControl sx={{boxShadow: "none", width: "20rem"}}>
                <Select sx={{height: "2rem", margin: "0rem"}}
                    value={selectedArea.id} onChange={handleChange}>
                    {observationAreas.map(entity => (
                        <MenuItem sx={{margin: "0rem"}}
                            key={entity.id} value={entity.id} >{entity.name}</MenuItem>))}
                </Select>
            </FormControl>
            <FormControl>
                <Tooltip title={t("button.update")}>
                    <IconButton sx={{height: "2rem"}} onClick={onEditClick}>
                        <EditRoundedIcon color="primary" />
                    </IconButton>
                </Tooltip>
            </FormControl>
            <FormControl>
                <Tooltip title={selectedArea.geoReferenced ? t("observationArea.renewImage.geoReferencedDisabled") : t("observationArea.renewImage")}>
                    <span>
                        <IconButton sx={{height: "2rem"}} onClick={handleRefreshImage} disabled={renewImageDisabled}>
                            {refreshing ? <CircularProgress size={20} /> : <RefreshIcon color={renewImageDisabled ? "disabled" : "primary"} />}
                        </IconButton>
                    </span>
                </Tooltip>
            </FormControl>
            <FormControl>
                <Tooltip title={t('observationArea.showTrajectories')}>
                    <IconButton sx={{height: "2rem"}} fontSize="small"
                        onClick={onLiveTrajectoriesClick}>
                        {showTrajectories ? <Camera color="secondary" /> : <Camera color="primary" />}
                    </IconButton>
                </Tooltip>
            </FormControl>
            <FormControl>
                <Tooltip title={processingEnabled ? t("observationArea.track.stop.title") : t("observationArea.track.start.title")}>
                    <IconButton sx={{height: "2rem"}}
                        onClick={() => {
                            openProcessingPrompt();
                        }}
                    >
                        {renderProcessingIcon()}
                    </IconButton>
                </Tooltip>
            </FormControl>
            {renderProcessingText()}

            <Divider orientation="vertical" variant="middle" sx={{mt: "2px", mb: "6px"}} flexItem />

            <FormControl>
                <Tooltip title={t('observationArea.showSavedTrajectories')}>
                    <IconButton sx={{height: "2rem"}} fontSize="small"
                        onClick={onShowSavedTrajectoriesClicked}>
                        {showSavedTrajectories ? <SsidChartIcon color="secondary" /> : <SsidChartIcon color="primary" />}
                    </IconButton>
                </Tooltip>
            </FormControl>
            <FormControl>
                <Tooltip title={t('observationArea.recordTrajectories')}>
                    <IconButton sx={{height: "2rem"}} fontSize="small"
                        onClick={onStartRecordingClick}>
                        {showRecordedTrajectories ? <EmergencyRecordingIcon color="secondary" /> : <EmergencyRecordingIcon color="primary" />}
                    </IconButton>
                </Tooltip>
            </FormControl>

            <FormControl>
                <ConfirmationDialog
                    title={processingEnabled ? t("observationArea.track.stop.title") : t("observationArea.track.start.title")}
                    message={processingEnabled ? t("observationArea.track.stop") : t("observationArea.track.start")}
                    open={processingPromptOpen}
                    onClose={closeProcessingPrompt}
                    onSubmit={toggleProcessing}
                    confirmTitle={t("button.submit")}
                />
            </FormControl>
            <FormControl sx={{marginLeft: "auto", paddingRight: "0.5rem"}}>
                <Tooltip title={t('button.save')}>
                    <IconButton sx={{height: "2rem"}} fontSize="small"
                        onClick={onSaveClick}>
                        <SaveIcon color="primary" />
                    </IconButton>
                </Tooltip>
            </FormControl>
        </Stack >
    );
}

export default ObservationAreaSelect;
