import React, {useState, useMemo, useEffect} from "react";
import {useNavigate, useLocation} from "react-router-dom";
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
import {
    entityDefault
} from "../../modifiers/ParkingAreaModifier";
import {useTranslation} from "react-i18next";
import {UploadFile} from "@mui/icons-material";

function deriveIdFromLocation(location) {
    const m = location.pathname.match(/^\/(\d+)$/);
    if (m === null) {
        return undefined;
    }
    return parseInt(m[1]);
}

function ParkingAreaSelect() {
    const [selectedArea, setSelectedArea] = useImmer(entityDefault);
    const parkingareaRest = useMemo(() => new ParkingAreaRest(), [entityDefault]);
    const [parkingAreaAll, setParkingAreaAll] = useImmer([]);
    const [openDialog, setOpenDialog] = React.useState(false);
    const [isCreate, setIsCreate] = React.useState(false);
    const {t} = useTranslation();
    const navigate = useNavigate();
    const locationId = deriveIdFromLocation(useLocation());

    useEffect(() => {
        reload();
    }, []);

    function reload() {
        parkingareaRest.findAll().then(response => {
            const loadedAreas = response.data;
            setParkingAreaAll(loadedAreas);

            const preselectedArea = loadedAreas.find(entity => entity.id === locationId);
            if (preselectedArea !== undefined) {
                setSelectedArea(preselectedArea);
            } else {
                navigate(`/${loadedAreas[0].id}`);
            }
        });
    }

    function update(modifiedEntity) {
        if (isCreate) {
            parkingareaRest.findAll().then(response => {
                navigate(`${modifiedEntity.id}`);
            });
        } else {
            reload();
            navigate(`${modifiedEntity.id}`);
        }
    }

    function handleDelete() {
        if (!!selectedArea) {
            parkingareaRest.delete(selectedArea.id).then(response => {
                parkingareaRest.findAll().then(response => {
                    navigate(`/${response.data[0].id}`);
                });
            });
        }
    }

    const handleChange = event => {
        const newParkingAreaId = event.target.value;
        navigate(`/${newParkingAreaId}`);
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
                <Select value={selectedArea.id} label={t("parkingArea.title")} onChange={handleChange}>
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
                selected={selectedArea}
                update={update}
            />
        </>
    );
}

export default ParkingAreaSelect;
