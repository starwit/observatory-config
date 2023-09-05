import ReactImageAnnotate from "@starwit/react-image-annotate";
import {useEffect, useMemo, useState} from "react";
import ClassificationRest from "../../services/ClassificationRest";
import {Typography} from "@mui/material";
import ImageRest from "../../services/ImageRest";

function ImageAnnotate(props){

    const [classifications, setClassifications] = useState(null);
    const [images, setImages] = useState(null);
    const [parsedImages, setParsedImages] = useState(null);
    const [selectedImage, setSelectedImage] = useState(0);
    const classificationRest = useMemo(() => new ClassificationRest(), []);
    const imageRest = useMemo(() => new ImageRest(), []);

    useEffect(() => {
        classificationRest.findAll().then(response => {
            setClassifications(response.data)
        })
    }, [])

    useEffect(() => {
        imageRest.findAll().then(response => {
            setImages(response.data)
        })
    }, [])

    useEffect(() => {
        if (!images){
            return
        }
        setParsedImages(images.map(image => {
            return {
                src: window.location.pathname + "api/imageFile/name/" + image.src,
                name: image.name,
                regions: []
            }
        }))
    }, [images])

    function wrapAround(newNumber) {
        return ((newNumber % parsedImages?.length) + parsedImages?.length) % parsedImages?.length
    }

    function onNextImage(){
        setSelectedImage(wrapAround(selectedImage + 1))
    }

    function onPrevImage(){
        setSelectedImage(wrapAround(selectedImage - 1))

    }

    if (!classifications || !parsedImages){
        return <Typography>Loading</Typography>
    }

    if (parsedImages.length === 0){
        return <Typography>No images created. Please add some first</Typography>
    }

    return(
        <ReactImageAnnotate
            labelImages
            regionClsList={classifications.map(classification => classification.name)}
            onExit={evnt => {
                console.log(evnt.images.map(image => image.regions))
                console.log(evnt)
            }}
            enabledTools={["select", "create-point", "create-polygon", "create-box", "create-line"]}
            images={parsedImages}
            hideHeaderText
            selectedImage={selectedImage}
            onNextImage={onNextImage}
            onPrevImage={onPrevImage}
            hideNext={parsedImages.length === 1}
            hidePrev={parsedImages.length === 1}
            hideClone
        />
    )
}
export default ImageAnnotate
