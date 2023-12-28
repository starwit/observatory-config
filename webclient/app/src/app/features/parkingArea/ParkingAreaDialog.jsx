import {
    Box, Button,
    Dialog,Input,
    DialogActions, DialogContent, FormControl, Stack
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
import zIndex from "@mui/material/styles/zIndex";
import UploadFileIcon from "@mui/icons-material/UploadFile";


function ParkingAreaDialog(props) {
    const {open, onClose, id, isCreate} = props;
    const {t} = useTranslation();
    const [entity, setEntity] = useImmer(entityDefault);
    const [fields, setFields] = useImmer(entityFields);
    const entityRest = useMemo(() => new ParkingAreaRest(), []);
    const parkingconfigRest = useMemo(() => new ParkingConfigRest(), []);
    const [hasFormError, setHasFormError] = React.useState(false);
    const [image, setImage] = useImmer();

    useEffect(() => {
        if (isCreate) {
            setEntity(entityDefault);
        } else {
            reloadSelectLists();
        }
    }, [id, isCreate]);

    useEffect(() => {
        onEntityChange();
    }, [entity]);

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

    function onDialogClose() {
        onClose();
    }

    function onEntityChange() {
        setHasFormError(!isValid(fields, entity));
    }

    function handleSubmit(event) {
        // turn off page reload
        event.preventDefault();
        const tmpOrg = prepareForSave(entity, fields);
        console.log(tmpOrg);
        if (isCreate) {
            var entity = entityRest.create(tmpOrg).then();
            console.log(entity);
        } else {
            entityRest.update(tmpOrg).then();
        }
        onClose();
    }

    const handleImageUpload = (e) => {
    if (!e.target.files) {
      return;
    }
    setImage(e.target.files);
  };

    function getDialogTitle() {
        if (!id) {
            return "parkingArea.create.title";
        }
        return "parkingArea.update.title";
    }

    return (
        <Dialog onClose={onDialogClose} open={open} spacing={2} sx={{zIndex: 10000}}>
            <DialogHeader onClose={onDialogClose} title={t(getDialogTitle())} />
            <form autoComplete="off" onSubmit={handleSubmit}>
                <DialogContent>

                    <Stack>
                        <FormControl>
                            <ValidatedTextField
                                fullWidth
                                autoFocus
                                label={t("parkingArea.name")}
                                name={fields[0].name}
                                value={entity[fields[0].name]}
                                type={fields[0].type}
                                variant="standard"
                                onChange={e => handleChange(e, setEntity)}
                                max={fields[0].max}
                                helperText={t("parkingArea.name.hint")}
                            />
                            <input
                            accept="image/*"
                            id="image-picker"
                            type="file"
                            hidden
                            onChange={handleImageUpload}
                            />
                            <label htmlFor="image-picker">
                            <Button variant="outlined" component="span" startIcon={<UploadFileIcon />}>
                                Upload Image
                            </Button>
                            </label> 
                        </FormControl>
                    </Stack >

                </DialogContent>
                <DialogActions>
                    <Button
                        onClick={onDialogClose}
                    >{t("button.cancel")}</Button>
                    <Button
                        type="submit"
                        disabled={hasFormError}>
                        {t("button.save")}
                    </Button>
                </DialogActions>
            </form>
        </Dialog >

    );
}

ParkingAreaDialog.propTypes = {
    id: PropTypes.number,
    open: PropTypes.bool.isRequired,
    onClose: PropTypes.func.isRequired,
    isCreate: PropTypes.bool.isRequired
};

export default ParkingAreaDialog;
