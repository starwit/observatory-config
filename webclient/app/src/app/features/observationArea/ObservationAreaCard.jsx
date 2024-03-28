import { ContentCopy, Delete, Edit } from "@mui/icons-material";
import { Card, CardActionArea, CardContent, CardMedia, Divider, Grid, IconButton, Typography } from "@mui/material";
import PropTypes from "prop-types";
import React from "react";
import { useTranslation } from "react-i18next";
import { useNavigate } from "react-router";

function ObservationAreaCard(props) {
    const {observationArea, onCopyClick, onDeleteClick, onEditClick} = props;

    const navigate = useNavigate();
    const {t} = useTranslation();
    
    const imageUrl = `${window.location.pathname}api/imageFile/id/${observationArea.image.id}`;

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
                    {observationArea.image.data !== null ?
                        <CardMedia
                            component="img"
                            height="300"
                            src={imageUrl}
                        /> :
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
