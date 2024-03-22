import {Container, Grid, Typography} from "@mui/material";
import AddFabButton from "../../commons/addFabButton/AddFabButton";
import LoadingSpinner from "../../commons/loadingSpinner/LoadingSpinner";
import React, {useCallback, useEffect, useMemo, useState} from "react";
import {useTranslation} from "react-i18next";
import {useNavigate} from "react-router";
import ObservationAreaRest from "../../services/ObservationAreaRest";
import ObservationAreaCard from "./ObservationAreaCard";
import ObservationAreaDialog from "./ObservationAreaDialog";

function ObservationAreaOverview() {
    const {t} = useTranslation();
    const navigate = useNavigate();
    const observationAreaRest = useMemo(() => new ObservationAreaRest(), []);
    const [observationAreas, setObservationAreas] = useState(null);
    const [openUpdateDialog, setOpenUpdateDialog] = useState(false);


    const loadObservationAreas = useCallback(() => {
        setObservationAreas(null);
        observationAreaRest.findAll().then(response => {
            setObservationAreas(response.data);
        });
    }, [observationAreaRest, setObservationAreas]);

    useEffect(() => {
        loadObservationAreas();
    }, [loadObservationAreas]);

    function update() {
        loadObservationAreas();
    };

    function deleteById(id) {
        return observationAreaRest.delete(id)
            .then(response => {
                setObservationAreas(null);
                loadObservationAreas();
            });
    }

    function renderObservationAreas() {
        if (!observationAreas) {
            return <LoadingSpinner message={t("observationAreas.loading")} />;
        }

        if (observationAreas.length === 0) {
            return (
                null
            );
        }

        return (
            <Grid container spacing={5}>
                {observationAreas?.map(area => (
                    <Grid item sm={6} xs={12} key={area.id}>
                        <ObservationAreaCard
                            onEditClick={update}
                            onDeleteClick={deleteById}
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
            <ObservationAreaDialog
                open={openUpdateDialog}
                onClose={() => setOpenUpdateDialog(false)}
                isCreate={true}
                selected={null}
                update={() => update()}
            />
            <AddFabButton onClick={() => setOpenUpdateDialog(true)} />
        </Container>
    );
}

export default ObservationAreaOverview;
