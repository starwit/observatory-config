import {
    Box, Button,
    Dialog,
    DialogActions, DialogContent, Stack
} from "@mui/material";
import PropTypes from "prop-types";
import React, {useMemo, useEffect} from "react";
import {useParams} from "react-router-dom";
import {useImmer} from "use-immer";
import {useTranslation} from "react-i18next";
import DialogHeader from "../../commons/dialog/DialogHeader";
import {ValidatedTextField, addSelectLists} from "@starwit/react-starwit";
import {
    handleChange,
    handleDateTime,
    handleMultiSelect,
    handleSelect,
    isDate,
    isDateTime,
    isEnum,
    isInput,
    isMultiSelect,
    isSelect,
    isTime,
    isValid,
    prepareForSave
} from "../../modifiers/DefaultModifier";
import ParkingAreaRest from "../../services/ParkingAreaRest";
import ParkingConfigRest from "../../services/ParkingConfigRest";
import {
    entityDefault,
    entityFields
} from "../../modifiers/ParkingAreaModifier";

function ParkingAreaDialog(props) {
    const {open, onClose} = props;
    const {t} = useTranslation();
    const [entity, setEntity] = useImmer(entityDefault);
    const [fields, setFields] = useImmer(entityFields);
    const entityRest = useMemo(() => new ParkingAreaRest(), []);
    const parkingconfigRest = useMemo(() => new ParkingConfigRest(), []);
    const [hasFormError, setHasFormError] = React.useState(false);
    const {id} = useParams();

    function onDialogClose() {
        onClose();
    }

    useEffect(() => {
        reloadSelectLists();
    }, [id]);

    useEffect(() => {
        onEntityChange();
    }, [entity]);

    function onEntityChange() {
        setHasFormError(!isValid(fields, entity));
    }

    function reloadSelectLists() {
        const selectLists = [];
        const functions = [
            parkingconfigRest.findAllWithoutParkingArea(id)
        ];
        Promise.all(functions).then(values => {
            selectLists.push({name: "parkingConfig", data: values[0].data});
            if (id) {
                entityRest.findById(id).then(response => {
                    setEntity(response.data);
                    selectLists.push({name: "selectedTestConfig", data: response.data.parkingConfig});
                    selectLists.push({name: "selectedProdConfig", data: response.data.parkingConfig});
                    addSelectLists(response.data, fields, setFields, selectLists);
                });
            } else {
                addSelectLists(entity, fields, setFields, selectLists);
            }
        });
    }

    return (
        <Dialog onClose={onDialogClose} open={open} spacing={2}>
            <DialogHeader onClose={onDialogClose} title={t("parkingArea.create.title")} />
            <DialogContent>
                <Stack>
                    <ValidatedTextField
                        fullWidth
                        autoFocus
                        label={t("parkingArea.name") + "*"}
                        name="name"
                        value={entity["name"]}
                        type="string"
                        variant="standard"
                        onChange={e => handleChange(e, setEntity)}
                        max={10}
                        helperText={t("parkingArea.name.hint")}
                    />
                </Stack >
            </DialogContent>
            <DialogActions>
                <Button>{t("button.cancel")}</Button>
                <Button
                    type="submit"
                    variant="contained"
                    color="secondary"
                    disabled={hasFormError}>
                    {t("button.save")}
                </Button>
            </DialogActions>
        </Dialog >

    );
}

ParkingAreaDialog.propTypes = {
    open: PropTypes.bool.isRequired,
    onClose: PropTypes.func.isRequired
};

export default ParkingAreaDialog;
