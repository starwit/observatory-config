import {useEffect, useMemo, useRef, useState} from "react";
import DeckGL from "@deck.gl/react";
import {OrthographicView} from "@deck.gl/core";
import {PathLayer} from "@deck.gl/layers";
import DetectionRest from "../../services/DetectionRest";
import {classColor, isClassVisible} from "../../commons/objectClassFilter/ObjectClasses";

// Saved trajectories are drawn translucent so dense areas remain readable.
const SAVED_TRAJECTORY_ALPHA = 100;

function SavedTrajectoryDrawer(props) {
    const {streamKey, width, height, start, end, selectedClassIds = []} = props;
    const detectionRest = useRef(new DetectionRest());

    const [classTrajectories, setClassTrajectories] = useState([]);

    useEffect(() => {
        if (!start || !end) {
            setClassTrajectories([]);
            return;
        }
        detectionRest.current.findTrajectories(start, end, streamKey).then(result => {
            setClassTrajectories(result.data);
        });
    }, [streamKey, start, end]);

    const viewState = useMemo(() => {
        if (!width || !height) return null;
        return {
            target: [width / 2, height / 2, 0],
            zoom: 0,
            minZoom: -5,
            maxZoom: 10
        };
    }, [width, height]);

    const visibleClassTrajectories = classTrajectories.filter(({classId}) => isClassVisible(classId, selectedClassIds));

    const layers = visibleClassTrajectories.flatMap(({classId, tracedObjects}) => {
        const color = classColor(classId, SAVED_TRAJECTORY_ALPHA);
        const paths = tracedObjects
            .filter(tracedObject => tracedObject.trajectory.length >= 2)
            .map(tracedObject => ({path: tracedObject.trajectory.map(point => [point.x * width, point.y * height])}));

        return new PathLayer({
            id: `paths-class-${classId}`,
            data: paths,
            getPath: dataItem => dataItem.path, //what is d?
            getColor: color,
            getWidth: 2,
            widthUnits: 'pixels',
            capRounded: true,
        });
    });

    return (
        <DeckGL
            views={new OrthographicView({id: 'ortho', flipY: true})}
            viewState={viewState}
            controller={false}
            layers={layers}
            getCursor={() => 'default'}
        />
    );
}

export default SavedTrajectoryDrawer;