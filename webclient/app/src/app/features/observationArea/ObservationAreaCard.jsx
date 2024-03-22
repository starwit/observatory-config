import React, {useState} from "react";
import {Card, CardActionArea, CardActions, CardContent, Divider, Grid, IconButton, Typography} from "@mui/material";
import {Delete, Edit, MoreHoriz, ContentCopy} from "@mui/icons-material";
import PropTypes from "prop-types";
import {useTranslation} from "react-i18next";
import {useNavigate} from "react-router";
import ConfirmationDialog from "../../commons/dialog/ConfirmationDialog";
import ObservationAreaDialog from "./ObservationAreaDialog";

function ObservationAreaCard(props) {
    const {observationArea, onDeleteClick, onEditClick} = props;
    const navigate = useNavigate();
    const {t} = useTranslation();

    const [openDeleteDialog, setOpenDeleteDialog] = useState(false);

    const [openUpdateDialog, setOpenUpdateDialog] = useState(false);

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
                        <IconButton onClick={() => setOpenUpdateDialog(true)}>
                                <ContentCopy fontSize={"small"} />
                            </IconButton>
                            <IconButton onClick={() => setOpenUpdateDialog(true)}>
                                <Edit fontSize={"small"} />
                            </IconButton>
                            <IconButton onClick={() => setOpenDeleteDialog(true)}>
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
            <ConfirmationDialog
                title={t("observationArea.delete.title")}
                message={t("observationArea.delete.message")}
                open={openDeleteDialog}
                onClose={() => setOpenDeleteDialog(false)}
                onSubmit={() => onDeleteClick(observationArea.id)}
                confirmTitle={t("button.delete")}
            />
            <ObservationAreaDialog
                open={openUpdateDialog}
                onClose={() => setOpenUpdateDialog(false)}
                isCreate={false}
                selected={observationArea}
                update={() => onEditClick()}
            />
        </>
    );
}

ObservationAreaCard.propTypes = {
    observationArea: PropTypes.shape({
        id: PropTypes.number.isRequired,
        name: PropTypes.string.isRequired
    }),
    onEditClick: PropTypes.func.isRequired,
    onDeleteClick: PropTypes.func.isRequired
};

export default ObservationAreaCard;
