import {Checkbox, ListItemText} from "@mui/material";
import FormControl from "@mui/material/FormControl";
import MenuItem from "@mui/material/MenuItem";
import Select from "@mui/material/Select";
import {useTranslation} from "react-i18next";
import {CLASS_COLORS, CLASS_MAPPING, classColorCss} from "./ObjectClasses";

const ALL_CLASS_IDS = "all";

function ObjectClassFilter(props) {
    const {selectedClassIds = [], onChange, sx, formSx} = props;
    const {t} = useTranslation();

    function handleChange(event) {
        const selectedValues = event.target.value;
        const values = typeof selectedValues === "string" ? selectedValues.split(",") : selectedValues;
        onChange(values.includes(ALL_CLASS_IDS) ? [] : values.map(Number));
    }

    return (
        <FormControl sx={{width: "12rem", ...formSx}}>
            <Select
                multiple
                displayEmpty
                value={selectedClassIds}
                onChange={handleChange}
                renderValue={ids => ids.length === 0
                    ? t("observationArea.trajectoryFilter.allClasses")
                    : ids.map(classId => t(`mapping.${CLASS_MAPPING[classId]}`)).join(", ")}
                inputProps={{"aria-label": t("observationArea.trajectoryFilter.label")}}
                sx={{height: "2rem", margin: "0rem", ...sx}}
            >
                <MenuItem value={ALL_CLASS_IDS}>
                    <Checkbox checked={selectedClassIds.length === 0} />
                    <ListItemText primary={t("observationArea.trajectoryFilter.allClasses")} />
                </MenuItem>
                {Object.keys(CLASS_COLORS).map(classId => (
                    <MenuItem key={classId} value={Number(classId)}>
                        <Checkbox checked={selectedClassIds.includes(Number(classId))} />
                        <span
                            aria-hidden="true"
                            style={{
                                width: "0.75rem",
                                height: "0.75rem",
                                marginRight: "0.5rem",
                                borderRadius: "50%",
                                backgroundColor: classColorCss(classId)
                            }}
                        />
                        <ListItemText primary={t(`mapping.${CLASS_MAPPING[classId]}`)} />
                    </MenuItem>
                ))}
            </Select>
        </FormControl>
    );
}

export default ObjectClassFilter;
