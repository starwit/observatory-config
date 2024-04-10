import { useEffect, useMemo, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { AppBar } from "../../assets/styles/HeaderStyles";
import ObservationAreaRest from "../../services/ObservationAreaRest";
import ImageAnnotate from "../imageAnnotate/ImageAnnotate";
import ObservationAreaDialog, { MODE as ObservationAreaDialogMode } from "./ObservationAreaDialog";
import ObservationAreaSelect from "./ObservationAreaSelect";

export default function ObservationAreaDetail() {

    const {observationAreaId} = useParams();
    const navigate = useNavigate();

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

    function navigateToHome() {
        navigate("/");
    }

    if (observationAreas === undefined || selectedArea === undefined) {
        return;
    }

    return (
        <>
            <AppBar color="transparent" sx={{boxShadow: "none", left: "0rem", right: "8rem", width: "80%"}}>
                <ObservationAreaSelect
                    observationAreas={observationAreas}
                    selectedArea={selectedArea}
                    onHomeClick={navigateToHome}
                    onEditClick={editArea}
                    onAreaChange={navigateToArea}
                />
            </AppBar>
            <ImageAnnotate 
                observationAreaId={observationAreaId}
            ></ImageAnnotate>
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