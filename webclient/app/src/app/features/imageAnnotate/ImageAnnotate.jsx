import React from "react";
import ReactImageAnnotate from "@starwit/react-image-annotate";
import {useEffect, useMemo, useState} from "react";
import {useParams} from "react-router-dom";
import {useTranslation} from "react-i18next";
import ClassificationRest from "../../services/ClassificationRest";
import {Typography} from "@mui/material";
import ImageRest from "../../services/ImageRest";
import Snackbar from "@mui/material/Snackbar";
import MuiAlert from "@mui/material/Alert";
import {setIn} from 'seamless-immutable';

const Alert = React.forwardRef(function Alert(props, ref) {
    return <MuiAlert elevation={6} ref={ref} variant="filled" {...props} />;
});

const userReducer = (state, action) => {
    switch (action.type) {
        case "SELECT_CLASSIFICATION": {
            switch (action.cls) {
                case "Lichtschranke": {
                    return setIn(state, ["selectedTool"], "create-line");
                }
            }
        }   
    }
    
    return state;
};

function ImageAnnotate() {
    const {t} = useTranslation();

    const [classifications, setClassifications] = useState();
    const [images, setImages] = useState(null);
    const [open, setOpen] = useState(false);
    const [messageInfo, setMessageInfo] = React.useState(undefined);
    const [selectedImage, setSelectedImage] = useState(0);
    const classificationRest = useMemo(() => new ClassificationRest(), []);
    const imageRest = useMemo(() => new ImageRest(), []);
    const {imageId} = useParams();

    useEffect(() => {
        reloadClassification();
    }, []);

    useEffect(() => {
        reloadImages();
    }, [imageId]);

    function reloadClassification() {
        classificationRest.findAll().then(response => {
            setClassifications(response.data);
        });
    }

    function reloadImages() {
        imageRest.findWithPolygons(imageId).then(response => {
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
                handleMessage("success", t("response.save.success"));
            } else {
                handleMessage("error", t("response.save.failed"));
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
                regionColorList={classifications.map(classification => classification.color)}
                onExit={event => {
                    console.log("save image");
                    console.log(event.images);
                    savePolygons(event.images);
                }}
                images={images}
                hideHeaderText
                selectedImage={selectedImage}
                onNextImage={onNextImage}
                onPrevImage={onPrevImage}
                hideNext={images.length === 1}
                hidePrev={images.length === 1}
                hideSettings={true}
                hideClone
                enabledRegionProps={["name"]}
                userReducer={userReducer}
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
