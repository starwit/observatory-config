import {Analytics, Camera, Home} from "@mui/icons-material";
import EditRoundedIcon from "@mui/icons-material/EditRounded";
import EmergencyRecordingIcon from '@mui/icons-material/EmergencyRecording';
import RefreshIcon from "@mui/icons-material/Refresh";
import SaveIcon from '@mui/icons-material/Save';
import SsidChartIcon from '@mui/icons-material/SsidChart';
import {Button, Checkbox, CircularProgress, Divider, ListItemText, Stack, Tooltip, Typography} from "@mui/material";
import FormControl from "@mui/material/FormControl";
import MenuItem from "@mui/material/MenuItem";
import Select from "@mui/material/Select";
import {useMemo, useState} from "react";
import {useTranslation} from "react-i18next";
import ConfirmationDialog from "../../commons/dialog/ConfirmationDialog";
import ImageRest from "../../services/ImageRest";
import ObservationAreaRest from "../../services/ObservationAreaRest";
import {CLASS_COLORS, CLASS_MAPPING} from "../visualizer/SavedTrajectoriesDrawer";

const ALL_CLASS_IDS = "all";

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
        showRecordedTrajectories = false,
        selectedSavedTrajectoryClassIds = [],
        onSavedTrajectoryClassIdsChange
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

    function handleSavedTrajectoryClassChange(event) {
        const selectedValues = event.target.value;
        const values = typeof selectedValues === "string" ? selectedValues.split(",") : selectedValues;
        onSavedTrajectoryClassIdsChange(values.includes(ALL_CLASS_IDS) ? [] : values.map(Number));
    }

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
                <Analytics color="secondary" variant="outlined" />
            );
        }
        return (
            <Analytics color="primary" variant="outlined" />
        );
    }

    function renderProcessingText() {
        if (processingEnabled) {
            return (
                <Typography variant="body2" component="span" noWrap sx={{marginTop: "0.2rem"}}>{t("button.tracking")}</Typography>
            );
        } else {
            return (
                <Typography variant="body2" component="span" noWrap sx={{marginTop: "0.2rem"}}>{t("button.notTracking")}</Typography>
            );
        }
    }

    return (
        <Stack
            direction="row"
            sx={{marginTop: "0.4rem", height: "2.4rem", px: 1}}
            useFlexGap
        >
            <FormControl>
                <Button sx={{height: "2rem", minWidth: "3rem"}}
                    onClick={onHomeClick}>
                    <Home color="primary" />
                </Button>
            </FormControl>
            <FormControl sx={{boxShadow: "none", width: "20rem"}}>
                <Select sx={{height: "2rem", margin: "0rem", marginRight: "0.5rem"}}
                    value={selectedArea.id} onChange={handleChange}>
                    {observationAreas.map(entity => (
                        <MenuItem sx={{margin: "0rem"}}
                            key={entity.id} value={entity.id} >{entity.name}</MenuItem>))}
                </Select>
            </FormControl>
            <FormControl>
                <Tooltip title={t("button.update")}>
                    <Button sx={{height: "2rem", minWidth: "3rem"}} onClick={onEditClick}>
                        <EditRoundedIcon color="primary" />
                    </Button>
                </Tooltip>
            </FormControl>
            <FormControl>
                <Tooltip title={selectedArea.geoReferenced ? t("observationArea.renewImage.geoReferencedDisabled") : t("observationArea.renewImage")}>
                    <span>
                        <Button sx={{height: "2rem", minWidth: "3rem"}} onClick={handleRefreshImage} disabled={renewImageDisabled}>
                            {refreshing ? <CircularProgress size={20} /> : <RefreshIcon color={renewImageDisabled ? "disabled" : "primary"} />}
                        </Button>
                    </span>
                </Tooltip>
            </FormControl>
            <Divider orientation="vertical" variant="middle" sx={{mt: "2px", mb: "6px", mr: "1.5rem", textAlign: "center", minWidth: "1.5rem"}} flexItem />

            <FormControl>
                <Tooltip title={t('observationArea.showTrajectories')}>
                    <Button sx={{height: "2rem", minWidth: "3rem"}} fontSize="small"
                        onClick={onLiveTrajectoriesClick}>
                        {showTrajectories ? <Camera color="secondary" /> : <Camera color="primary" />}
                    </Button>
                </Tooltip>
            </FormControl>

            <FormControl>
                <Tooltip title={t('observationArea.showSavedTrajectories')}>
                    <Button sx={{height: "2rem", minWidth: "3rem"}} fontSize="small"
                        onClick={onShowSavedTrajectoriesClicked}>
                        {showSavedTrajectories ? <SsidChartIcon color="secondary" /> : <SsidChartIcon color="primary" />}
                    </Button>
                </Tooltip>
            </FormControl>
            {showSavedTrajectories && (
                <FormControl sx={{width: "12rem"}}>
                    <Select
                        multiple
                        value={selectedSavedTrajectoryClassIds}
                        onChange={handleSavedTrajectoryClassChange}
                        renderValue={selectedClassIds => selectedClassIds.length === 0
                            ? t("observationArea.trajectoryFilter.allClasses")
                            : selectedClassIds.map(classId => t(`mapping.${CLASS_MAPPING[classId]}`)).join(", ")}
                        inputProps={{"aria-label": t("observationArea.trajectoryFilter.label")}}
                        sx={{height: "2rem", margin: "0rem", marginRight: "0.5rem"}}
                    >
                        <MenuItem value={ALL_CLASS_IDS}>
                            <Checkbox checked={selectedSavedTrajectoryClassIds.length === 0} />
                            <ListItemText primary={t("observationArea.trajectoryFilter.allClasses")} />
                        </MenuItem>
                        {Object.entries(CLASS_COLORS).map(([classId, color]) => (
                            <MenuItem key={classId} value={Number(classId)}>
                                <Checkbox checked={selectedSavedTrajectoryClassIds.includes(Number(classId))} />
                                <span
                                    aria-hidden="true"
                                    style={{
                                        width: "0.75rem",
                                        height: "0.75rem",
                                        marginRight: "0.5rem",
                                        borderRadius: "50%",
                                        backgroundColor: `rgba(${color[0]}, ${color[1]}, ${color[2]}, ${color[3] / 255})`
                                    }}
                                />
                                <ListItemText primary={t(`mapping.${CLASS_MAPPING[classId]}`)} />
                            </MenuItem>
                        ))}
                    </Select>
                </FormControl>
            )}
            <FormControl>
                <Tooltip title={t('observationArea.recordTrajectories')}>
                    <Button sx={{height: "2rem", minWidth: "3rem"}} fontSize="small"
                        onClick={onStartRecordingClick}>
                        {showRecordedTrajectories ? <EmergencyRecordingIcon color="secondary" /> : <EmergencyRecordingIcon color="primary" />}
                    </Button>
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
            <Divider orientation="vertical" variant="middle" sx={{mt: "2px", mb: "6px", mr: "1.5rem", textAlign: "center", minWidth: "1.5rem"}} flexItem />

            <FormControl sx={{marginLeft: "auto"}}>
                {renderProcessingText()}
            </FormControl>
            <FormControl>
                <Tooltip title={processingEnabled ? t("observationArea.track.stop.title") : t("observationArea.track.start.title")}>
                    <Button sx={{height: "2rem", minWidth: "3rem"}} fontSize="small"
                        onClick={() => {
                            openProcessingPrompt();
                        }}
                    >
                        {renderProcessingIcon()}
                    </Button>
                </Tooltip>
            </FormControl>
            <FormControl sx={{paddingRight: "0.5rem", width: "270px"}}>
                <Tooltip title={t('button.save')}>
                    <Button sx={{height: "2rem", minWidth: "3rem", marginLeft: "auto"}} fontSize="small"
                        onClick={onSaveClick}>
                        <SaveIcon color="primary" />
                    </Button>
                </Tooltip>
            </FormControl>
        </Stack >
    );
}

export default ObservationAreaSelect;
