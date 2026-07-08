
import { useEffect, useMemo, useRef, useState } from "react";
import DeckGL from "@deck.gl/react";
import { OrthographicView } from "@deck.gl/core";
import { PathLayer } from "@deck.gl/layers";
import DetectionRest from "../../services/DetectionRest";

const CLASS_COLORS = [
    [255, 100, 100, 220],
    [100, 200, 100, 220],
    [100, 100, 255, 220],
    [255, 200, 50, 220],
    [200, 100, 255, 220],
    [50, 220, 220, 220],
];

function colorForClass(classId) {
    return CLASS_COLORS[classId % CLASS_COLORS.length];
}

function SavedTrajectoryDrawer(props) {
    const { streamKey, width, height, frameWidth, frameHeight } = props;
    const detectionRest = useRef(new DetectionRest());

    const [classTrajectories, setClassTrajectories] = useState([]);

    useEffect(() => {
        detectionRest.current.findTrajectories(new Date(), 10, streamKey).then(result => {
            console.log('SavedTrajectoryDrawer data sample:', JSON.stringify(result.data?.[0]?.tracedObjects?.[0]?.trajectory?.slice(0,3)));
            setClassTrajectories(result.data);
        });
    }, [streamKey]);

    const viewState = useMemo(() => {
        console.log('SavedTrajectoryDrawer viewState inputs:', { width, height, frameWidth, frameHeight });
        if (!frameWidth || !frameHeight || !width || !height) return null;
        return {
            target: [frameWidth / 2, frameHeight / 2, 0],
            zoom: Math.log2(width / frameWidth),
            minZoom: -5,
            maxZoom: 10
        };
    }, [frameWidth, frameHeight, width, height]);

    const layers = classTrajectories.flatMap(({ classId, tracedObjects }) => {
        const color = colorForClass(classId);
        const paths = tracedObjects
            .filter(o => o.trajectory.length >= 2)
            .map(o => ({ path: o.trajectory.map(p => [p.x, p.y]) }));
        return new PathLayer({
            id: `paths-class-${classId}`,
            data: paths,
            getPath: d => d.path,
            getColor: color,
            getWidth: 2,
            widthUnits: 'pixels',
            jointRounded: true,
            capRounded: true,
        });
    });

    return (
        <DeckGL
            views={new OrthographicView({ id: 'ortho', flipY: true })}
            viewState={viewState}
            controller={false}
            layers={layers}
            getCursor={() => 'default'}
        />
    );
}

export default SavedTrajectoryDrawer;