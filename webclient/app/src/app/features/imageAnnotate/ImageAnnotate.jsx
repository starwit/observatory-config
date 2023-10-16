import React from "react";
import ReactImageAnnotate from "@starwit/react-image-annotate";
import {useEffect, useMemo, useState} from "react";
import {useTranslation} from "react-i18next";
import ClassificationRest from "../../services/ClassificationRest";
import {Typography} from "@mui/material";
import ImageRest from "../../services/ImageRest";
import Snackbar from "@mui/material/Snackbar";
import MuiAlert from "@mui/material/Alert";

const Alert = React.forwardRef(function Alert(props, ref) {
    return <MuiAlert elevation={6} ref={ref} variant="filled" {...props} />;
});

function ImageAnnotate() {
    const {t} = useTranslation();

    const [classifications, setClassifications] = useState();
    const [images, setImages] = useState(null);
    const [open, setOpen] = useState(false);
    const [messageInfo, setMessageInfo] = React.useState(undefined);
    const [selectedImage, setSelectedImage] = useState(0);
    const classificationRest = useMemo(() => new ClassificationRest(), []);
    const imageRest = useMemo(() => new ImageRest(), []);
    const id = 1;// TODO: useParams();

    useEffect(() => {
        reloadClassification();
    }, []);

    useEffect(() => {
        reloadImages();
    }, [id]);

    function reloadClassification() {
        classificationRest.findAll().then(response => {
            setClassifications(response.data);
        });
    }

    function reloadImages() {
        imageRest.findWithPolygons(id).then(response => {
            if (response.data == null) {
                return;
            }
            setImages(response.data.map(image => parseImage(image)));
        });
    }

    function parseImage(image) {
        image.src = window.location.pathname + "api/imageFile/name/" + image.src;

        return image;
    }

    function wrapAround(newNumber) {
        return ((newNumber % images?.length) + images?.length) % images?.length;
    }

    function onNextImage() {
        setSelectedImage(wrapAround(selectedImage + 1));
    }

    function onPrevImage() {
        setSelectedImage(wrapAround(selectedImage - 1));
    }

    function handleMessage(severity, message) {
        setMessageInfo({severity: severity, message: message});
        setOpen(true);
    }

    function savePolygons(event) {
        imageRest.savePolygons(event).then(response => {
            reloadImages();
            if (response.status == 200) {
                handleMessage("success", "Saved successfuly");
            } else {
                handleMessage("error", "Failed to Save! Error " + response.status);
            }
        });
    }

    if (!classifications || !images) {
        return <Typography>{t("general.loading")}</Typography>;
    }

    if (images.length === 0) {
        return <Typography>{t("parkingConfig.image.empty")}</Typography>;
    }

    const handleClose = (event, reason) => {
        if (reason === "clickaway") {
            return;
        }
        setOpen(false);
    };

    return (
        <>
            <ReactImageAnnotate
                labelImages
                regionClsList={classifications.map(classification => classification.name)}
                onExit={event => {
                    console.log("save image");
                    console.log(event.images);
                    savePolygons(event.images);
                }}
                enabledTools={["select", "create-point", "create-polygon", "create-box", "create-line"]}
                images={images}
                hideHeaderText
                selectedImage={selectedImage}
                onNextImage={onNextImage}
                onPrevImage={onPrevImage}
                hideNext={images.length === 1}
                hidePrev={images.length === 1}
                hideClone
            />
            <Snackbar open={open} autoHideDuration={6000} onClose={handleClose}>
                <Alert onClose={handleClose} severity={messageInfo ? messageInfo.severity : undefined}
                    sx={{width: "100%"}}>
                    {messageInfo ? messageInfo.message : undefined}
                </Alert>
            </Snackbar>

        </>
    );
}
export default ImageAnnotate;
