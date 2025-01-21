import {Container, Typography, Stack, Button, Tooltip} from "@mui/material";
import React, {useEffect, useMemo, useState} from "react";
import Grid from "@mui/material/Grid2";
import {useTranslation} from "react-i18next";
import AddFabButton from "../../commons/addFabButton/AddFabButton";
import ConfirmationDialog from "../../commons/dialog/ConfirmationDialog";
import LoadingSpinner from "../../commons/loadingSpinner/LoadingSpinner";
import ObservationAreaRest from "../../services/ObservationAreaRest";
import ObservationAreaDialog, {MODE as ObservationAreaDialogMode} from "./ObservationAreaDialog";
import ObservationAreaCard from "./ObservationAreaCard";
import ObservationAreaMap from "./ObservationAreaMap";
import {ViewList, Map} from "@mui/icons-material";
import MapSidebar from "./MapSidebar";


function ObservationAreaOverview() {
    const [map, setMap] = useState(false);
    const {t} = useTranslation();
    const observationAreaRest = useMemo(() => new ObservationAreaRest(), []);
    const [observationAreas, setObservationAreas] = useState(null);
    const [selectedArea, setSelectedArea] = useState(null);

    const [openUpdateDialog, setOpenUpdateDialog] = useState(false);
    const [updateDialogMode, setUpdateDialogMode] = useState(ObservationAreaDialogMode.CREATE);
    const [openDeleteDialog, setOpenDeleteDialog] = useState(false);
    const [viewState, setViewState] = useState({
        longitude: 0,
        latitude: 36.7,
        zoom: 2.1,
        maxZoom: 20,
        pitch: 0,
        bearing: 0
    });

    function toggleView() {
        setMap(!map);
        setSelectedArea(null);
    }

    function openDialogWithMode(mode) {
        setUpdateDialogMode(mode);
        setOpenUpdateDialog(true);
    }

    function createArea() {
        setSelectedArea(null);
        openDialogWithMode(ObservationAreaDialogMode.CREATE);
    }

    function editArea(area) {
        setSelectedArea(area);
        openDialogWithMode(ObservationAreaDialogMode.UPDATE);
    }

    function copyArea(area) {
        setSelectedArea(area);
        openDialogWithMode(ObservationAreaDialogMode.COPY);
    }

    function promptDeleteArea(area) {
        setSelectedArea(area);
        setOpenDeleteDialog(true);
    }

    function deleteArea(area) {
        setOpenDeleteDialog(false);
        observationAreaRest.delete(area.id)
            .then(response => {
                reloadObservationAreas();
            });
    }

    useEffect(() => {
        reloadObservationAreas();
    }, []);

    function reloadObservationAreas() {
        observationAreaRest.findAll().then(response => {
            setObservationAreas(response.data);
            setViewState({
                longitude: response.data[0]?.centerlongitude ?? 10.786965,
                latitude: response.data[0]?.centerlatitude ?? 52.424249,
                zoom: 13,
                pitch: 0,
                bearing: 0
            });
        });
    }

    function onSelect(area) {
        setSelectedArea(area.object);
    }

    function renderMap() {
        if (map) {
            return (
                <ObservationAreaMap data={observationAreas} viewState={viewState} onLoad={reloadObservationAreas} onSelect={onSelect} />
            );
        }
        return null;
    }

    function renderObservationAreas() {
        if (!map) {
            if (!observationAreas) {
                return <LoadingSpinner message={t("observationAreas.loading")} />;
            }

            if (observationAreas.length === 0) {
                return <Typography variant="body1">{t("observationAreas.empty")}</Typography>;
            }

            return (
                <Grid container spacing={5} marginTop={3}>
                    {observationAreas?.map(area => (
                        <Grid size={{sm: 6, xs: 12}} key={area.id}>
                            <ObservationAreaCard
                                onEditClick={() => editArea(area)}
                                onDeleteClick={() => promptDeleteArea(area)}
                                onCopyClick={() => copyArea(area)}
                                observationArea={area} />
                        </Grid>
                    ))}
                </Grid>
            );
        }
        return null;
    }

    function renderToggleButton() {
        if (map) {
            return (
                <Tooltip title={t("button.viewlist")}>
                    <Button onClick={toggleView} variant="contained" color="primary"><ViewList /></Button>
                </Tooltip>
            );
        }
        return (
            <Tooltip title={t("button.map")}>
                <Button onClick={toggleView} variant="contained" color="primary"><Map /></Button>
            </Tooltip>
        );
    }

    function renderSidebar() {
        if (map && selectedArea != null) {
            return (<MapSidebar selected={selectedArea} observationAreas={observationAreas} editArea={editArea} copyArea={copyArea} deleteArea={promptDeleteArea} ></MapSidebar>);
        }
        return null;
    }

    return (
        <>
            {renderSidebar()}
            <Container>
                {renderMap()}
                <Stack direction="row" justifyContent="space-between">
                    <Typography zIndex={2} variant={"h2"} gutterBottom>
                        {t("observationAreas.title")}
                    </Typography>
                    {renderToggleButton()}
                </Stack>
                {renderObservationAreas()}
                <ConfirmationDialog
                    title={t("observationArea.delete.title")}
                    message={t("observationArea.delete.message")}
                    open={openDeleteDialog}
                    onClose={() => setOpenDeleteDialog(false)}
                    onSubmit={() => deleteArea(selectedArea)}
                    confirmTitle={t("button.delete")}
                />
                <ObservationAreaDialog
                    open={openUpdateDialog}
                    onSubmit={() => setOpenUpdateDialog(false)}
                    mode={updateDialogMode}
                    selectedArea={selectedArea}
                    update={reloadObservationAreas}
                />
                <AddFabButton onClick={createArea} />
            </Container>
        </>
    );
}

export default ObservationAreaOverview;
