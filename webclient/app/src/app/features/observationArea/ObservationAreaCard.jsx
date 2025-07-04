import {ContentCopy, Delete, Edit} from "@mui/icons-material";
import {Card, CardActionArea, CardContent, CardMedia, Divider, Box, IconButton, ImageListItemBar, Tooltip, Typography} from "@mui/material";
import { Grid } from '@mui/material';
import PropTypes from "prop-types";
import React from "react";
import {useTranslation} from "react-i18next";
import {useNavigate} from "react-router";
import {imageFileUrlForId} from "../../services/ImageRest";
import QueryStats from "@mui/icons-material/QueryStats";

function ObservationAreaCard(props) {
    const {observationArea, onCopyClick, onDeleteClick, onEditClick} = props;

    const navigate = useNavigate();
    const {t} = useTranslation();

    const imageUrl = observationArea.image !== null ? imageFileUrlForId(observationArea.image.id) : null;

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

    function openArea() {
        navigate("/observationarea/" + observationArea.id);
    }

    return (
        <>
            <Card elevation={5}>
                <CardContent>
                    <Grid container spacing={0}>
                        <Grid size={7}>
                            <Typography gutterBottom variant="h5" component="div" onClick={openArea} sx={{cursor: "pointer"}}>
                                {observationArea.name}
                            </Typography>
                        </Grid>
                        <Grid size={5} align="right">
                            <Tooltip title={t("button.copy")}>
                                <IconButton onClick={onCopyClick}>
                                    <ContentCopy fontSize={"small"} />
                                </IconButton>
                            </Tooltip>
                            <Tooltip title={t("button.update")}>
                                <IconButton onClick={onEditClick}>
                                    <Edit fontSize={"small"} />
                                </IconButton>
                            </Tooltip>
                            <Tooltip title={t("button.delete")}>
                                <IconButton onClick={onDeleteClick}>
                                    <Delete fontSize={"small"} />
                                </IconButton>
                            </Tooltip>
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
                            {renderProcessingIcon(observationArea.processingEnabled)}
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
    onCopyClick: PropTypes.func.isRequired
};

export default ObservationAreaCard;
