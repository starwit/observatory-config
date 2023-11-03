import {Container, Typography, Button, Stack} from "@mui/material";
import React, {useState, useMemo, useEffect} from "react";
import {useTranslation} from "react-i18next";
import {OverviewTable} from "@starwit/react-starwit";
import ParkingConfigRest from "../../services/ParkingConfigRest";
import {useNavigate} from "react-router-dom";
import {parkingConfigOverviewFields} from "../../modifiers/ParkingConfigModifier";

function ParkingConfigOverview() {
    const [selected, setSelected] = useState(undefined);
    const {t} = useTranslation();
    const parkingconfigRest = useMemo(() => new ParkingConfigRest(), []);
    const navigate = useNavigate();
    const [parkingConfigAll, setParkingConfigAll] = useState();

    useEffect(() => {
        reload();
    }, []);

    function reload() {
        parkingconfigRest.findAll().then(response => {
            setParkingConfigAll(response.data);
        });
    }

    function goToCreate() {
        navigate("/parkingconfig/create");
    }

    function goToUpdate() {
        if (!!selected) {
            navigate("/parkingconfig/update/" + selected.id);
            setSelected(undefined);
        }
    }

    function handleDelete() {
        if (!!selected) {
            parkingconfigRest.delete(selected.id).then(reload);
            setSelected(undefined);
        }
    }

    return (
        <Container>
            <Typography variant={"h2"} gutterBottom>{t("parkingConfig.title")}</Typography>
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
                entities={parkingConfigAll}
                prefix={"parkingConfig"}
                selected={selected}
                onSelect={setSelected}
                fields={parkingConfigOverviewFields}/>
        </Container>
    );
}

export default ParkingConfigOverview;
