
import { useEffect, useRef, useState } from "react";
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

function calculateViewportDimensions(containerWidth, containerHeight, frameWidth, frameHeight) {
    if (!frameWidth || !frameHeight) return { width: containerWidth, height: containerHeight };
    const frameAspectRatio = frameWidth / frameHeight;
    const containerAspectRatio = containerWidth / containerHeight;
    if (containerAspectRatio > frameAspectRatio) {
        const viewHeight = containerHeight;
        return { width: viewHeight * frameAspectRatio, height: viewHeight };
    }
    const viewWidth = containerWidth;
    return { width: viewWidth, height: viewWidth / frameAspectRatio };
}

function SavedTrajectoryDrawer(props) {
    const { streamKey } = props;
    const detectionRest = useRef(new DetectionRest());
    const containerRef = useRef(null);

    const [classTrajectories, setClassTrajectories] = useState([]);
    const [shape, setShape] = useState({});
    const [viewState, setViewState] = useState({ target: [0, 0, 0], zoom: -1, minZoom: -5, maxZoom: 10 });

    useEffect(() => {
        detectionRest.current.findTrajectories(new Date(), 10, streamKey).then(result => {
            const data = result.data;
            setClassTrajectories(data);
            let maxX = 0, maxY = 0;
            data.forEach(({ tracedObjects }) =>
                tracedObjects.forEach(({ trajectory }) =>
                    trajectory.forEach(({ x, y }) => {
                        if (x > maxX) maxX = x;
                        if (y > maxY) maxY = y;
                    })
                )
            );
            if (maxX && maxY) setShape({ width: maxX, height: maxY });
        });
    }, [streamKey]);

    useEffect(() => {
        const updateDimensions = () => {
            if (!shape.width || !shape.height || !containerRef.current) return;
            const containerWidth = containerRef.current.clientWidth;
            const containerHeight = containerRef.current.clientHeight;
            const { width: viewWidth, height: viewHeight } = calculateViewportDimensions(
                containerWidth, containerHeight, shape.width, shape.height
            );
            const scale = Math.min(viewWidth / shape.width, viewHeight / shape.height);
            setViewState({
                target: [shape.width / 2, shape.height / 2, 0],
                zoom: Math.log2(scale),
                minZoom: -5,
                maxZoom: 10
            });
        };
        updateDimensions();
        window.addEventListener('resize', updateDimensions);
        return () => window.removeEventListener('resize', updateDimensions);
    }, [shape]);

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
        <div ref={containerRef} style={{ position: 'absolute', top: '55px', left: '10px', right: '310px', bottom: '40px' }}>
            {layers.length > 0 && (
                <DeckGL
                    views={new OrthographicView({ id: 'ortho', flipY: true })}
                    viewState={viewState}
                    controller={false}
                    layers={layers}
                    getCursor={() => 'default'}
                />
            )}
        </div>
    );
}

export default SavedTrajectoryDrawer;