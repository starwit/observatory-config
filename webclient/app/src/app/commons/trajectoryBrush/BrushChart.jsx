import {scaleLinear, scaleTime} from "@visx/scale";
import {max} from "@visx/vendor/d3-array";
import PropTypes from "prop-types";
import {useMemo} from "react";
import BrushAreaChart from "./BrushAreaChart";
import ConstrainedBrush from "./ConstrainedBrush";

const BRUSH_MARGIN = {top: 10, bottom: 20, left: 20, right: 20};

const selectedBoxStyle = {
    fill: "rgba(25, 118, 210, 0.15)",
    stroke: "#1976d2",
};

const getObjectCount = (d) => d.objectCount;

function BrushChart({width, height, histogram, maxWidthMs, onCommit}) {
    const {buckets} = histogram;

    const xBrushMax = Math.max(width - BRUSH_MARGIN.left - BRUSH_MARGIN.right, 0);
    const yBrushMax = Math.max(height - BRUSH_MARGIN.top - BRUSH_MARGIN.bottom, 0);

    const xScale = useMemo(
        () => scaleTime({
            range: [0, xBrushMax],
            domain: [new Date(histogram.intervalStart), new Date(histogram.intervalEnd)],
        }),
        [xBrushMax, histogram.intervalStart, histogram.intervalEnd],
    );

    const yScale = useMemo(
        () => scaleLinear({range: [yBrushMax, 0], domain: [0, max(buckets, getObjectCount) || 1], nice: true}),
        [yBrushMax, buckets],
    );

    const initialBrushPosition = useMemo(() => {
        const end = new Date(histogram.intervalEnd);
        const startBound = new Date(histogram.intervalStart);
        const start = new Date(Math.max(startBound.getTime(), end.getTime() - maxWidthMs));
        return {start: {x: xScale(start)}, end: {x: xScale(end)}};
    }, [xScale, histogram.intervalStart, histogram.intervalEnd, maxWidthMs]);

    const handleBrushEnd = (domain) => {
        if (!domain) return;
        onCommit({start: new Date(domain.x0), end: new Date(domain.x1)});
    };

    if (xBrushMax <= 0 || yBrushMax <= 0) return null;

    return (
        <svg width={width} height={height}>
            <BrushAreaChart
                data={buckets}
                width={width}
                yMax={yBrushMax}
                margin={BRUSH_MARGIN}
                xScale={xScale}
                yScale={yScale}
            >
                <ConstrainedBrush
                    maxWidth={maxWidthMs}
                    xScale={xScale}
                    yScale={yScale}
                    width={xBrushMax}
                    height={yBrushMax}
                    margin={BRUSH_MARGIN}
                    handleSize={8}
                    brushDirection="horizontal"
                    initialBrushPosition={initialBrushPosition}
                    onBrushEnd={handleBrushEnd}
                    selectedBoxStyle={selectedBoxStyle}
                    useWindowMoveEvents
                />
            </BrushAreaChart>
        </svg>
    );
}

BrushChart.propTypes = {
    width: PropTypes.number.isRequired,
    height: PropTypes.number.isRequired,
    histogram: PropTypes.object.isRequired,
    maxWidthMs: PropTypes.number.isRequired,
    onCommit: PropTypes.func.isRequired,
};

export default BrushChart;
