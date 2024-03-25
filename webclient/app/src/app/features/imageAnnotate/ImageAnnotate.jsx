import React from "react";
import ReactImageAnnotate from "@starwit/react-image-annotate";
import {useEffect, useMemo, useState} from "react";
import {useParams} from "react-router-dom";
import {useTranslation} from "react-i18next";
import ClassificationRest from "../../services/ClassificationRest";
import {Box, Container, FormControl, Stack, Typography} from "@mui/material";
import ImageRest from "../../services/ImageRest";
import ObservationAreaRest from "../../services/ObservationAreaRest";
import {setIn} from 'seamless-immutable';
import {useSnackbar} from 'notistack';
import {classificationSelectTools} from "../../AppConfig";
import ObservationAreaSelect from "../observationArea/ObservationAreaSelect";
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
    const observationAreaRest = useMemo(() => new ObservationAreaRest(), [])
    const {observationAreaId} = useParams();

    useEffect(() => {
        reloadClassification();
        reloadObservationAreas();
    }, []);

    function reloadClassification() {
        classificationRest.findAll().then(response => {
            const translatedClassifications = response.data.map(classification => ({
                ...classification,
                name: t("classification.name." + classification.name) == "classification.name." + classification.name ?  classification.name : t("classification.name." + classification.name)
            }));
            setClassifications(translatedClassifications);
        });
    }

    function reloadObservationAreas() {
        if (observationAreaId !== "undefined") {
            observationAreaRest.findById(observationAreaId).then(response => {
                if (response.data == null) {
                    return;
                } else if (response.data?.id !== undefined) {
                    reloadImages(response.data?.id);
                }
            });
        }
    }

    function reloadImages(observationAreaId) {
        imageRest.findWithPolygons(observationAreaId).then(response => {
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
        if (!validateRegionNames(event.images[event.selectedImage].regions)) {
            handleMessage("error", t("error.image.notunique"))
            return;
        }
        
        imageRest.savePolygons(event.images).then(() => {
            handleMessage("success", t("response.save.success"));
            reloadObservationAreas();
        });
    }

    function validateRegionNames(regions) {
        const names = regions.map(r => r.name);
        return validateNonEmpty(names) && validateUnique(names);
    }

    function validateNonEmpty(entries) {
        return entries.every(n => n !== undefined && n !== "");
    }
    
    function validateUnique(entries) {
        const uniqueNames = new Set(entries);
        return uniqueNames.size === entries.length;
    }

    if (!classifications || !images) {
        return (
            <>
                <AppBar sx={{bgcolor: "white", color: "black", width: "100%", left: "0rem"}}>
                    <ObservationAreaSelect />
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
                <AppBar sx={{bgcolor: "white", color: "black", width: "100%", left: "0rem"}}>
                    <ObservationAreaSelect />
                </AppBar>
                <Box sx={{padding: "3rem", paddingTop: "5rem"}}>
                    <Typography variant="h5">{t("parkingConfig.image.empty")}</Typography>
                </Box>
            </>

        );
    }

    return (
        <>
            <AppBar color="transparent" sx={{boxShadow: "none", left: "0rem", right: "8rem", width: "80%"}}>
                <ObservationAreaSelect />
            </AppBar>
            <ReactImageAnnotate
                sx={{width: '99%'}}
                labelImages
                regionClsList={classifications.map(classification => classification.name)}
                regionColorList={classifications.map(classification => classification.color)}
                onExit={savePolygons}
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
