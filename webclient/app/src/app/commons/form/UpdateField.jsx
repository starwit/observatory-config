import React from "react";
import {useTranslation} from "react-i18next";
import ValidatedTextField from "./ValidatedTextField";
import UpdateFieldStyles from "./UpdateFieldStyles";
import {Checkbox, FormControlLabel, FormGroup} from "@mui/material";
import {isNumber} from '../../modifiers/DefaultModifier';

function UpdateField(props) {
    const {entity, field, prefix, handleChange, ...newProps} = props;
    const {t} = useTranslation();
    let defaultValue = "";

    if (field.type === "boolean") {
        return (
            <FormGroup>
                <FormControlLabel
                    control={
                        <Checkbox
                            checked={entity[field.name] !== null ? entity[field.name] : ""}
                            value={entity[field.name] !== null ? entity[field.name] : ""}
                            name={field.name} onChange={handleChange} key={field.name}
                            id={"checkbox-" + field.name}
                            label={t(prefix + "." + field.name)}
                        />
                    }
                    label={t(prefix + "." + field.name)}
                />
            </FormGroup>
        );
    }

    let type = field.type;
    if (isNumber(field.type)){
        type = 'number';
        defaultValue = 0;
    }

    return (
        <ValidatedTextField
            autoFocus
            inputProps={field.inputProps}
            key={field.name}
            id={"input-" + field.name}
            label={t(prefix + "." + field.name)}
            helperText={t(prefix + "." + field.name + ".hint")}
            name={field.name}
            type={type}
            value={entity[field.name] !== null ? entity[field.name] : defaultValue}
            sx={UpdateFieldStyles.textField}
            variant="standard"
            onChange={handleChange}
            isCreate={!entity?.id}
            regex={field.regex}
            notNull={field.notNull}
            min={field.min}
            max={field.max}
            {...newProps}
        />
    );
}

export default UpdateField;
