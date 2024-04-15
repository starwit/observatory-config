import { ContentCopy, Delete, Edit } from "@mui/icons-material";
import { Card, CardActionArea, CardContent, CardMedia, Divider, CardHeader, Grid, IconButton, Typography, ImageListItemBar } from "@mui/material";
import PropTypes from "prop-types";
import React from "react";
import { useTranslation } from "react-i18next";
import { useNavigate } from "react-router";
import { imageFileUrlForId } from "../../services/ImageRest";
import StopCircleIcon from '@mui/icons-material/StopCircle';
import Box from '@mui/material/Box';

function ObservationAreaCard(props) {
    const {observationArea, onCopyClick, onDeleteClick, onEditClick} = props;

    const navigate = useNavigate();
    const {t} = useTranslation();
    
    const imageUrl = observationArea.image !== null ? imageFileUrlForId(observationArea.image.id) : null;



     function renderProcessingIcon(processingEnabled) {
         if (processingEnabled) {
             return (
                <CardMedia>
                    <Box display='flex' alignItems="center">
             <StopCircleIcon sx={{ color: 'white', margin: '0.5rem'}} fontSize="large"></StopCircleIcon>  
             <Typography variant="h6" component="div" color="white">
                    {t("button.tracking")}
                    </Typography>
                    </Box>
             </CardMedia>
             )
         }}

    function openArea() {
        navigate("/observationarea/" + observationArea.id)
    }

    return (
        <>
            <Card elevation={5}>
                <CardContent>
                    <Grid container spacing={0}>
                        <Grid item xs={7}>
                            <Typography gutterBottom variant="h5" component="div" onClick={openArea} sx={{cursor: "pointer"}}>
                                {observationArea.name}
                            </Typography>
                        </Grid>
                        <Grid item xs={5} align="right">
                            <IconButton onClick={onCopyClick}>
                                <ContentCopy fontSize={"small"} />
                            </IconButton>
                            <IconButton onClick={onEditClick}>
                                <Edit fontSize={"small"} />
                            </IconButton>
                            <IconButton onClick={onDeleteClick}>
                                <Delete fontSize={"small"} />
                            </IconButton>
                        </Grid>
                    </Grid>
                </CardContent>
                <Divider />
                <CardActionArea onClick={openArea}>
                    {observationArea.image !== null ?
                        <>
                        <CardMedia
                            component="img"
                            height="300"
                            src={imageUrl}>
                        </CardMedia>
                        <ImageListItemBar
                            sx={{
                            background:
                                'linear-gradient(to bottom, rgba(255,0,0,1) 0%, ' +
                                'rgba(255,0,0,1) 0%, rgba(255,0,0,1) 0%)',
                            }}
                            actionPosition="left"
                                      position="top"
                                      
                                      actionIcon={
                                        renderProcessingIcon(observationArea.processingEnabled)
                                      }
                        ></ImageListItemBar>
                        </> :
                        <CardContent sx={{height: 300}}>
                            <Typography textAlign={"center"}>{t("observationAreaCard.noImage")}</Typography>
                        </CardContent>
                    }
                </CardActionArea>
            </Card>
        </>
    );
}

ObservationAreaCard.propTypes = {
    observationArea: PropTypes.shape({
        id: PropTypes.number.isRequired,
        name: PropTypes.string.isRequired
    }),
    onEditClick: PropTypes.func.isRequired,
    onDeleteClick: PropTypes.func.isRequired,
    onCopyClick: PropTypes.func.isRequired,
};

export default ObservationAreaCard;
