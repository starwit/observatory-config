import {Container, Typography, Button, Stack} from "@mui/material";
import React, {useState, useMemo, useEffect} from "react";
import {useTranslation} from "react-i18next";
import {OverviewTable} from "@starwit/react-starwit";
import ClassificationRest from "../../services/ClassificationRest";
import {useHistory} from "react-router-dom";
import {classificationOverviewFields} from "../../modifiers/ClassificationModifier";

function ClassificationOverview() {
    const [selected, setSelected] = useState(undefined);
    const {t} = useTranslation();
    const classificationRest = useMemo(() => new ClassificationRest(), []);
    const history = useHistory();
    const [classificationAll, setClassificationAll] = useState();

    useEffect(() => {
        reload();
    }, []);

    function reload() {
        classificationRest.findAll().then(response => {
            setClassificationAll(response.data);
        });
    }

    function goToCreate() {
        history.push("/classification/create");
    }

    function goToUpdate() {
        if (!!selected) {
            history.push("/classification/update/" + selected.id);
            setSelected(undefined);
        }
    }

    function handleDelete() {
        if (!!selected) {
            classificationRest.delete(selected.id).then(reload);
            setSelected(undefined);
        }
    }

    return (
        <Container>
            <Typography variant={"h2"} gutterBottom>{t("classification.title")}</Typography>
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
                entities={classificationAll}
                prefix={"classification"}
                selected={selected}
                onSelect={setSelected}
                fields={classificationOverviewFields}/>
        </Container>
    );
}

export default ClassificationOverview;
