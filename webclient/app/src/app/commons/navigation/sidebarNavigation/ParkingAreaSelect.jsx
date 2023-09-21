import React, {useState, useMemo, useEffect} from "react";
import ParkingAreaRest from "../../../services/ParkingAreaRest";
import {useHistory} from "react-router-dom";
import Select from "@mui/material/Select";
import MenuItem from "@mui/material/MenuItem";
import InputLabel from "@mui/material/InputLabel";
import FormControl from "@mui/material/FormControl";

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
        </FormControl>);
}

export default ParkingAreaSelect;
