import React, {useState, useMemo, useEffect} from "react";
import ParkingAreaRest from "../../../services/ParkingAreaRest";
import {useHistory} from "react-router-dom";
import Select from "@mui/material/Select";
import MenuItem from "@mui/material/MenuItem";
import InputLabel from "@mui/material/InputLabel";
import FormControl from "@mui/material/FormControl";
import AddCircleIcon from "@mui/icons-material/AddCircle";
import DeleteIcon from '@mui/icons-material/Delete';
import EditRoundedIcon from "@mui/icons-material/EditRounded";
import {
    IconButton,
    Typography
} from "@mui/material";

function ParkingAreaSelect() {
    const [selected, setSelected] = React.useState({});
    const parkingareaRest = useMemo(() => new ParkingAreaRest(), []);
    const history = useHistory();
    const [parkingAreaAll, setParkingAreaAll] = useState([]);

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

    const handleChange = event => {
        setSelected(event.target.value);
        history.push("/parkingarea/update/" + event.target.value.id);
    };

    return (
        <FormControl fullWidth>
            <InputLabel>ParkingArea</InputLabel>
            <Select value={selected} label="ParkingArea" onChange={handleChange}>
                {parkingAreaAll.map(entity => (
                    <MenuItem key={entity.id} value={entity} >{entity.name}</MenuItem> ))}
            </Select>
            <Typography align="right">
                <IconButton onClick={goToCreate}>
                    <AddCircleIcon/>
                </IconButton>
                <IconButton onClick={goToUpdate}>
                    <EditRoundedIcon/>
                </IconButton>
                <IconButton onClick={handleDelete}>
                    <DeleteIcon/>
                </IconButton>
            </Typography>
        </FormControl>);
}

export default ParkingAreaSelect;
