import React, {useState} from "react";
import {Card, CardActionArea, CardActions, CardContent, Divider, Grid, IconButton, Typography} from "@mui/material";
import {Delete, Edit, MoreHoriz} from "@mui/icons-material";
import PropTypes from "prop-types";
import {useTranslation} from "react-i18next";
import {ConfirmationDialog} from "@starwit/react-starwit";
import {useNavigate} from "react-router";

function ParkingAreaCard(props) {
    const {parkingArea, onDeleteClick, onEditClick} = props;
    const navigate = useNavigate();
    const {t} = useTranslation();

    const [openDeleteDialog, setOpenDeleteDialog] = useState(false);

    return (
        <>
            <Card elevation={5}>
                <CardContent>
                    <Grid container spacing={0}>
                        <Grid item xs={7}>
                            <Typography gutterBottom variant="h5" component="div">
                                {parkingArea.name}
                            </Typography>
                        </Grid>
                        <Grid item xs={5} align="right">
                            <IconButton onClick={onEditClick}>
                                <Edit fontSize={"small"} />
                            </IconButton>
                            <IconButton onClick={() => setOpenDeleteDialog(true)}>
                                <Delete fontSize={"small"} />
                            </IconButton>
                        </Grid>
                    </Grid>
                </CardContent>
                <Divider />
                <CardActionArea onClick={() => navigate("/parkingarea/" + parkingArea.id)}>
                    <CardContent>
                        <Typography variant="body2">
                            das ist ein Test
                        </Typography>
                        <CardActions>
                            <MoreHoriz color="primary" />
                        </CardActions>
                    </CardContent>
                </CardActionArea>
            </Card>
            <ConfirmationDialog
                title={t("app.delete.title")}
                message={t("app.delete.message")}
                open={openDeleteDialog}
                onClose={() => setOpenDeleteDialog(false)}
                onSubmit={() => onDeleteClick(parkingArea.id)}
                confirmTitle={t("button.delete")}
            />
        </>
    );
}

ParkingAreaCard.propTypes = {
    parkingArea: PropTypes.shape({
        id: PropTypes.number.isRequired,
        name: PropTypes.string.isRequired
    }),
    onEditClick: PropTypes.func.isRequired,
    onDeleteClick: PropTypes.func.isRequired
};

export default ParkingAreaCard;
