
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
} from "../../modifiers/ObservationAreaModifier";
import ObservationAreaRest from "../../services/ObservationAreaRest";
import ObservationAreaDialog, { MODE as ObservationAreaDialogMode } from "./ObservationAreaDialog";

function ObservationAreaSelect() {
    const [selectedArea, setSelectedArea] = useImmer(entityDefault);
    const observationareaRest = useMemo(() => new ObservationAreaRest(), [entityDefault]);
    const [observationAreaAll, setObservationAreaAll] = useImmer([]);
    const [openDialog, setOpenDialog] = React.useState(false);
    const [isCreate, setIsCreate] = React.useState(false);
    const {t} = useTranslation();
    const {observationAreaId} = useParams();
    const nav = "/observationarea/";
    const navigate = useNavigate();
    const [track, setTrack] = useState(false);
    const [startTrack, setStartTrack] = useState(false);

    useEffect(() => {
        reload();
    }, [observationAreaId]);

    function reload() {
        observationareaRest.findAll().then(response => {
            const loadedAreas = response.data;
            setObservationAreaAll(loadedAreas);

            const preselectedArea = loadedAreas.find(entity => entity.id === parseInt(observationAreaId));
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

    const handleChange = event => {
        const newObservationAreaId = event.target.value;
        navigate(`${nav}${newObservationAreaId}`);
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
                    {observationAreaAll.map(entity => (
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
                title={t("observationArea.track.title")}
                message={t("observationArea.track.message")}
                open={track}
                onClose={() => {setTrack(false);}}
                onSubmit={() => {setTrack(false); toggleTrack();}}
                confirmTitle={t("button.submit")}
            />           
            </FormControl>
            <ObservationAreaDialog
                open={openDialog}
                onSubmit={handleDialogClose}
                mode={ObservationAreaDialogMode.UPDATE}
                selectedArea={selectedArea}
                update={reload}
            />
        </Stack >
    );
}


export default ObservationAreaSelect;
