import {Box, CircularProgress, Typography} from "@mui/material";
import {ParentSize} from "@visx/responsive";
import PropTypes from "prop-types";
import {useEffect, useRef, useState} from "react";
import {useTranslation} from "react-i18next";
import DetectionRest from "../../services/DetectionRest";
import BrushChart from "./BrushChart";

const DEFAULT_MAX_WIDTH_MS = 20 * 60 * 1000; // 20 minutes

function TrajectoryBrushFilter({streamKey, onRangeChange, maxWidthMs = DEFAULT_MAX_WIDTH_MS, height = 128}) {
    const {t} = useTranslation();
    const detectionRest = useRef(new DetectionRest());
    const [histogram, setHistogram] = useState(null);

    // Keep the latest callback in a ref so effects don't need it as a dependency (avoids re-firing).
    const onRangeChangeRef = useRef(onRangeChange);
    onRangeChangeRef.current = onRangeChange;

    useEffect(() => {
        let cancelled = false;
        setHistogram(null);
        detectionRest.current.getObjectCountHistogram(streamKey).then((result) => {
            if (!cancelled) setHistogram(result.data);
        });
        return () => {
            cancelled = true;
        };
    }, [streamKey]);

    const hasData = Boolean(histogram?.intervalStart && histogram?.buckets?.length > 0);

    // Load the trajectories for the default (most recent) selection as soon as the histogram arrives.
    useEffect(() => {
        if (!hasData) return;
        const end = new Date(histogram.intervalEnd);
        const startBound = new Date(histogram.intervalStart);
        const start = new Date(Math.max(startBound.getTime(), end.getTime() - maxWidthMs));
        onRangeChangeRef.current({start, end});
    }, [histogram, hasData, maxWidthMs]);

    if (histogram && !hasData) {
        return (
            <Box sx={{height, display: "flex", alignItems: "center", justifyContent: "center"}}>
                <Typography variant="body2" color="text.secondary">
                    {t("observationArea.trajectoryFilter.noRecordings")}
                </Typography>
            </Box>
        );
    }

    if (!hasData) {
        return (
            <Box sx={{height, display: "flex", alignItems: "center", justifyContent: "center"}}>
                <CircularProgress disableShrink/>
            </Box>
        );
    }

    return (
        <Box sx={{height, width: "100%", userSelect: "none"}}>
            <ParentSize>
                {({width}) => (
                    <BrushChart
                        width={width}
                        height={height}
                        histogram={histogram}
                        maxWidthMs={maxWidthMs}
                        onCommit={(range) => onRangeChangeRef.current(range)}
                    />
                )}
            </ParentSize>
        </Box>
    );
}

TrajectoryBrushFilter.propTypes = {
    streamKey: PropTypes.string.isRequired,
    onRangeChange: PropTypes.func.isRequired,
    maxWidthMs: PropTypes.number,
    height: PropTypes.number,
};

export default TrajectoryBrushFilter;
