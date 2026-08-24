import React, {useEffect} from 'react'
import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
import {ContentCopy, Delete, Edit, QueryStats, Camera, Dashboard, Link} from "@mui/icons-material";
import {CardMedia, CardActionArea, CardContent, Typography, IconButton, Box, Accordion, AccordionSummary, AccordionDetails, Tooltip, ImageListItemBar, Button} from "@mui/material";
import {Grid} from '@mui/material';
import {imageFileUrlForId} from "../../services/ImageRest";
import {useNavigate} from "react-router-dom";
import MapStyles from "../../assets/styles/MapStyles";
import {useTranslation} from "react-i18next";


export default function MapSidebar(props) {
    const {selected, observationAreas, editArea, copyArea, deleteArea} = props;
    const navigate = useNavigate();
    const [expanded, setExpanded] = React.useState(selected.id);
    const {t} = useTranslation();

    useEffect(() => {
        setExpanded(selected.id);
    }, [selected]);

    function openArea(area) {
        navigate("/observationarea/" + area.id)
    }

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

    function renderProcessingIcon(processingEnabled) {
        if (processingEnabled) {
            return (
                <Box sx={{display: "flex", alignItems: "center", background: "rgba(215, 93, 42, 0.7)"}}>
                    <QueryStats sx={{color: "white", margin: "0.5rem", scale: "90%", opacity: "90%"}} fontSize="large" />
                    <Typography variant="h6" component="div" sx={{color: "white"}}>
                        {t("button.tracking")}
                    </Typography>
                </Box>
            );
        }
    }


    function renderImage(area) {
        const imageUrl = area.image !== null ? imageFileUrlForId(area.image.id) : null;
        return (
            <CardMedia
                component="img"
                height="300"
                src={imageUrl}
                sx={{filter: area.processingEnabled ? 'none' : 'grayscale(100%)'}}
            />);

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
                                            ? 'rgba(215, 93, 42, 0.18)'
                                            : 'rgba(0, 0, 0, 0.06)',
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
                                    {area.image !== null ?
                                        <Box
                                            role="button"
                                            tabIndex={0}
                                            onClick={() => openArea(area)}
                                            sx={{cursor: "pointer", display: "grid", "& > *": {gridArea: "1 / 1"}}}
                                        >
                                            <Box sx={{display: "grid", "& > *": {gridArea: "1 / 1"}}}>
                                                {renderImage(area)}
                                                <Box sx={{display: "flex", flexDirection: "column", justifyContent: "space-between", height: "100%"}}>
                                                    {renderProcessingIcon(area.processingEnabled)}
                                                    <Box sx={{display: "flex", justifyContent: "flex-end", width: "100%", px: 1, backgroundColor: "rgba(255, 255, 255, 0.45)", backdropFilter: "blur(2px)"}}>
                                                        {area.links && Array.isArray(area.links) && area.links.length > 0 ? (
                                                            area.links.map((link) => (
                                                                <Button key={link.id} size="small" onClick={(event) => {
                                                                    event.stopPropagation();
                                                                    window.open(link.url, '_blank');
                                                                }}>
                                                                    {link.name}
                                                                </Button>
                                                            ))
                                                        ) : (<></>)}
                                                    </Box>
                                                </Box>
                                            </Box>
                                        </Box> :
                                        <CardContent sx={{height: 300}}>
                                            <Typography textAlign={"center"}>{t("observationAreaCard.noImage")}</Typography>
                                        </CardContent>
                                    }
                                </AccordionDetails>

                            </Accordion>);
                    }
                })
            }
        </Box>
    )
}
