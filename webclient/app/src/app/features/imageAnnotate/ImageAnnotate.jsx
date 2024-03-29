import ReactImageAnnotate from "@starwit/react-image-annotate";
import { useSnackbar } from 'notistack';
import React, { useEffect, useMemo, useState } from "react";
import { useTranslation } from "react-i18next";
import { setIn } from 'seamless-immutable';
import ClassificationRest from "../../services/ClassificationRest";
import ImageRest, { imageFileUrlForId } from "../../services/ImageRest";
import ObservationAreaRest from "../../services/ObservationAreaRest";

function ImageAnnotate(props) {
    const {observationAreaId} = props;

    const {t} = useTranslation();
    const {enqueueSnackbar} = useSnackbar();

    const classificationRest = useMemo(() => new ClassificationRest(), []);
    const observationAreaRest = useMemo(() => new ObservationAreaRest(), []);
    const imageRest = useMemo(() => new ImageRest(), []);

    const [classifications, setClassifications] = useState();
    const [image, setImage] = useState();

    useEffect(() => {
        reloadClassifications();
    }, []);

    function reloadClassifications() {
        classificationRest.findAll().then(response => {
            const translatedClassifications = response.data.map(classification => ({
                ...classification,
                name: t("classification.name." + classification.name)
            }));
            setClassifications(translatedClassifications);
        });
    }

    useEffect(() => {
        reloadImage();
    }, [observationAreaId]);

    function userReducer(state, action) {
        if ("SELECT_CLASSIFICATION" == action.type) {
            const select = classifications.find((c) => c.name == action.cls);
            if (select !== undefined) {
    
                return setIn(state, ["selectedTool"], select.toolType);
            }
        }
        return state;
    };

    function reloadImage() {
        imageRest.findWithPolygons(observationAreaId).then(response => {
            if (response.data == null) {
                return;
            }
            setImage(parseImage(response.data[0]));
        });
    }

    function parseImage(image) {
        if (image !== undefined) {
            image.src = image !== undefined ? imageFileUrlForId(image.id) : "";
            image.name = "";
        }
        return image;
    }

    function handleMessage(severity, message) {
        enqueueSnackbar(message, {variant: severity});
    }
    
    function savePolygons(event) {
        if (!validateRegionNames(event.images[event.selectedImage].regions)) {
            handleMessage("error", t("error.image.notunique"))
            return;
        }
        
        observationAreaRest.savePolygons(observationAreaId, event.images[0].regions).then(() => {
            handleMessage("success", t("response.save.success"));
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

    if (image === undefined || classifications === undefined) {
        return;
    }

    return (
        <>
            <ReactImageAnnotate
                labelImages
                regionClsList={classifications.map(classification => classification.name)}
                regionColorList={classifications.map(classification => classification.color)}
                onExit={savePolygons}
                images={[image]}
                hideHeaderText
                selectedImage={0}
                hideNext={true}
                hidePrev={true}
                hideSettings={true}
                hideClone
                enabledRegionProps={["name"]}
                userReducer={userReducer}
            />
        </>
    );
}
export default ImageAnnotate;
