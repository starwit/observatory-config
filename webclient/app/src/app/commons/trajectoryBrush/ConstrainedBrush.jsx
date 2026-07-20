import {Brush} from "@visx/brush";
import PropTypes from "prop-types";
import {useCallback, useEffect, useRef} from "react";

/** Continuous scales (time, linear, …) are invertible; band/point scales are not. */
function isContinuous(scale) {
    return "invert" in scale && typeof scale.invert === "function";
}

/**
 * Constrains `state.extent` so the selection is at most `maxWidth` domain units wide, anchoring the
 * edge the user is *not* dragging. Returns the constrained state, or null when no constraining is needed.
 *
 * Only `extent` is touched: while brushing, BaseBrush keeps `start`/`end` as the pre-drag anchors
 * and recomputes the extent from them plus the total pointer offset, so rewriting them mid-drag
 * would make the next pointer move jump.
 */
function constrainExtent(state, xScale, maxWidth) {
    if (!isContinuous(xScale)) return null;

    const {extent, bounds, start, end, activeHandle, brushingType} = state;
    const x0 = Math.min(extent.x0, extent.x1);
    const x1 = Math.max(extent.x0, extent.x1);

    // Idle / not-yet-drawn brush.
    if (x0 < 0 || x1 <= x0) return null;
    // Moving the selection preserves its width, so there is nothing to constrain.
    if (brushingType === "move") return null;

    const toDomain = (px) => Number(xScale.invert(px));
    const toPixels = (domainValue) => Number(xScale(domainValue));

    if (toDomain(x1) - toDomain(x0) <= maxWidth) return null;

    // The pixel the drag pivots around, i.e. the edge the user is *not* moving. BaseBrush keeps this
    // in start/end: dragging the left handle pivots on `end`, the right handle and a new selection
    // pivot on `start`. Programmatic updates have no drag, so they pivot on the left edge.
    // `brushingType` is only set with useWindowMoveEvents; otherwise handle drags set `activeHandle`.
    const dragType = brushingType ?? activeHandle;
    const anchor = dragType === "left" ? end.x : dragType === "right" || dragType === "select" ? start.x : x0;

    // Which extent edge is the anchor? Not necessarily the one matching the drag direction: dragging a
    // handle past the opposite one swaps them, since BaseBrush normalizes the extent to x0 <= x1.
    const anchorIsRightEdge = Math.abs(anchor - x1) < Math.abs(anchor - x0);

    const constrained = anchorIsRightEdge
        ? {x0: Math.max(bounds.x0, toPixels(toDomain(x1) - maxWidth)), x1}
        : {x0, x1: Math.min(bounds.x1, toPixels(toDomain(x0) + maxWidth))};

    const nextExtent = {...extent, ...constrained};

    return {
        ...state,
        extent: nextExtent,
        // Outside of a drag (initial position, programmatic updates) start/end are the source of
        // truth for the rendered selection, so they have to follow the constrained extent.
        ...(state.isBrushing
            ? {}
            : {
                start: {...start, x: nextExtent.x0},
                end: {...end, x: nextExtent.x1},
            }),
    };
}

/**
 * A `Brush` whose selection can never be wider than `maxWidth` x-domain units.
 *
 * Takes every `Brush` prop, plus `maxWidth`: the maximum selection width in x-domain units.
 * For a time scale that means milliseconds (e.g. 20 * 60 * 1000 for 20 minutes).
 */
function ConstrainedBrush({maxWidth, xScale, onChange, ...brushProps}) {
    const brushRef = useRef(null);
    
    // Read xScale through a ref so `constrain`'s identity does not change on resize (a resize builds a
    // new xScale). See the effect below for why re-running constrain on scale changes is harmful.
    const xScaleRef = useRef(xScale);
    xScaleRef.current = xScale;

    /** Returns true when the brush was constrained (which re-triggers onChange with the constrained bounds). */
    const constrain = useCallback(() => {
        const brush = brushRef.current;
        if (!brush) return false;

        const constrained = constrainExtent(brush.state, xScaleRef.current, maxWidth);
        if (!constrained) return false;

        brush.updateBrush(() => constrained);
        return true;
    }, [maxWidth]);

    // Enforce the constraint on the initial brush position and whenever maxWidth or the scale change.
    //
    // Deliberately NOT re-run on xScale changes. A window resize builds a new xScale, but BaseBrush
    // already rescales the selection's pixel extent proportionally to the new width in its own
    // componentDidUpdate, which preserves the domain-width constraint automatically (pixels scale with
    // the range, so the domain span is unchanged). Re-running constrain here would fire during the
    // resize while brush.state still holds the pre-rescale pixels (x1 === oldWidth) and convert them
    // with the new, smaller-range scale — making the domain span appear to exceed maxWidth and constraining
    // to an out-of-range x1, which clobbers BaseBrush's rescale and leaves the selection past the end.
    useEffect(() => {
        constrain();
    }, [constrain]);

    const handleChange = useCallback(
        (bounds) => {
            // Constrain selection if necessary (updated === true means the values were out of bounds and therefore updated)
            const updated = constrain();

            // Swallow the out-of-bounds value: constraining fires onChange again with the constrained selection.
            if (updated) return;
            onChange?.(bounds);
        },
        [constrain, onChange],
    );

    return <Brush {...brushProps} xScale={xScale} innerRef={brushRef} onChange={handleChange} />;
}

ConstrainedBrush.propTypes = {
    maxWidth: PropTypes.number.isRequired,
    xScale: PropTypes.func.isRequired,
    onChange: PropTypes.func,
};

export default ConstrainedBrush;
