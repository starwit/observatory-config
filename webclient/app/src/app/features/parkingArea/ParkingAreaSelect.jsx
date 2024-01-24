import React, {useState, useMemo, useEffect} from "react";
import ParkingAreaRest from "../../services/ParkingAreaRest";
import Select from "@mui/material/Select";
import MenuItem from "@mui/material/MenuItem";
import InputLabel from "@mui/material/InputLabel";
import FormControl from "@mui/material/FormControl";
import AddCircleIcon from "@mui/icons-material/AddCircle";
import DeleteIcon from "@mui/icons-material/Delete";
import EditRoundedIcon from "@mui/icons-material/EditRounded";
import {
    IconButton,
    Typography
} from "@mui/material";
import ParkingAreaDialog from "./ParkingAreaDialog";
import {useImmer} from "use-immer";
import {produce} from "immer";
import {
    entityDefault
} from "../../modifiers/ParkingAreaModifier";
import {useTranslation} from "react-i18next";

function ParkingAreaSelect() {
    const [selected, setSelected] = useImmer(entityDefault);
    const parkingareaRest = useMemo(() => new ParkingAreaRest(), [entityDefault]);
    const [parkingAreaAll, setParkingAreaAll] = useImmer([]);
    const [openDialog, setOpenDialog] = React.useState(false);
    const [isCreate, setIsCreate] = React.useState(false);
    const {t} = useTranslation();

    useEffect(() => {
        reload();
    }, []);

    function reload() {
        parkingareaRest.findAll().then(response => {
            setParkingAreaAll(response.data);
            if (selected?.id == "") {setSelected(response.data[0]);}
        });
    }

    function update(modifiedEntity) {
        if (isCreate) {
            parkingareaRest.findAll().then(response => {
                setParkingAreaAll(response.data);
                const index = response.data.findIndex(entity => entity.name === modifiedEntity.name);
                setSelected(response.data[index]);
                setIsCreate(false);
            });
        } else {
            reload();
            setSelected(updateSelected(modifiedEntity));
            setParkingAreaAll(updateParkingAreaAll(modifiedEntity));
        }
    }

    function updateParkingAreaAll(modifiedEntity) {
        return produce(parkingAreaAll, draft => {
            const index = draft.findIndex(entity => entity.id === selected.id);
            if (index !== -1) {
                draft[index] = modifiedEntity;
            }
        });
    }

    function updateSelected(modifiedEntity) {
        return produce(selected, draft => {
            draft.name = modifiedEntity.name;
        });
    }

    function handleDelete() {
        if (!!selected) {
            parkingareaRest.delete(selected.id).then(response => {
                parkingareaRest.findAll().then(response => {
                    setParkingAreaAll(response.data);
                    setSelected(response.data[0]);
                });
            });
        }
    }

    const handleChange = event => {
        const value = parkingAreaAll.find(entity => entity.id === event.target.value);
        setSelected(value);
    };

    function handleDialogOpen() {
        setOpenDialog(true);
        setIsCreate(false);
    }

    function handleCreateDialogOpen() {
        setOpenDialog(true);
        setIsCreate(true);
    }

    function handleDialogClose() {
        reload();
        setOpenDialog(false);
    }

    return (
        <>
            <FormControl fullWidth>
                <InputLabel>{t("parkingArea.title")}</InputLabel>
                <Select value={selected.id} label={t("parkingArea.title")} onChange={handleChange}>
                    {parkingAreaAll.map(entity => (
                        <MenuItem key={entity.id} value={entity.id} >{entity.name}</MenuItem>))}
                </Select>
                <Typography align="right">
                    <IconButton onClick={handleCreateDialogOpen}>
                        <AddCircleIcon />
                    </IconButton>
                    <IconButton onClick={handleDialogOpen}>
                        <EditRoundedIcon />
                    </IconButton>
                    <IconButton onClick={handleDelete}>
                        <DeleteIcon />
                    </IconButton>
                </Typography>
            </FormControl >
            <ParkingAreaDialog
                open={openDialog}
                onClose={handleDialogClose}
                isCreate={isCreate}
                selected={selected}
                update={update}
            />
        </>
    );
}

export default ParkingAreaSelect;
