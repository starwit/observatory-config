import {QueryStats} from "@mui/icons-material";
import {Box, Button, CardContent, CardMedia, Typography} from "@mui/material";
import {imageFileUrlForId} from "../../services/ImageRest";
import PropTypes from "prop-types";
import {useTranslation} from "react-i18next";
import {useNavigate} from "react-router-dom";

function ObservationAreaPreview(props) {
    const {observationArea} = props;
    const {t} = useTranslation();
    const navigate = useNavigate();

    function renderImage(area) {
        const imageUrl = area.image !== null ? imageFileUrlForId(area.image.id) : null;
        return (
            <CardMedia
                component="img"
                height="300"
                src={imageUrl}
                sx={{filter: area.processingEnabled ? 'none' : 'grayscale(100%)'}}
            />);

    }

    function renderProcessingIcon(processingEnabled) {
        if (processingEnabled) {
            return (
                <Box sx={{display: "flex", alignItems: "center", background: "rgba(215, 93, 42, 0.7)"}}>
                    <QueryStats sx={{color: "white", margin: "0.5rem", scale: "90%", opacity: "90%"}} fontSize="large" />
                    <Typography variant="h6" component="div" sx={{color: "white"}}>
                        {t("button.tracking")}
                    </Typography>
                </Box>
            );
        }
    }
    function openArea(area) {
        navigate("/observationarea/" + area.id)
    }

    return (
        observationArea.image !== null ?
            <Box
                role="button"
                tabIndex={0}
                onClick={() => openArea(observationArea)}
                sx={{cursor: "pointer", display: "grid", "& > *": {gridArea: "1 / 1"}}}
            >
                <Box sx={{display: "grid", "& > *": {gridArea: "1 / 1"}}}>
                    {renderImage(observationArea)}
                    <Box sx={{display: "flex", flexDirection: "column", justifyContent: "space-between", height: "100%"}}>
                        {renderProcessingIcon(observationArea.processingEnabled)}
                        <Box sx={{display: "flex", justifyContent: "flex-end", width: "100%", px: 1, backgroundColor: "rgba(255, 255, 255, 0.45)", backdropFilter: "blur(2px)"}}>
                            {observationArea.links && Array.isArray(observationArea.links) && observationArea.links.length > 0 ? (
                                observationArea.links.map((link) => (
                                    <Button key={link.id} size="small" onClick={(event) => {
                                        event.stopPropagation();
                                        window.open(link.url, '_blank');
                                    }}>
                                        {link.name}
                                    </Button>
                                ))
                            ) : (<></>)}
                        </Box>
                    </Box>
                </Box>
            </Box> :
            <CardContent sx={{height: 300}}>
                <Typography textAlign={"center"}>{t("observationAreaCard.noImage")}</Typography>
            </CardContent>
    );
}

ObservationAreaPreview.propTypes = {
    observationArea: PropTypes.object
};

export default ObservationAreaPreview;