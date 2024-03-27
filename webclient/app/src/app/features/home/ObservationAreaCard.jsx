import { ContentCopy, Delete, Edit, MoreHoriz } from "@mui/icons-material";
import { Card, CardActionArea, CardActions, CardContent, Divider, Grid, IconButton, Typography } from "@mui/material";
import PropTypes from "prop-types";
import React, { useMemo } from "react";
import { useNavigate } from "react-router";

function ObservationAreaCard(props) {
    const {observationArea, onCopyClick, onDeleteClick, onEditClick} = props;
    const navigate = useNavigate();
    const imageData = 'data:' + observationArea.image.type + ';base64,' + observationArea.image.data;

    return (
        <>
            <Card elevation={5}>
                <CardContent>
                    <Grid container spacing={0}>
                        <Grid item xs={7}>
                            <Typography gutterBottom variant="h5" component="div">
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
                <CardActionArea onClick={() => navigate("/observationarea/" + observationArea.id)}>
                    <CardMedia
                        component="img"
                        height="300"
                        image={imageData}
                    />
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
