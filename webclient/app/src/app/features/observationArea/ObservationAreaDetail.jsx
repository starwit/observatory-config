import ImageNotSupportedOutlinedIcon from "@mui/icons-material/ImageNotSupportedOutlined";
import {Box, Collapse, Typography} from "@mui/material";
import {useEffect, useMemo, useRef, useState} from "react";
import {useTranslation} from "react-i18next";
import {useNavigate, useParams} from "react-router-dom";
import {AppBar} from "../../assets/styles/HeaderStyles";
import ObservationAreaDetailStyles from "../../assets/styles/ObservationAreaDetailStyles";
import ImageRest, {imageFileUrlForId} from "../../services/ImageRest";
import ObservationAreaRest from "../../services/ObservationAreaRest";
import RecordingRest from "../../services/RecordingRest";
import TrajectoryBrushFilter from "../../commons/trajectoryBrush/TrajectoryBrushFilter";
import ImageAnnotate from "../imageAnnotate/ImageAnnotate";
import SavedTrajectoryDrawer from "../visualizer/SavedTrajectoriesDrawer";
import TrajectoryDrawer from "../visualizer/TrajectoryDrawer";
import ObservationAreaDialog, {MODE as ObservationAreaDialogMode} from "./ObservationAreaDialog";
import ObservationAreaSelect from "./ObservationAreaSelect";

