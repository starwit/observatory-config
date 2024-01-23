import {
    Box, Button,
    Dialog,Input,
    DialogActions, DialogContent, FormControl, Stack
} from "@mui/material";
import PropTypes from "prop-types";
import React, {useMemo, useEffect} from "react";
import {useImmer} from "use-immer";
import {useTranslation} from "react-i18next";
import DialogHeader from "../../commons/dialog/DialogHeader";
import {ValidatedTextField} from "@starwit/react-starwit";
import {
    handleChange,
    isValid,
    prepareForSave
} from "../../modifiers/DefaultModifier";
import ParkingAreaRest from "../../services/ParkingAreaRest";
import {
    entityDefault,
    entityFields
} from "../../modifiers/ParkingAreaModifier";
import UploadFileIcon from "@mui/icons-material/UploadFile";
import ImageRest from "../../services/ImageRest";


function ParkingAreaDialog(props) {
    const {open, onClose, selected, isCreate, update} = props;
    const {t} = useTranslation();
    const [entity, setEntity] = useImmer(entityDefault);
    const fields = entityFields;
    const entityRest = useMemo(() => new ParkingAreaRest(), []);
    const imageRest = useMemo(()=> new ImageRest(), []);
    const [hasFormError, setHasFormError] = React.useState(false);
    const [image, setImage] = useImmer();

    useEffect(() => {
        if (isCreate) {
            setEntity(entityDefault);
        } else {
            setEntity(selected);
        }
    }, [selected, isCreate]);

    useEffect(() => {
        onEntityChange();
    }, [entity]);

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
        if (entity.id) {
            entityRest.update(tmpOrg).then(response => update(response.data));
        } else {
            var imageEntity=imageRest.upload(image);
            console.log(imageEntity);
            entityRest.create(tmpOrg).then(response => update(response.data));
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
        if (entity?.id) {
            return "parkingArea.update.title";
        }
        return "parkingArea.create.title";
    }

    return (
        <Dialog onClose={onDialogClose} open={open} spacing={2} sx={{zIndex: 10000}}>
            <DialogHeader onClose={onDialogClose} title={t(getDialogTitle())} />
            <form autoComplete="off">
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
                                {t("button.upload.image")}
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
                        // type="submit"
                        onClick={handleSubmit}
                        disabled={hasFormError}>
                        {t("button.save")}
                    </Button>
                </DialogActions>
            </form>
        </Dialog >

    );
}

ParkingAreaDialog.propTypes = {
    open: PropTypes.bool.isRequired,
    onClose: PropTypes.func.isRequired,
    selected: PropTypes.object,
    isCreate: PropTypes.bool.isRequired,
    update: PropTypes.func.isRequired
};

export default ParkingAreaDialog;
