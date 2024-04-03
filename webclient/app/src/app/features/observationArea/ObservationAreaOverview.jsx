import {Container, Grid, Typography, Box} from "@mui/material";
import React, {useEffect, useMemo, useState, useCallback} from "react";
import {useTranslation} from "react-i18next";
import AddFabButton from "../../commons/addFabButton/AddFabButton";
import ConfirmationDialog from "../../commons/dialog/ConfirmationDialog";
import LoadingSpinner from "../../commons/loadingSpinner/LoadingSpinner";
import ObservationAreaRest from "../../services/ObservationAreaRest";
import ObservationAreaDialog, {MODE as ObservationAreaDialogMode} from "./ObservationAreaDialog";
import ObservationAreaCard from "./ObservationAreaCard";
import ObservationAreaMap from "./ObservationAreaMap";
import {PickingInfo} from '@deck.gl/core';

function ObservationAreaOverview() {
    const {t} = useTranslation();
    const observationAreaRest = useMemo(() => new ObservationAreaRest(), []);
    const [observationAreas, setObservationAreas] = useState(null);
    const [selectedArea, setSelectedArea] = useState(null);

    const [openUpdateDialog, setOpenUpdateDialog] = useState(false);
    const [updateDialogMode, setUpdateDialogMode] = useState(ObservationAreaDialogMode.CREATE);
    const [openDeleteDialog, setOpenDeleteDialog] = useState(false);
    const [selected, setSelected] = useState(undefined);
    const [viewState, setViewState] = useState({
        longitude: 0,
        latitude: 36.7,
        zoom: 2.1,
        maxZoom: 20,
        pitch: 0,
        bearing: 0
    });

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
            response.data.map(area => {area.coordinates = [area.centerlongitude, area.centerlatitude]});
            setObservationAreas(response.data);
            console.log(response.data);

            setViewState({
                longitude: response.data[0].centerlongitude,
                latitude: response.data[0].centerlatitude,
                zoom: 15,
                pitch: 0,
                bearing: 0
            });
        });
    }

    function onSelect(area) {
        setSelected(area.object);
    }

    function renderMap() {
        return (
            <ObservationAreaMap data={observationAreas} viewState={viewState} onLoad={reloadObservationAreas} onSelect={onSelect} />
        );
    }

    function renderObservationAreas() {
        if (!observationAreas) {
            return <LoadingSpinner message={t("observationAreas.loading")} />;
        }

        if (observationAreas.length === 0) {
            return <Typography variant="body1">{t("observationAreas.empty")}</Typography>;
        }

        return (
            <Grid container spacing={5}>
                {observationAreas?.map(area => (
                    <Grid item sm={6} xs={12} key={area.id}>
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

    return (
        <Container>
            <Typography variant={"h2"} gutterBottom>
                {t("observationAreas.title")}
            </Typography>
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
            <Box sx={{height: '50vh', position: 'relative', top: 40, boxShadow: 10}}>
                {renderMap()}
            </Box>
        </Container>
    );
}

export default ObservationAreaOverview;
