import React, {useEffect} from 'react'
import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
import {ContentCopy, Delete, Edit, QueryStats} from "@mui/icons-material";
import {CardMedia, CardActionArea, CardContent, Typography, IconButton, Box, Accordion, AccordionSummary, AccordionDetails, Tooltip, ImageListItemBar} from "@mui/material";
import Grid from '@mui/material/Grid2';
import {imageFileUrlForId} from "../../services/ImageRest";
import {useNavigate} from "react-router";
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
                <ImageListItemBar
                    sx={{background: "rgba(215, 93, 42, 0.7)"}}
                    actionPosition="left"
                    position="top"
                    actionIcon={
                        <Box display='flex' alignItems="center">
                            <QueryStats sx={{color: "white", margin: "0.5rem", scale: "90%", opacity: "90%"}} fontSize="large"></QueryStats>
                            <Typography variant="h6" component="div" color="white">
                                {t("button.tracking")}
                            </Typography>
                        </Box>
                    }
                ></ImageListItemBar>
            );
        }
    }

    function renderImage(area) {
        const imageUrl = area.image !== null ? imageFileUrlForId(area.image.id) : null;
        if (area.image !== null) {
            return (
                <CardMedia
                    component="img"
                    height="200"
                    src={imageUrl}
                />);
        }
        return (
            <CardContent sx={{height: 150}}>
                <Typography textAlign={"center"}>{t("observationAreaCard.noImage")}</Typography>
            </CardContent>
        );

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
                                sx={{boxShadow: 0}}
                                onChange={handleChange(area.id)}
                                expanded={expanded === area.id}
                            >
                                <AccordionSummary
                                    expandIcon={<ExpandMoreIcon />}
                                >
                                    <Typography sx={MapStyles.title}>
                                        {area.name}
                                    </Typography>

                                    <Grid size={{xs: 5}} align-items="center">
                                        <Tooltip title={t("button.copy")}>
                                            <IconButton onClick={() => copyArea(area)}>
                                                <ContentCopy fontSize={"small"} />
                                            </IconButton>
                                        </Tooltip>
                                        <Tooltip title={t("button.update")}>
                                            <IconButton onClick={() => editArea(area)}>
                                                <Edit fontSize={"small"} />
                                            </IconButton>
                                        </Tooltip>
                                        <Tooltip title={t("button.delete")}>
                                            <IconButton onClick={() => deleteArea(area)}>
                                                <Delete fontSize={"small"} />
                                            </IconButton>
                                        </Tooltip>
                                    </Grid>
                                </AccordionSummary>
                                <AccordionDetails sx={{padding: 0}}>
                                    <CardActionArea onClick={() => openArea(area)}>
                                        {renderImage(area)}
                                        {renderProcessingIcon(area.processingEnabled)}
                                    </CardActionArea>
                                </AccordionDetails>
                            </Accordion>);
                    }
                })
            }
        </Box>
    )
}
