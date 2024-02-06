import React from "react";
import ReactImageAnnotate from "@starwit/react-image-annotate";
import {useEffect, useMemo, useState} from "react";
import {useParams} from "react-router-dom";
import {useTranslation} from "react-i18next";
import ClassificationRest from "../../services/ClassificationRest";
import {Typography} from "@mui/material";
import ImageRest from "../../services/ImageRest";
import ParkingAreaRest from "../../services/ParkingAreaRest";
import {setIn} from 'seamless-immutable';
import { useSnackbar } from 'notistack';
import {classificationSelectTools} from "../../AppConfig";

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
    const { enqueueSnackbar } = useSnackbar();
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
        parkingAreaRest.findById(parkingAreaId).then(response => {
            if (response.data == null) {
                return;
            } else if (response.data?.selectedProdConfig !== undefined) {
                reloadImages(response.data?.selectedProdConfig.id);
            }
        });
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
        enqueueSnackbar(message, {variant: severity});
    }
    
    function savePolygons(event) {
        if (!validateRegionNames(event.images[event.selectedImage].regions)) {
            handleMessage("error", t("error.image.notunique"))
            return;
        }
        
        imageRest.savePolygons(event.images).then(() => {
            handleMessage("success", t("response.save.success"));
            reloadParkingAreas();
        })
        .catch(() => handleMessage("error", t("response.save.failed")));
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
        return <Typography>{t("general.loading")}</Typography>;
    }

    if (images.length === 0) {
        return <Typography>{t("parkingConfig.image.empty")}</Typography>;
    }

    return (
        <>
            <ReactImageAnnotate
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
