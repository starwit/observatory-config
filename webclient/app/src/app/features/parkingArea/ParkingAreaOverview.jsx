import {Container, Typography, Button, Stack} from "@mui/material";
import React, {useState, useMemo, useEffect} from "react";
import {useTranslation} from "react-i18next";
import {OverviewTable} from "@starwit/react-starwit";
import ParkingAreaRest from "../../services/ParkingAreaRest";
import {useHistory} from "react-router-dom";
import {parkingAreaOverviewFields} from "../../modifiers/ParkingAreaModifier";

function ParkingAreaOverview() {
    const [selected, setSelected] = useState(undefined);
    const {t} = useTranslation();
    const parkingareaRest = useMemo(() => new ParkingAreaRest(), []);
    const history = useHistory();
    const [parkingAreaAll, setParkingAreaAll] = useState();

    useEffect(() => {
        reload();
    }, []);

    function reload() {
        parkingareaRest.findAll().then(response => {
            setParkingAreaAll(response.data);
        });
    }

    function goToCreate() {
        history.push("/parkingarea/create");
    }

    function goToUpdate() {
        if (!!selected) {
            history.push("/parkingarea/update/" + selected.id);
            setSelected(undefined);
        }
    }

    function handleDelete() {
        if (!!selected) {
            parkingareaRest.delete(selected.id).then(reload);
            setSelected(undefined);
        }
    }

    return (
        <Container>
            <Typography variant={"h2"} gutterBottom>{t("parkingArea.title")}</Typography>
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
                entities={parkingAreaAll}
                prefix={"parkingArea"}
                selected={selected}
                onSelect={setSelected}
                fields={parkingAreaOverviewFields}/>
        </Container>
    );
}

export default ParkingAreaOverview;
