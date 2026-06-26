import {useEffect, useState} from "react";
import TrajectoryDrawer from './TrajectoryDrawer';



function ObservationVisualization(props) {

    const {streams, imageSize} = props;


    const [selectedStream, setSelectedStream] = useState("");

    useEffect(() => {
        // Fetch available streams from backend
        setSelectedStream(streams && streams[0] ? streams[0] : "");
    }, [streams]);



    return (
        <>

            <TrajectoryDrawer
                imageSize={imageSize}
                key={selectedStream}
                stream={selectedStream}
                running={true} />

        </>
    )
}

export default ObservationVisualization;