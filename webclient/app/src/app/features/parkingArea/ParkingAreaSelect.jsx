
import { Home } from "@mui/icons-material";
import EditRoundedIcon from "@mui/icons-material/EditRounded";
import PlayCircleFilledWhiteIcon from '@mui/icons-material/PlayCircleFilledWhite';
import StopCircleIcon from '@mui/icons-material/StopCircle';
import {
    IconButton,
    Stack
} from "@mui/material";
import FormControl from "@mui/material/FormControl";
import MenuItem from "@mui/material/MenuItem";
import Select from "@mui/material/Select";
import React, { useEffect, useMemo, useState } from "react";
import { useTranslation } from "react-i18next";
import { useNavigate, useParams } from "react-router-dom";
import { useImmer } from "use-immer";
import ConfirmationDialog from "../../commons/dialog/ConfirmationDialog";
import {
    entityDefault
} from "../../modifiers/ParkingAreaModifier";
import ParkingAreaRest from "../../services/ParkingAreaRest";
import ParkingAreaDialog from "./ParkingAreaDialog";

function ParkingAreaSelect() {
    const [selectedArea, setSelectedArea] = useImmer(entityDefault);
    const parkingareaRest = useMemo(() => new ParkingAreaRest(), [entityDefault]);
    const [parkingAreaAll, setParkingAreaAll] = useImmer([]);
    const [openDialog, setOpenDialog] = React.useState(false);
    const [isCreate, setIsCreate] = React.useState(false);
    const {t} = useTranslation();
    const {parkingAreaId} = useParams();
    const nav = "/parkingarea/";
    const navigate = useNavigate();
    const [track, setTrack] = useState(false);
    const [startTrack, setStartTrack] = useState(false);

    useEffect(() => {
        reload();
    }, [parkingAreaId]);

    function reload() {
        parkingareaRest.findAll().then(response => {
            const loadedAreas = response.data;
            setParkingAreaAll(loadedAreas);

            const preselectedArea = loadedAreas.find(entity => entity.id === parseInt(parkingAreaId));
            if (preselectedArea !== undefined) {
                setSelectedArea(preselectedArea);
            } else {
                navigate(`${nav}${loadedAreas[0].id}`);
            }
        });
    }

    function home() {
        navigate("/");
    }

    function update(modifiedEntity) {
        if (isCreate) {
            parkingareaRest.findAll().then(response => {
                navigate(`${nav}${modifiedEntity.id}`);
            });
        } else {
            navigate(`${nav}${modifiedEntity.id}`);
        }
    }

    const handleChange = event => {
        const newParkingAreaId = event.target.value;
        navigate(`${nav}${newParkingAreaId}`);
    };

    function handleDialogOpen() {
        setOpenDialog(true);
        setIsCreate(false);
    }

    function handleDialogClose() {
        reload();
        setOpenDialog(false);
    }

    function toggleTrack() {
        setStartTrack(!startTrack);
    }

    return (
        <Stack
            direction="row"
            sx={{marginTop: "0.4rem", height: "2.2rem", color: "dimgrey"}}
            useFlexGap
            flexWrap="nowrap"
        >
            <FormControl sx={{paddingLeft: "0.5rem"}}>
                <IconButton sx={{height: "2rem"}}
                    onClick={home}>
                    <Home fontSize="small" />
                </IconButton>
            </FormControl>
            <FormControl sx={{boxShadow: "none", width: "20rem"}}>
                <Select sx={{height: "2rem", margin: "0rem"}}
                    value={selectedArea.id} onChange={handleChange}>
                    {parkingAreaAll.map(entity => (
                        <MenuItem sx={{margin: "0rem"}}
                            key={entity.id} value={entity.id} >{entity.name}</MenuItem>))}
                </Select>
            </FormControl >
            <FormControl>
                <IconButton sx={{height: "2rem"}}
                    onClick={handleDialogOpen}>
                    <EditRoundedIcon fontSize="small" />
                </IconButton>
            </FormControl>

            {/* TRACKING BUTTON */}
            <FormControl>
            <IconButton sx={{height: "2rem"}}
                onClick={() => setTrack(!track)}>
                {startTrack ? <StopCircleIcon fontSize="small" color="error" /> : <PlayCircleFilledWhiteIcon fontSize="small" />} 
            </IconButton>
            <ConfirmationDialog
                title={t("parkingArea.track.title")}
                message={t("parkingArea.track.message")}
                open={track}
                onClose={() => {setTrack(false);}}
                onSubmit={() => {setTrack(false); toggleTrack();}}
                confirmTitle={t("button.submit")}
            />           
            </FormControl>
            <ParkingAreaDialog
                open={openDialog}
                onClose={handleDialogClose}
                isCreate={isCreate}
                selected={selectedArea}
                update={update}
            />
        </Stack >
    );
}


export default ParkingAreaSelect;
