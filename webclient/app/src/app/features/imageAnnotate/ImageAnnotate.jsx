import ReactImageAnnotate from "@starwit/react-image-annotate";
import {useEffect, useMemo, useState} from "react";
import {useTranslation} from "react-i18next";
import ClassificationRest from "../../services/ClassificationRest";
import {Typography} from "@mui/material";
import ImageRest from "../../services/ImageRest";

function ImageAnnotate() {

    const {t} = useTranslation();

    const [classifications, setClassifications] = useState();
    const [images, setImages] = useState(null);
    const [selectedImage, setSelectedImage] = useState(0);
    const classificationRest = useMemo(() => new ClassificationRest(), []);
    const imageRest = useMemo(() => new ImageRest(), []);
    const id = 1;//TODO: useParams();

    useEffect(() => {
        reloadClassification();
    }, []);

    useEffect(() => {
        reloadImages();
    }, [id]);

    function reloadClassification() {
        classificationRest.findAll().then(response => {
            setClassifications(response.data)
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
        return ((newNumber % images?.length) + images?.length) % images?.length
    }

    function onNextImage() {
        setSelectedImage(wrapAround(selectedImage + 1))
    }

    function onPrevImage() {
        setSelectedImage(wrapAround(selectedImage - 1))

    }

    function savePolygons(event) {
        imageRest.savePolygons(event).then(response => {
            reloadImages();
        });
    }

    if (!classifications || !images) {
        return <Typography>{t("general.loading")}</Typography>
    }

    if (images.length === 0) {
        return <Typography>{t("parkingConfig.image.empty")}</Typography>
    }

    return (
        <>
            <ReactImageAnnotate
                labelImages
                regionClsList={classifications.map(classification => classification.name)}
                onExit={event => {
                    console.log("save image");
                    console.log(event.images);
                    savePolygons(event.images)
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
        </>
    );
}
export default ImageAnnotate
