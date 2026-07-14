import {AxisBottom, AxisLeft} from "@visx/axis";
import {curveMonotoneX} from "@visx/curve";
import {LinearGradient} from "@visx/gradient";
import {Group} from "@visx/group";
import {AreaClosed} from "@visx/shape";
import {useTheme} from "@mui/material/styles";
import PropTypes from "prop-types";

// accessors
const getDate = (d) => new Date(d.timestamp);
const getObjectCount = (d) => d.objectCount;

// Follows the browser locale but forces a 24h clock (hourCycle "h23"). Midnight ticks show the date
// instead, so multi-day intervals stay readable.
const timeFormatter = new Intl.DateTimeFormat(undefined, {hour: "2-digit", minute: "2-digit", hourCycle: "h23"});
const dateFormatter = new Intl.DateTimeFormat(undefined, {day: "2-digit", month: "2-digit"});

function formatAxisTick(value) {
    const date = value instanceof Date ? value : new Date(value);
    if (date.getHours() === 0 && date.getMinutes() === 0) {
        return dateFormatter.format(date);
    }
    return timeFormatter.format(date);
}

function BrushAreaChart({data, width, yMax, margin, xScale, yScale, children}) {
    const theme = useTheme();

    if (width < 10) return null;

    const axisColor = theme.palette.text.secondary;
    const gradientColor = theme.palette.primary.main;
    const tickLabelProps = {
        fill: axisColor,
        fontFamily: theme.typography.fontFamily,
        fontSize: 10,
    };
    return (
        <Group left={margin.left} top={margin.top}>
            <LinearGradient
                id="trajectory-brush-gradient"
                from={gradientColor}
                fromOpacity={0.8}
                to={gradientColor}
                toOpacity={0.15}
            />
            <AreaClosed
                data={data}
                x={(d) => xScale(getDate(d)) || 0}
                y={(d) => yScale(getObjectCount(d)) || 0}
                yScale={yScale}
                strokeWidth={1}
                stroke={gradientColor}
                fill="url(#trajectory-brush-gradient)"
                curve={curveMonotoneX}
            />
            <AxisBottom
                top={yMax}
                scale={xScale}
                numTicks={width > 520 ? 10 : 5}
                tickFormat={formatAxisTick}
                stroke={axisColor}
                tickLength={4}
                tickStroke={axisColor}
                tickLabelProps={{...tickLabelProps, textAnchor: "middle"}}
            />
            {children}
        </Group>
    );
}

BrushAreaChart.propTypes = {
    data: PropTypes.array.isRequired,
    width: PropTypes.number.isRequired,
    yMax: PropTypes.number.isRequired,
    margin: PropTypes.object.isRequired,
    xScale: PropTypes.func.isRequired,
    yScale: PropTypes.func.isRequired,
    children: PropTypes.node,
};

export default BrushAreaChart;
