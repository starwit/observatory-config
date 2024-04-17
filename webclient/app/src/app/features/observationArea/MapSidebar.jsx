import React from 'react'
import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
import {ContentCopy, Delete, Edit} from "@mui/icons-material";
import {CardMedia, CardActionArea, CardContent, Typography, Grid, IconButton, Box, Button, Accordion, AccordionActions, AccordionSummary, AccordionDetails} from "@mui/material";
import {imageFileUrlForId} from "../../services/ImageRest";
import {useNavigate} from "react-router";


export default function MapSidebar(props) {
    const {selected, observationAreas, editArea, copyArea, deleteArea} = props;
    const navigate = useNavigate();
    const [expanded, setExpanded] = React.useState(null);

    function openArea(area) {
        navigate("/observationarea/" + area.id)
    }

    const handleChange = (panel) => (event, newExpanded) => {
        setExpanded(newExpanded ? panel : false);
    };

    function isNearby(area1,area2) {

        if (area1.centerlongitude<area2.centerlongitude+0.0005 && area1.centerlongitude>area2.centerlongitude-0.0005) {
            if (area1.centerlatitude<area2.centerlatitude+0.0005 && area1.centerlatitude>area2.centerlatitude-0.0005) {
                return true;
            }
        }
        return false;
    }

    return (
        <Box position={"absolute"} height={"100%"} sx={{zIndex: 3}}>

            <Box sx={{bgcolor: '#FFFFFF', width: 350, marginLeft: 5, marginTop: 20, boxShadow: 2, borderRadius: "20px", padding: 2}}>
                {
                    observationAreas?.map(area => {
                        if (isNearby(area, selected)) {
                            const imageUrl = area.image !== null ? imageFileUrlForId(area.image.id) : null;
                            return (
                                <Accordion
                                    disableGutters
                                    sx={{boxShadow: 0}}
                                    onChange={handleChange(area.name)}
                                    expanded={expanded === area.name}
                                >
                                    <AccordionSummary
                                        expandIcon={<ExpandMoreIcon />}
                                        justifyContent="space-between"
                                    >
                                        <Typography sx={{margin: "auto", width: '124px', flexShrink: 0}}>
                                            {area.name}
                                        </Typography>

                                        <Grid item xs={5} align-items="center">
                                            <IconButton onClick={() => copyArea(area)}>
                                                <ContentCopy fontSize={"small"} />
                                            </IconButton>
                                            <IconButton onClick={() => editArea(area)}>
                                                <Edit fontSize={"small"} />
                                            </IconButton>
                                            <IconButton onClick={() => deleteArea(area)}>
                                                <Delete fontSize={"small"} />
                                            </IconButton>
                                        </Grid>
                                    </AccordionSummary>
                                    <AccordionDetails sx={{padding: 0}}>
                                        <CardActionArea onClick={() => openArea(area)}>
                                            {area.image !== null ?
                                                <CardMedia
                                                    component="img"
                                                    height="150"
                                                    src={imageUrl}
                                                    sx={{borderRadius: "10px"}}
                                                /> :
                                                <CardContent sx={{height: 150}}>
                                                    <Typography textAlign={"center"}>{t("observationAreaCard.noImage")}</Typography>
                                                </CardContent>
                                            }
                                        </CardActionArea>
                                    </AccordionDetails>
                                </Accordion>);
                        }
                        return (<></>);
                    })
                }
            </Box>
        </Box>
    )
}
