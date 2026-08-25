import {ContentCopy, Delete, Edit} from "@mui/icons-material";
import {Card, CardContent, Divider, Grid, IconButton, Tooltip, Typography} from "@mui/material";
import PropTypes from "prop-types";
import {useTranslation} from "react-i18next";
import ObservationAreaPreview from "./ObservationAreaPreview";

function ObservationAreaCard(props) {
    const {observationArea, onCopyClick, onDeleteClick, onEditClick} = props;

    const {t} = useTranslation();

    return (
        <>
            <Card elevation={5}>
                <CardContent>
                    <Grid container spacing={0}>
                        <Grid size={7}>
                            <Typography gutterBottom variant="h5" component="div">
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
                <ObservationAreaPreview observationArea={observationArea} />
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
