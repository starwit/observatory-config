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
    const [imageSrc, setImageSrc] = useState('');
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
            setImageSrc('data:'+response.data[0].type+';base64,'+response.data[0].data);
            setImages(response.data.map(image => 'data:'+image.type+';base64,'+image.data));
        });
    }

    // function arrayBufferToBase64(buffer) {
    //     var binary = '';
    //     var bytes = [].slice.call(new Uint8Array(buffer));
    //     bytes.forEach((b) => binary += String.fromCharCode(b));
    //     return window.btoa(binary);
    // }

    function convertToImage(image) {
        console.log(image);
        var base64Flag = 'data:'+image.type+';base64,';

        // var imageStr =
        //     this.arrayBufferToBase64(image.data);
        return base64Flag + image.data;
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
            <img src={imageSrc} />
            <ReactImageAnnotate
                labelImages
                regionClsList={classifications.map(classification => classification.name)}
                onExit={event => {
                    console.log("save image");
                    console.log(event.images);
                    savePolygons(event.images);
                }}
                //enabledTools={["select", "create-polygon", "create-box", "create-line"]}
                enabledTools={["select", "create-polygon", "create-line"]}
                images={images}
                hideHeaderText
                selectedImage={selectedImage}
                onNextImage={onNextImage}
                onPrevImage={onPrevImage}
                hideNext={images.length === 1}
                hidePrev={images.length === 1}
                hideSettings={true}
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
