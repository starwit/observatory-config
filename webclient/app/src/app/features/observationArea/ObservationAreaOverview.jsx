import { Container, Grid, Typography } from "@mui/material";
import React, { useCallback, useEffect, useMemo, useState } from "react";
import { useTranslation } from "react-i18next";
import AddFabButton from "../../commons/addFabButton/AddFabButton";
import ConfirmationDialog from "../../commons/dialog/ConfirmationDialog";
import LoadingSpinner from "../../commons/loadingSpinner/LoadingSpinner";
import ObservationAreaRest from "../../services/ObservationAreaRest";
import ObservationAreaCard from "./ObservationAreaCard";
import ObservationAreaDialog, { MODE as ObservationAreaDialogMode } from "./ObservationAreaDialog";

function ObservationAreaOverview() {
    const {t} = useTranslation();
    const observationAreaRest = useMemo(() => new ObservationAreaRest(), []);
    const [observationAreas, setObservationAreas] = useState(null);
    const [selectedArea, setSelectedArea] = useState(null);

    const [openUpdateDialog, setOpenUpdateDialog] = useState(false);
    const [updateDialogMode, setUpdateDialogMode] = useState(ObservationAreaDialogMode.CREATE);
    const [openDeleteDialog, setOpenDeleteDialog] = useState(false);

    function openDialogWithMode(mode) {
        setUpdateDialogMode(mode);
        setOpenUpdateDialog(true);
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
                setObservationAreas(null);
                loadObservationAreas();
            });
    }

    const loadObservationAreas = useCallback(() => {
        setObservationAreas(null);
        observationAreaRest.findAll().then(response => {
            setObservationAreas(response.data);
        });
    }, [observationAreaRest, setObservationAreas]);

    useEffect(() => {
        loadObservationAreas();
    }, [loadObservationAreas]);

    function handleUpdate() {
        loadObservationAreas();
    };


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
                update={handleUpdate}
            />
            <AddFabButton onClick={() => openDialogWithMode(ObservationAreaDialogMode.CREATE)} />
        </Container>
    );
}

export default ObservationAreaOverview;
