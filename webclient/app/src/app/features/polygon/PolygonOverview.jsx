import {Container, Typography, Button, Stack} from "@mui/material";
import React, {useState, useMemo, useEffect} from "react";
import {useTranslation} from "react-i18next";
import {OverviewTable} from "@starwit/react-starwit";
import PolygonRest from "../../services/PolygonRest";
import {useHistory} from "react-router-dom";
import {polygonOverviewFields} from "../../modifiers/PolygonModifier";

function PolygonOverview() {
    const [selected, setSelected] = useState(undefined);
    const {t} = useTranslation();
    const polygonRest = useMemo(() => new PolygonRest(), []);
    const history = useHistory();
    const [polygonAll, setPolygonAll] = useState();

    useEffect(() => {
        reload();
    }, []);

    function reload() {
        polygonRest.findAll().then(response => {
            setPolygonAll(response.data);
        });
    }

    function goToCreate() {
        history.push("/polygon/create");
    }

    function goToUpdate() {
        if (!!selected) {
            history.push("/polygon/update/" + selected.id);
            setSelected(undefined);
        }
    }

    function handleDelete() {
        if (!!selected) {
            polygonRest.delete(selected.id).then(reload);
            setSelected(undefined);
        }
    }

    return (
        <Container>
            <Typography variant={"h2"} gutterBottom>{t("polygon.title")}</Typography>
            <Stack spacing={2} direction={"row"}>
                <Button onClick={goToCreate} variant="contained" color="secondary">{t("button.create")}</Button>
                <Button onClick={goToUpdate} variant="contained" color="secondary" disabled={!selected?.id} >
                    {t("button.update")}
                </Button>
                <Button onClick={handleDelete} variant="contained" color="secondary" disabled={!selected?.id}>
                    {t("button.delete")}
                </Button>
            </Stack>
            <OverviewTable
                entities={polygonAll}
                prefix={"polygon"}
                selected={selected}
                onSelect={setSelected}
                fields={polygonOverviewFields}/>
        </Container>
    );
}

export default PolygonOverview;
