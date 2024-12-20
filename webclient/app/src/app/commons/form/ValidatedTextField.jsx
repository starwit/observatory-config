import React, {useEffect, useState} from "react";
import {TextField} from "@mui/material";
import PropTypes from "prop-types";

function ValidatedTextField(props) {
    const {value = "", regex, isCreate = false, notNull, min, max, type, ...newProps} = props;
    const [error, setError] = useState(false);
    const [changed, setChanged] = useState(false);

    useEffect(() => {
        setError(false);
        if (regex) {
            setError(!regex.test(value));
        }
        if (notNull && (!value || value === "")) {
            setError(true);
        }
        if (!!min && type !== "string" && value !== "" && value < min) {
            setError(true);
        }
        if (!!max && type !== "string" && value !== "" && value > max) {
            setError(true);
        }
        if (!!min && type === "string" && value.length < min) {
            setError(true);
        }
        if (!!max && type === "string" && value.length > max) {
            setError(true);
        }
    }, [value, regex]);

    return (
        <TextField
            error={(!isCreate || changed) && error}
            value={value}
            onBlur={() => setChanged(true)}
            type={type}
            {...newProps}
        />
    );
}

ValidatedTextField.propTypes = {
    helperText: PropTypes.string.isRequired,
    regex: PropTypes.any,
    notNull: PropTypes.bool,
    min: PropTypes.number,
    max: PropTypes.number,
    type: PropTypes.any,
    onChange: PropTypes.func.isRequired,
    value: PropTypes.any,
    isCreate: PropTypes.bool
};

export default ValidatedTextField;
