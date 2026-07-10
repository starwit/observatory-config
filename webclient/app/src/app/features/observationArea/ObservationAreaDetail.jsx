import {useEffect, useMemo, useState, useRef} from "react";
import {useNavigate, useParams} from "react-router-dom";
import {AppBar} from "../../assets/styles/HeaderStyles";
import ObservationAreaRest from "../../services/ObservationAreaRest";
import ImageRest, {imageFileUrlForId} from "../../services/ImageRest";
import ImageAnnotate from "../imageAnnotate/ImageAnnotate";
import TrajectoryDrawer from "../visualizer/TrajectoryDrawer";
import ObservationAreaDialog, {MODE as ObservationAreaDialogMode} from "./ObservationAreaDialog";
import ObservationAreaSelect from "./ObservationAreaSelect";
import {Box, Typography} from "@mui/material";
import ImageNotSupportedOutlinedIcon from "@mui/icons-material/ImageNotSupportedOutlined";
import {useTranslation} from "react-i18next";
import ObservationAreaDetailStyles from "../../assets/styles/ObservationAreaDetailStyles";

function ObservationAreaDetail(props) {

    const {observationAreaId} = useParams();
    const navigate = useNavigate();
    const {t} = useTranslation();

    const [liveTrajectoriesActive, setLiveTrajectoriesActive] = useState(false);
    const [observationAreas, setObservationAreas] = useState();
    const selectedArea = observationAreas?.find(a => String(a.id) === observationAreaId);
    const [editDialogOpen, setEditDialogOpen] = useState(false);
    const [image, setImage] = useState();
    const [imageLoaded, setImageLoaded] = useState(false);

    const observationAreaRest = useMemo(() => new ObservationAreaRest(), [])
    const imageRest = useMemo(() => new ImageRest(), []);

    const annotatorRef = useRef(null);

    useEffect(() => {
        reloadObservationAreas();
    }, []);

    useEffect(() => {
        reloadImage();
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
                    onSaveClick={saveRegions}
                    onLiveTrajectoriesClick={toggleLiveTrajectories}
                    onImageRenewed={reloadImage}
                    showTrajectories={liveTrajectoriesActive}
                />
            </AppBar>
        )
    }



    return (
        <>
            <Box sx={{height: "100vh", display: "flex", flexDirection: "column"}}>
                {renderAppBar()}
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
                            liveTrajectoriesActive && selectedArea.saeStreamKeys?.[0]
                                ? ({width}) => (
                                    <TrajectoryDrawer
                                        stream={selectedArea.saeStreamKeys[0]}
                                        width={width}
                                    />
                                )
                                : undefined
                        }
                        ref={annotatorRef}
                    ></ImageAnnotate>
                )}
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