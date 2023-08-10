import ReactImageAnnotate from "react-image-annotate";
import {useEffect, useMemo, useState} from "react";
import React from "react";
import ClassificationRest from "../../services/ClassificationRest";
import {Typography} from "@mui/material";
import ImageRest from "../../services/ImageRest";

function ImageAnnotate() {
    const [classifications, setClassifications] = useState(null);
    const [images, setImages] = useState(null);
    const [parsedImages, setParsedImages] = useState([{
        src: window.location.pathname + "api/imageFile/name/image_south.jpg",
        name: "Kamera 1"
    }]);
    const classificationRest = useMemo(() => new ClassificationRest(), []);
    const imageRest = useMemo(() => new ImageRest(), []);

    useEffect(() => {
        classificationRest.findAll().then(response => {
            setClassifications(response.data);
        });
    }, []);

    useEffect(() => {
        imageRest.findAll().then(response => {
            setImages(response.data);
        });
    }, []);

    useEffect(() => {
        if (!images) {
            return [{
                src: window.location.pathname + "api/imageFile/name/image_south.jpg",
                name: "Kamera 1"
            }];
        }
        setParsedImages(images.map(image => {
            return {
                src: window.location.pathname + "api/imageFile/name/" + image.src,
                name: image.name
            };
        }));
    }, [images]);

    if (!classifications || !parsedImages) {
        return <Typography>Loading</Typography>;
    }

    return (
        <ReactImageAnnotate
            labelImages
            regionClsList={classifications.map(classification => classification.name)}
            // regionClsList={["Sperrung", "Überwachungsbereich", "Parkbereich"]}
            enabledTools={["select", "create-point", "create-polygon", "create-box"]}
            hideSettings={true}
            hideClone={true}
            hideFullScreen={true}
            hideNext={true}
            hidePrev={true}
            //            images={parsedImages}
            images={[
                {
                    src:
                        "https://starwit-technologies.de/wp-content/uploads/2023/07/parking_south.jpg",
                    name: "Kamera 1"
                }
            ]}

        />
    );
}
export default ImageAnnotate;
