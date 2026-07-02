import ReactImageAnnotate from "@starwit/react-image-annotate";
import React, {useEffect, useMemo, useState, useRef, useImperativeHandle} from "react";
import {useTranslation} from "react-i18next";
import {produce} from 'immer';
import ClassificationRest from "../../services/ClassificationRest";
import ImageRest, {imageFileUrlForId} from "../../services/ImageRest";
import ObservationAreaRest from "../../services/ObservationAreaRest";
import {toast} from "react-toastify";

function ImageAnnotate(props) {
    const {observationAreaId, onImageSizeChange, lockCanvas, ref} = props;

    const {t} = useTranslation();

    const classificationRest = useMemo(() => new ClassificationRest(), []);
    const observationAreaRest = useMemo(() => new ObservationAreaRest(), []);
    const imageRest = useMemo(() => new ImageRest(), []);

    const [classifications, setClassifications] = useState();
    const [image, setImage] = useState();

    const annotatorRef = useRef(null);

    useImperativeHandle(
        ref,
        () => ({
            saveRegions,
        })
    );

    useEffect(() => {
        reloadClassifications();
    }, []);

    function reloadClassifications() {
        classificationRest.findAll().then(response => {
            setClassifications(response.data);
        });
    }

    useEffect(() => {
        reloadImage();
    }, [observationAreaId]);

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

            const imageWidth = image.width;
            const imageHeight = image.height;
            if (onImageSizeChange && imageWidth && imageHeight) {
                onImageSizeChange({width: imageWidth, height: imageHeight});
            } else if (onImageSizeChange && image.src) {
                const loadedImage = new window.Image();
                loadedImage.onload = () => {
                    onImageSizeChange({width: loadedImage.naturalWidth, height: loadedImage.naturalHeight});
                };
                loadedImage.src = image.src;
            }
        }
        return image;
    }

    function saveRegions() {
        let regions = annotatorRef.current?.getRegions();

        if (!validateRegionNames(regions)) {
            toast.error(t("error.image.notunique"))
            return;
        }

        observationAreaRest.savePolygons(observationAreaId, regions).then(() => {
            toast.success(t("response.save.success"));
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
        <ReactImageAnnotate
            classifications={classifications.map(c => ({
                cls: c.name, 
                displayName: t("classification.name." + c.name), 
                color: c.color,
                tool: c.toolType
            }))}
            image={image}
            enabledRegionProps={["name", "line-direction"]}
            movementLocked={lockCanvas}
            ref={annotatorRef}>
        </ReactImageAnnotate>
    );
}
export default ImageAnnotate;
