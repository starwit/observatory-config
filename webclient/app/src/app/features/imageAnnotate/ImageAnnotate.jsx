import React from "react";
import ReactImageAnnotate from "@starwit/react-image-annotate";
import {useEffect, useMemo, useState} from "react";
import {useParams} from "react-router-dom";
import {useTranslation} from "react-i18next";
import ClassificationRest from "../../services/ClassificationRest";
import {Box, Container, FormControl, Stack, Typography} from "@mui/material";
import ImageRest from "../../services/ImageRest";
import ParkingAreaRest from "../../services/ParkingAreaRest";
import {setIn} from 'seamless-immutable';
import {useSnackbar} from 'notistack';
import {classificationSelectTools} from "../../AppConfig";
import ParkingAreaSelect from "../parkingArea/ParkingAreaSelect";
import {AppBar} from "../../assets/styles/HeaderStyles";

const userReducer = (state, action) => {
    if ("SELECT_CLASSIFICATION" == action.type) {
        const select = classificationSelectTools.find((c) => c.classification == action.cls);
        if (select !== undefined) {

            return setIn(state, ["selectedTool"], select.selectTool);
        }
    }
    return state;
};


function ImageAnnotate() {
    const {t} = useTranslation();

    const [classifications, setClassifications] = useState();
    const [images, setImages] = useState(null);
    const {enqueueSnackbar} = useSnackbar();
    const [selectedImage, setSelectedImage] = useState(0);
    const classificationRest = useMemo(() => new ClassificationRest(), []);
    const imageRest = useMemo(() => new ImageRest(), []);
    const parkingAreaRest = useMemo(() => new ParkingAreaRest(), [])
    const {parkingAreaId} = useParams();

    useEffect(() => {
        reloadClassification();
    }, []);

    useEffect(() => {
        reloadParkingAreas();
    }, [parkingAreaId]);

    function reloadClassification() {
        classificationRest.findAll().then(response => {
            setClassifications(response.data);
        });
    }

    function reloadParkingAreas() {
        if (parkingAreaId !== "undefined") {
            parkingAreaRest.findById(parkingAreaId).then(response => {
                if (response.data == null) {
                    return;
                } else if (response.data?.selectedProdConfig !== undefined) {
                    reloadImages(response.data?.selectedProdConfig.id);
                }
            });
        }
    }

    function reloadImages(prodConfigId) {
        imageRest.findWithPolygons(prodConfigId).then(response => {
            if (response.data == null) {
                return;
            }
            setImages(response.data.map(image => parseImage(image)));
        });
    }

    function parseImage(image) {
        image.src = window.location.pathname + "api/imageFile/id/" + image.id;
        image.name = "";
        return image;
    }

    function wrapAround(newNumber) {
        return ((newNumber % images?.length) + images?.length) % images?.length;
    }

    function onNextImage() {
        setSelectedImage(wrapAround(selectedImage));
    }

    function onPrevImage() {
        setSelectedImage(wrapAround(selectedImage - 1));
    }

    function handleMessage(severity, message) {
        enqueueSnackbar(message, {variant: severity});
    }

    function savePolygons(event) {
        imageRest.savePolygons(event).then(() => {
            handleMessage("success", t("response.save.success"));
            reloadParkingAreas();
        })
    }

    if (!classifications || !images) {
        return (
            <>
                <AppBar sx={{bgcolor: "white", color: "black", width: "100%", left: "0rem", paddingLeft: "3rem"}}>
                    <ParkingAreaSelect />
                </AppBar>
                <Box sx={{padding: "3rem", paddingTop: "5rem"}}>
                    <Typography variant="h5">{t("general.loading")}</Typography>
                </Box>
            </>
        );
    }

    if (images.length === 0) {
        return (
            <>
                <AppBar sx={{bgcolor: "white", color: "black", width: "100%", left: "0rem", paddingLeft: "3rem"}}>
                    <ParkingAreaSelect sx={{}} />
                </AppBar>
                <Box sx={{padding: "3rem", paddingTop: "5rem"}}>
                    <Typography variant="h5">{t("parkingConfig.image.empty")}</Typography>
                </Box>
            </>

        );
    }

    return (
        <>
            <AppBar color="transparent" sx={{boxShadow: "none", left: "3rem", right: "8rem", width: "80%"}}>
                <ParkingAreaSelect />
            </AppBar>
            <ReactImageAnnotate
                sx={{width: '99%'}}
                labelImages
                regionClsList={classifications.map(classification => classification.name)}
                regionColorList={classifications.map(classification => classification.color)}
                onExit={event => {
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
        </>
    );
}
export default ImageAnnotate;
