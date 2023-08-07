import ReactImageAnnotate from "react-image-annotate";
import {useEffect, useMemo, useState} from "react";
import ClassificationRest from "../../services/ClassificationRest";
import {Typography} from "@mui/material";

function ImageAnnotate(props){

    const [classifications, setClassifications] = useState(null);
    const classificationRest = useMemo(() => new ClassificationRest(), []);

    useEffect(() => {
        classificationRest.findAll().then(response => {
            setClassifications(response.data)
        })
    }, [])

    if (!classifications){
        return <Typography>Loading</Typography>
    }

    return(
        <ReactImageAnnotate
            labelImages
            regionClsList={classifications.map(classification => classification.name)}
            regionTagList={[]}
            onExit={evnt => {
                console.log(evnt.images.map(image => image.regions))
                console.log(evnt)
            }}
            enabledTools={["select", "create-point", "create-polygon", "create-box"]}
            images={[
                {
                    src: "https://placekitten.com/408/287",
                    name: "Image 1",
                    regions: []
                }
            ]}
        />
    )
}
export default ImageAnnotate