import {Container, Typography, Button, Stack} from "@mui/material";
import React, {useState, useMemo, useEffect} from "react";
import {useTranslation} from "react-i18next";
import {OverviewTable} from "@starwit/react-starwit";
import PointRest from "../../services/PointRest";
import {useNavigate} from "react-router-dom";
import {pointOverviewFields} from "../../modifiers/PointModifier";

function PointOverview() {
    const [selected, setSelected] = useState(undefined);
    const {t} = useTranslation();
    const pointRest = useMemo(() => new PointRest(), []);
    const navigate = useNavigate();
    const [pointAll, setPointAll] = useState();

    useEffect(() => {
        reload();
    }, []);

    function reload() {
        pointRest.findAll().then(response => {
            setPointAll(response.data);
        });
    }

    function goToCreate() {
        navigate("/point/create");
    }

    function goToUpdate() {
        if (!!selected) {
            navigate("/point/update/" + selected.id);
            setSelected(undefined);
        }
    }

    function handleDelete() {
        if (!!selected) {
            pointRest.delete(selected.id).then(reload);
            setSelected(undefined);
        }
    }

    return (
        <Container>
            <Typography variant={"h2"} gutterBottom>{t("point.title")}</Typography>
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
                entities={pointAll}
                prefix={"point"}
                selected={selected}
                onSelect={setSelected}
                fields={pointOverviewFields}/>
        </Container>
    );
}

export default PointOverview;
