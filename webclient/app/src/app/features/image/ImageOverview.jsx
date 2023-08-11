import {Container, Typography, Button, Stack} from "@mui/material";
import React, {useState, useMemo, useEffect} from "react";
import {useTranslation} from "react-i18next";
import {OverviewTable} from "@starwit/react-starwit";
import ImageRest from "../../services/ImageRest";
import {useHistory} from "react-router-dom";
import {imageOverviewFields} from "../../modifiers/ImageModifier";

function ImageOverview() {
    const [selected, setSelected] = useState(undefined);
    const {t} = useTranslation();
    const imageRest = useMemo(() => new ImageRest(), []);
    const history = useHistory();
    const [imageAll, setImageAll] = useState();

    useEffect(() => {
        reload();
    }, []);

    function reload() {
        imageRest.findAll().then(response => {
            setImageAll(response.data);
        });
    }

    function goToCreate() {
        history.push("/image/create");
    }

    function goToUpdate() {
        if (!!selected) {
            history.push("/image/update/" + selected.id);
            setSelected(undefined);
        }
    }

    function handleDelete() {
        if (!!selected) {
            imageRest.delete(selected.id).then(reload);
            setSelected(undefined);
        }
    }

    return (
        <Container>
            <Typography variant={"h2"} gutterBottom>{t("image.title")}</Typography>
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
                entities={imageAll}
                prefix={"image"}
                selected={selected}
                onSelect={setSelected}
                fields={imageOverviewFields}/>
        </Container>
    );
}

export default ImageOverview;
