import {useEffect, useMemo, useState} from "react";
import {useNavigate, useParams} from "react-router-dom";
import {AppBar} from "../../assets/styles/HeaderStyles";
import ObservationAreaRest from "../../services/ObservationAreaRest";
import ImageAnnotate from "../imageAnnotate/ImageAnnotate";
import ObservationVisualization from "../visualizer/ObservationVisualization";
import ObservationAreaDialog, {MODE as ObservationAreaDialogMode} from "./ObservationAreaDialog";
import ObservationAreaSelect from "./ObservationAreaSelect";

function ObservationAreaDetail(props) {

    const {observationAreaId} = useParams();
    const {showTrajectories = false} = props;
    const navigate = useNavigate();

    const [showTrajectoriesState, setShowTrajectoriesState] = useState(showTrajectories);
    const [annotateImageSize, setAnnotateImageSize] = useState();
    const [observationAreas, setObservationAreas] = useState();
    const selectedArea = observationAreas?.find(a => String(a.id) === observationAreaId);
    const [editDialogOpen, setEditDialogOpen] = useState(false);

    const observationAreaRest = useMemo(() => new ObservationAreaRest(), [])

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

    function onShowTrajectoriesChanged() {
        setShowTrajectoriesState(!showTrajectoriesState);
    }

    function navigateToHome() {
        navigate("/");
    }

    if (observationAreas === undefined || selectedArea === undefined) {
        return;
    }

    function renderAppBar() {
        const appBarSx = {boxShadow: "none", left: "0rem", right: "8rem", width: "80vw", transition: "none"};

        return (
            <AppBar color="transparent" sx={appBarSx}>
                <ObservationAreaSelect
                    observationAreas={observationAreas}
                    selectedArea={selectedArea}
                    onHomeClick={navigateToHome}
                    onEditClick={editArea}
                    onAreaChange={navigateToArea}
                    onShowTrajectoriesChanged={onShowTrajectoriesChanged}
                    showTrajectories={showTrajectoriesState}
                />
            </AppBar>
        )
    }



    return (
        <>
            {renderAppBar()}
            <ImageAnnotate
                observationAreaId={observationAreaId}
                onImageSizeChange={setAnnotateImageSize}
                sx={{zIndex: 20000}}
            ></ImageAnnotate>
            {showTrajectoriesState ? <ObservationVisualization streams={selectedArea.saeStreamKeys} imageSize={annotateImageSize}></ObservationVisualization> : null}
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