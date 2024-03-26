import { ContentCopy, Delete, Edit, MoreHoriz } from "@mui/icons-material";
import { Card, CardActionArea, CardActions, CardContent, Divider, Grid, IconButton, Typography } from "@mui/material";
import PropTypes from "prop-types";
import React from "react";
import { useNavigate } from "react-router";

function ObservationAreaCard(props) {
    const {observationArea, onCopyClick, onDeleteClick, onEditClick} = props;
    const navigate = useNavigate();

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
                    <CardContent>
                        <Typography variant="body2">
                        </Typography>
                        <CardActions>
                            <MoreHoriz color="primary" />
                        </CardActions>
                    </CardContent>
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
