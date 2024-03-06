import {
    Button,
    Dialog, Typography,
    DialogActions, DialogContent, FormControl, Stack, TextField
} from "@mui/material";
import PropTypes from "prop-types";
import React, {useMemo, useEffect, useState} from "react";
import {useImmer} from "use-immer";
import {useTranslation} from "react-i18next";
import DialogHeader from "../../commons/dialog/DialogHeader";
import ValidatedTextField from "../../commons/validatedTextField/ValidatedTextField";
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
import ImageRest from "../../services/ImageRest";
import {useDropzone} from 'react-dropzone';
import ImageUploadStyles from "../../assets/styles/ImageUploadStyles";
import { useSnackbar } from "notistack";
import CamIDField from "../../commons/CamIDField/CamIDField";

function ParkingAreaDialog(props) {
    const {open, onClose, selected, isCreate, update} = props;
    const {t} = useTranslation();
    const [entity, setEntity] = useImmer(entityDefault);
    const fields = entityFields;
    const entityRest = useMemo(() => new ParkingAreaRest(), []);
    const imageRest = useMemo(() => new ImageRest(), []);
    const [hasFormError, setHasFormError] = React.useState(false);
    const [selectedFile, setSelectedFile] = useState(null);
    const [previewUrl, setPreviewUrl] = useState('');
    const { enqueueSnackbar } = useSnackbar();

    const onDropAccepted = (acceptedFiles) => {
        const file = acceptedFiles[0];
        setSelectedFile(file);

        // Vorschaubild erzeugen
        const reader = new FileReader();
        reader.onloadend = () => {
            setPreviewUrl(reader.result);
        };
        reader.readAsDataURL(file);
    };

    const onDropRejected = () => {
        enqueueSnackbar(t("parkingArea.fileTooLarge"), {variant: "warning"});
    };


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

    const {getRootProps, getInputProps} = useDropzone({onDropAccepted, onDropRejected, maxSize: 4194304});

    function onDialogClose() {
        setSelectedFile(null);
        setPreviewUrl('');
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
            entityRest.update(tmpOrg)
                .then(response => {
                    uploadFile(response.data, response.data?.selectedProdConfigId);
                });
        } else {
            entityRest.create(tmpOrg).then(response => {
                uploadFile(response.data, response.data?.selectedProdConfigId);
            });
        }
        onClose();
    }

    function uploadFile(data, activeParkingConfigId) {
        if (!selectedFile) {
            update(data);
            return;
        }
        const formData = new FormData();
        formData.append('image', selectedFile);
        try {
            imageRest.upload(formData, activeParkingConfigId).then(() => {
                update(data);
            });
        } catch (error) {
            console.error(error);
            update(data);
        }
    }

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
                        </FormControl>
                        <Typography variant="caption">{t("parkingArea.image.hint")}</Typography>
                        <FormControl {...getRootProps()} sx={ImageUploadStyles.dropzoneStyle}>
                            <input {...getInputProps()} />
                            <Typography variant="overline">{t("parkingArea.image")}</Typography>
                            {previewUrl && <img src={previewUrl} style={ImageUploadStyles.previewStyle} alt={t("parkingArea.image.preview")} />}
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
