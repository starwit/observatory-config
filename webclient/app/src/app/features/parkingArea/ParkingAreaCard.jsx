import React, {useState, useMemo, useEffect} from "react";
import {Card, CardActionArea, CardActions, CardContent, Divider, Grid, IconButton, Typography} from "@mui/material";
import {Delete, Edit, MoreHoriz} from "@mui/icons-material";
import PropTypes from "prop-types";
import {useTranslation} from "react-i18next";
import {useNavigate} from "react-router";
import ConfirmationDialog from "../../commons/dialog/ConfirmationDialog";
import ParkingAreaDialog from "./ParkingAreaDialog";
import ImageRest from "../../services/ImageRest";

function ParkingAreaCard(props) {
    const {parkingArea, onDeleteClick, onEditClick} = props;
    const navigate = useNavigate();
    const {t} = useTranslation();
    const [imageData, setImageData] = useState(null);
    const imageRest = useMemo(() => new ImageRest(), []);

    const [openDeleteDialog, setOpenDeleteDialog] = useState(false);

    const [openUpdateDialog, setOpenUpdateDialog] = useState(false);

    useEffect(() => loadImageData());



    function loadImageData() {
        let promise = imageRest.findWithPolygons(parkingArea.selectedProdConfigId).then(response => {
            if (response.data == null) {
                return;
            }
            setImageData('data:image/jpeg;base64,' + response.data[0].data);
        });
        return promise.PromiseResult;

    }

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
                <CardActionArea onClick={() => navigate("/parkingarea/" + parkingArea.id)}>
                    <CardContent>
                        <Typography variant="body2">
                        </Typography>
                        <img src={imageData} width={520} />

                    </CardContent>
                </CardActionArea>
            </Card>
            <ConfirmationDialog
                title={t("parkingArea.delete.title")}
                message={t("parkingArea.delete.message")}
                open={openDeleteDialog}
                onClose={() => setOpenDeleteDialog(false)}
                onSubmit={() => onDeleteClick(parkingArea.id)}
                confirmTitle={t("button.delete")}
            />
            <ParkingAreaDialog
                open={openUpdateDialog}
                onClose={() => setOpenUpdateDialog(false)}
                isCreate={false}
                selected={parkingArea}
                update={() => onEditClick()}
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
