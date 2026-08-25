import {ContentCopy, Delete, Edit} from "@mui/icons-material";
import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
import {Accordion, AccordionDetails, AccordionSummary, Box, IconButton, Tooltip, Typography} from "@mui/material";
import React, {useEffect} from 'react';
import {useTranslation} from "react-i18next";
import MapStyles from "../../assets/styles/MapStyles";
import ObservationAreaPreview from './ObservationAreaPreview';


export default function MapSidebar(props) {
    const {selected, observationAreas, editArea, copyArea, deleteArea} = props;
    const [expanded, setExpanded] = React.useState(selected.id);
    const {t} = useTranslation();

    useEffect(() => {
        setExpanded(selected.id);
    }, [selected]);
    function handleChange(panel) {
        return function change(event, newExpanded) {
            setExpanded(newExpanded ? panel : false);
        }
    }

    function isNearby(area1, area2) {
        const distance = 0.0005;
        if (area1 < area2 + distance && area1 > area2 - distance) {
            return true;
        }
        return false;
    }

    return (
        <Box sx={MapStyles.innerBox}>
            {
                observationAreas?.map(area => {
                    if (isNearby(area.centerlongitude, selected.centerlongitude) && isNearby(area.centerlatitude, selected.centerlatitude)) {
                        return (
                            <Accordion
                                key={area.id}
                                disableGutters
                                sx={{
                                    boxShadow: expanded === area.id ? '0 8px 24px rgba(0, 0, 0, 0.16)' : 0,
                                    backgroundColor: expanded === area.id
                                        ? 'white'
                                        : area.processingEnabled
                                            ? 'rgba(217, 113, 69, 0.6)'
                                            : 'rgba(180, 180, 180, 0.6)',
                                    borderLeft: expanded === area.id && area.processingEnabled ? '4px solid rgba(215, 93, 42, 0.7)' : '4px solid transparent',
                                    transition: 'background-color 0.2s ease-in-out',
                                }}
                                onChange={handleChange(area.id)}
                                expanded={expanded === area.id}
                            >
                                <AccordionSummary
                                    expandIcon={<ExpandMoreIcon />}
                                    sx={{
                                        '& .MuiAccordionSummary-content': {
                                            width: '100%',
                                            margin: 0,
                                        },
                                    }}
                                >
                                    <Box sx={{display: 'flex', alignItems: 'center', justifyContent: 'space-between', width: '100%'}}>
                                        <Typography sx={{...MapStyles.title, textAlign: 'left', flex: 1}}>
                                            {area.name}
                                        </Typography>
                                        {expanded === area.id && (
                                            <Box sx={{display: 'flex', alignItems: 'center'}}>
                                                <Tooltip title={t("button.copy")}>
                                                    <IconButton
                                                        component="span"
                                                        onClick={(event) => {
                                                            event.stopPropagation();
                                                            copyArea(area);
                                                        }}
                                                    >
                                                        <ContentCopy fontSize={"small"} />
                                                    </IconButton>
                                                </Tooltip>
                                                <Tooltip title={t("button.update")}>
                                                    <IconButton
                                                        component="span"
                                                        onClick={(event) => {
                                                            event.stopPropagation();
                                                            editArea(area);
                                                        }}
                                                    >
                                                        <Edit fontSize={"small"} />
                                                    </IconButton>
                                                </Tooltip>
                                                <Tooltip title={t("button.delete")}>
                                                    <IconButton
                                                        component="span"
                                                        onClick={(event) => {
                                                            event.stopPropagation();
                                                            deleteArea(area);
                                                        }}
                                                    >
                                                        <Delete fontSize={"small"} />
                                                    </IconButton>
                                                </Tooltip>
                                            </Box>
                                        )}
                                    </Box>
                                </AccordionSummary>
                                <AccordionDetails sx={{padding: 0, }}>
                                    <ObservationAreaPreview observationArea={area} />
                                </AccordionDetails>
                            </Accordion>);
                    }
                })
            }
        </Box>
    )
}
