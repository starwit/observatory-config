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

function ParkingAreaSelect() {
    const [selected, setSelected] = useImmer({});
    const parkingareaRest = useMemo(() => new ParkingAreaRest(), []);
    const [parkingAreaAll, setParkingAreaAll] = useState([]);
    const [openDialog, setOpenDialog] = React.useState(false);
    const [isCreate, setIsCreate] = React.useState(false);

    useEffect(() => {
        reload();
    }, []);

    function reload() {
        parkingareaRest.findAll().then(response => {
            setParkingAreaAll(response.data);
            if (response?.data[0]) {
                let currEntity = response?.data.find(entity => entity.id === 1);
                if (currEntity) {
                    setSelected(currEntity);
                } else {
                    setSelected(response?.data[0]);
                }
            }
        });
    }


    function handleDelete() {
        if (!!selected) {
            parkingareaRest.delete(selected.id).then(reload);
            setSelected(undefined);
        }
    }

    const handleChange = event => {
        setSelected(event.target.value);
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
                <InputLabel>ParkingArea</InputLabel>
                <Select value={selected} label="ParkingArea" onChange={handleChange}>
                    {parkingAreaAll.map(entity => (
                        <MenuItem key={entity.id} value={entity} >{entity.name}</MenuItem>))}
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
                id={selected?.id}
                isCreate={isCreate}
                selected={selected}
                setSelected={setSelected}
            />
        </>
    );
}

export default ParkingAreaSelect;