function ObservationAreaDetail(props) {

    const {observationAreaId} = useParams();
    const navigate = useNavigate();
    const {t} = useTranslation();

    const [showSavedTrajectoriesState, setShowSavedTrajectoriesState] = useState(false);
    const [showRecordedTrajectories, setShowRecordedTrajectoriesState] = useState(false);
    const [selectedTrajectoryRange, setSelectedTrajectoryRange] = useState(null);

    const [liveTrajectoriesActive, setLiveTrajectoriesActive] = useState(false);
    const [observationAreas, setObservationAreas] = useState();
    const selectedArea = observationAreas?.find(a => String(a.id) === observationAreaId);
    const [editDialogOpen, setEditDialogOpen] = useState(false);
    const [image, setImage] = useState();
    const [imageLoaded, setImageLoaded] = useState(false);

    const observationAreaRest = useMemo(() => new ObservationAreaRest(), [])
    const recordingRest = useMemo(() => new RecordingRest(), [])
    const imageRest = useMemo(() => new ImageRest(), []);

    const annotatorRef = useRef(null);

    useEffect(() => {
        reloadObservationAreas();
    }, []);

    useEffect(() => {
        if (selectedArea) checkTrajectorySaving();
    }, [selectedArea?.id]);

    useEffect(() => {
        reloadImage();
        setSelectedTrajectoryRange(null);
    }, [observationAreaId]);

    function reloadObservationAreas() {
        observationAreaRest.findAll().then(response => {
            if (response.data == null) {
                return;
            }
            setObservationAreas(response.data);
            if (selectedArea !== undefined) navigateToArea(selectedArea.id);
        });
    }

    function checkTrajectorySaving() {
        recordingRest.getRecordingStreams().then((result) => {
            if (selectedArea?.saeStreamKeys?.some(key => result.data.includes(key))) {
                setShowRecordedTrajectoriesState(true);
            }
        });
    }

    function reloadImage() {
        setImageLoaded(false);
        imageRest.findWithPolygons(observationAreaId).then(response => {
            setImage(response.data == null ? undefined : parseImage(response.data[0]));
            setImageLoaded(true);
        });
    }

    function parseImage(image) {
        if (image !== undefined) {
            image.src = image !== undefined ? imageFileUrlForId(image.id) : "";
            image.name = "";
        }
        return image;
    }

    function editArea() {
        setEditDialogOpen(true);
    }

    function navigateToArea(newAreaId) {
        navigate(`/observationarea/${newAreaId}`);
    }

    function toggleLiveTrajectories() {
        setLiveTrajectoriesActive(!liveTrajectoriesActive);
    }

    function onShowSavedTrajectoriesClick() {
        const next = !showSavedTrajectoriesState;
        setShowSavedTrajectoriesState(next);
        if (!next) setSelectedTrajectoryRange(null);
    }

    function onStartRecordingClick() {

        recordingRest.getRecordingStreams().then((result) => {
            if (selectedArea?.saeStreamKeys?.some(key => result.data.includes(key))) {
                recordingRest.stopRecording(selectedArea.saeStreamKeys[0]);
                setShowRecordedTrajectoriesState(false);
            } else {
                recordingRest.startRecording(selectedArea.saeStreamKeys[0]);
                setShowRecordedTrajectoriesState(true);
            }
        });
    }

    function navigateToHome() {
        navigate("/");
    }

    function saveRegions() {
        annotatorRef.current?.saveRegions();
    }

    if (observationAreas === undefined || selectedArea === undefined) {
        return;
    }

    function renderAppBar() {

        return (
            <AppBar color="inherit" position="static">
                <ObservationAreaSelect
                    observationAreas={observationAreas}
                    selectedArea={selectedArea}
                    onHomeClick={navigateToHome}
                    onEditClick={editArea}
                    onAreaChange={navigateToArea}
                    onLiveTrajectoriesClick={toggleLiveTrajectories}
                    onImageRenewed={reloadImage}
                    showTrajectories={liveTrajectoriesActive}
                    onShowSavedTrajectoriesClicked={onShowSavedTrajectoriesClick}
                    showSavedTrajectories={showSavedTrajectoriesState}
                    onStartRecordingClick={onStartRecordingClick}
                    showRecordedTrajectories={showRecordedTrajectories}
                />
                {selectedArea.saeStreamKeys?.[0] && (
                    <Collapse in={showSavedTrajectoriesState} timeout={300} unmountOnExit>
                        <Box sx={{height: "105px", borderTop: 1, borderBottom: 1, borderColor: "divider"}}>
                            <TrajectoryBrushFilter
                                streamKey={selectedArea.saeStreamKeys[0]}
                                onRangeChange={setSelectedTrajectoryRange}
                                height={100}
                            />
                        </Box>
                    </Collapse>
                )}
            </AppBar>
        )
    }

    return (
        <>
            <Box sx={{height: "100vh", display: "flex", flexDirection: "column"}}>
                {renderAppBar()}
                
                <Box sx={{flex: 1, minHeight: 0}}>
                    {imageLoaded && !image ? (
                        <Box sx={ObservationAreaDetailStyles.noImagePlaceholder}>
                            <ImageNotSupportedOutlinedIcon sx={ObservationAreaDetailStyles.noImageIcon} />
                            <Typography variant="h6">{t("observationArea.detail.noImage.title")}</Typography>
                            <Typography variant="body2">{t("observationArea.detail.noImage.hint")}</Typography>
                        </Box>
                    ) : (
                        <ImageAnnotate
                            observationAreaId={observationAreaId}
                            image={image}
                            sx={{zIndex: 20000}}
                            lockCanvas={liveTrajectoriesActive}
                            renderImageOverlay={
                                (liveTrajectoriesActive || showSavedTrajectoriesState) && selectedArea.saeStreamKeys?.[0]
                                    ? ({width, height}) => (
                                        <Box sx={{width: '100%', height: '100%', backgroundColor: 'rgba(0, 0, 0, 0.4)'}}>
                                            {showSavedTrajectoriesState && (
                                                <SavedTrajectoryDrawer
                                                    streamKey={selectedArea.saeStreamKeys[0]}
                                                    width={width}
                                                    height={height}
                                                    start={selectedTrajectoryRange?.start}
                                                    end={selectedTrajectoryRange?.end}
                                                />
                                            )}
                                            {liveTrajectoriesActive &&
                                                <TrajectoryDrawer
                                                    stream={selectedArea.saeStreamKeys[0]}
                                                    width={width}
                                                />
                                            }
                                        </Box>
                                    )
                                    : undefined
                            }
                            ref={annotatorRef}
                        ></ImageAnnotate>
                    )}
                </Box>
            </Box >
            <ObservationAreaDialog
                open={editDialogOpen}
                onSubmit={() => setEditDialogOpen(false)}
                mode={ObservationAreaDialogMode.UPDATE}
                selectedArea={selectedArea}
                update={reloadObservationAreas}
            />
        </>
    )

}

export default ObservationAreaDetail;