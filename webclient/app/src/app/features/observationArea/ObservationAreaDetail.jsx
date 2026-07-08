import {useEffect, useMemo, useState, useRef} from "react";
import {useNavigate, useParams} from "react-router-dom";
import {AppBar} from "../../assets/styles/HeaderStyles";
import ObservationAreaRest from "../../services/ObservationAreaRest";
import ImageAnnotate from "../imageAnnotate/ImageAnnotate";
import TrajectoryDrawer from "../visualizer/TrajectoryDrawer";
import ObservationAreaDialog, {MODE as ObservationAreaDialogMode} from "./ObservationAreaDialog";
import ObservationAreaSelect from "./ObservationAreaSelect";
import SavedTrajectoryDrawer from "../visualizer/SavedTrajectoriesDrawer";
import {Box, useForkRef} from "@mui/material";

function ObservationAreaDetail(props) {

    const {observationAreaId} = useParams();
    const navigate = useNavigate();

    const [showSavedTrajectoriesState, setShowSavedTrajectoriesState] = useState(false);
    
    const [liveTrajectoriesActive, setLiveTrajectoriesActive] = useState(false);
    const [observationAreas, setObservationAreas] = useState();
    const selectedArea = observationAreas?.find(a => String(a.id) === observationAreaId);
    const [editDialogOpen, setEditDialogOpen] = useState(false);

    const observationAreaRest = useMemo(() => new ObservationAreaRest(), [])

    const annotatorRef = useRef(null);

    useEffect(() => {
        reloadObservationAreas();
    }, []);

    function reloadObservationAreas() {
        observationAreaRest.findAll().then(response => {
            if (response.data == null) {
                return;
            }
            setObservationAreas(response.data);
            if (selectedArea !== undefined) navigateToArea(selectedArea.id);
        });
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

    function onShowSavedTrajectoriesChanged() {
        setShowSavedTrajectoriesState(!showSavedTrajectoriesState);
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
                    showTrajectories={liveTrajectoriesActive}
                    onShowSavedTrajectoriesChanged={onShowSavedTrajectoriesChanged}
                    showSavedTrajectories={showSavedTrajectoriesState}
                />
            </AppBar>
        )
    }

    return (
        <>
            <Box sx={{height: "100vh", display: "flex", flexDirection: "column"}}>
                {renderAppBar()}
                <ImageAnnotate
                    observationAreaId={observationAreaId}
                    sx={{zIndex: 20000}}
                    lockCanvas={liveTrajectoriesActive}
                    renderImageOverlay={
                        (liveTrajectoriesActive || showSavedTrajectoriesState) && selectedArea.saeStreamKeys?.[0]
                            ? ({ width, height, naturalWidth, naturalHeight }) => (
                                <>
                                    {liveTrajectoriesActive && <TrajectoryDrawer stream={selectedArea.saeStreamKeys[0]} width={width} />}
                                    {showSavedTrajectoriesState && (
                                        <SavedTrajectoryDrawer
                                            streamKey={selectedArea.saeStreamKeys[0]}
                                            width={width}
                                            height={height}
                                            frameWidth={selectedArea.imageWidth ?? naturalWidth}
                                            frameHeight={selectedArea.imageHeight ?? naturalHeight}
                                        />
                                    )}
                                </>
                            )
                            : undefined
                    }
                    ref={annotatorRef}
                ></ImageAnnotate>
            </Box>
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