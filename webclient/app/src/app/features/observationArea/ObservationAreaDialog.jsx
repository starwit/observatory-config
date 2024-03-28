import { Button, Dialog, DialogActions, DialogContent, FormControl, Grid, Stack, Typography } from "@mui/material";
import { useSnackbar } from "notistack";
import PropTypes from "prop-types";
import React, { useEffect, useMemo, useState } from "react";
import { useDropzone } from 'react-dropzone';
import { useTranslation } from "react-i18next";
import { useImmer } from "use-immer";
import ImageUploadStyles from "../../assets/styles/ImageUploadStyles";
import DialogHeader from "../../commons/dialog/DialogHeader";
import UpdateField from "../../commons/form/UpdateField";
import { handleChange, isValid, prepareForSave } from "../../modifiers/DefaultModifier";
import { entityDefault, entityFields } from "../../modifiers/ObservationAreaModifier";
import ImageRest, { imageFileUrlForId } from "../../services/ImageRest";
import ObservationAreaRest from "../../services/ObservationAreaRest";
import CamIDList from "./CamIDList";

export const MODE = Object.freeze({
    UPDATE: "update",
    CREATE: "create",
    COPY: "copy",
});

function ObservationAreaDialog(props) {
    const {open, onSubmit, selectedArea, mode, update} = props;
    const {t} = useTranslation();
    const [entity, setEntity] = useImmer(entityDefault);
    const fields = entityFields;
    const observationAreaRest = useMemo(() => new ObservationAreaRest(), []);
    const imageRest = useMemo(() => new ImageRest(), []);
    const [hasFormError, setHasFormError] = React.useState(false);
    const [selectedImageFile, setSelectedImageFile] = useState(null);
    const [previewUrl, setPreviewUrl] = useState('');
    const {enqueueSnackbar} = useSnackbar();
    console.log(selectedArea)

    useEffect(() => {
        if (mode === MODE.CREATE) {
            setEntity(entityDefault);
        } else if (mode === MODE.UPDATE) {
            setEntity(selectedArea);
            previewExistingImage();
        } else if (mode === MODE.COPY) {
            let newArea = structuredClone(selectedArea);
            newArea.id = null;
            newArea.name = `${newArea.name} - ${t("observationArea.copy.suffix")}`;
            setEntity(newArea);
            previewExistingImage();
        }
    }, [selectedArea, mode, open]);

    function previewExistingImage() {
        if (selectedArea.image !== null) {
            setPreviewUrl(imageFileUrlForId(selectedArea.image.id));
        }
    }
    
    useEffect(() => {
        setHasFormError(!allFieldsValid())
    }, [entity]);
    
    function allFieldsValid() {
        if (!isValid(fields, entity)) {
            return false;
        }
        if (entity.saeIds === undefined || 
                entity.saeIds === null || 
                entity.saeIds.length === 0 || 
                entity.saeIds[0] === "") {
            return false;
        }
        return true;
    }

    const onDropAccepted = (acceptedFiles) => {
        const file = acceptedFiles[0];
        setSelectedImageFile(file);

        // Vorschaubild erzeugen
        const reader = new FileReader();
        reader.onloadend = () => {
            setPreviewUrl(reader.result);
        };
        reader.readAsDataURL(file);
    };

    const onDropRejected = () => {
        enqueueSnackbar(t("observationArea.fileTooLarge"), {variant: "warning"});
    };

    const {getRootProps, getInputProps} = useDropzone({onDropAccepted, onDropRejected, maxSize: 4194304});

    function onDialogClose(_, reason) {
        if (["backdropClick", "escapeKeyDown"].includes(reason)) {
            return;
        }
        setSelectedImageFile(null);
        setPreviewUrl('');
        onSubmit();
    }

    function handleSubmit(event) {
        // turn off page reload
        event.preventDefault();
        const tmpOrg = prepareForSave(entity, fields);
        if (mode === MODE.UPDATE) {
            observationAreaRest.update(tmpOrg).then(response => {
                uploadFile(response.data?.id);
            });
        } else if ([MODE.CREATE, MODE.COPY].includes(mode)) {
            observationAreaRest.create(tmpOrg).then(response => {
                uploadFile(response.data?.id);
            });
        }
        onSubmit();
    }

    function uploadFile(observationAreaId) {
        if (!selectedImageFile) {
            update();
            return;
        }
        console.log("uploading")
        const formData = new FormData();
        formData.append('image', selectedImageFile);
        try {
            imageRest.upload(formData, observationAreaId).then(() => {
                update();
            });
        } catch (error) {
            console.error(error);
            update();
        }
    }

    function handleSaeIdsChange(newIds) {
        setEntity(draft => {
            draft["saeIds"] = newIds;
        });
    }

    return (
        <Dialog onClose={onDialogClose} open={open} spacing={2} sx={{zIndex: 10000}} maxWidth="lg">
            <DialogHeader onClose={onDialogClose} title={t(`observationArea.${mode}.title`)}/>
            <form autoComplete="off">
                <DialogContent>
                    <Grid container spacing={2}>
                        <Grid item xs={8}>
                            <FormControl key={fields[0].name} fullWidth>
                                <UpdateField
                                    autoFocus
                                    entity={entity}
                                    field={fields[0]}
                                    prefix="observationArea"
                                    handleChange={e => handleChange(e, setEntity)}
                                />
                            </FormControl>
                        </Grid>
                        <Grid item xs={4}>
                            <FormControl key={fields[1].name} fullWidth>
                                <UpdateField
                                    entity={entity}
                                    field={fields[1]}
                                    prefix="observationArea"
                                    handleChange={e => handleChange(e, setEntity)}
                                />
                            </FormControl>
                        </Grid>
                        <Grid item xs={8}>
                            <Grid container spacing={2}>
                                <Grid item xs={12}>                 
                                    <CamIDList values={entity?.saeIds} handleChange={handleSaeIdsChange}/>
                                </Grid>
                                {fields?.slice(2).map(field => {
                                        return (
                                            <Grid key={field.name} item xs={6}>
                                                <FormControl key={field.name} fullWidth>
                                                    <UpdateField
                                                        entity={entity}
                                                        field={field}
                                                        prefix="observationArea"
                                                        handleChange={e => handleChange(e, setEntity)}
                                                    />
                                                </FormControl>
                                            </Grid>
                                        );
                                    })
                                }
                            </Grid>
                        </Grid>
                        <Grid item xs={4}> 
                            <Stack> 
                            <Typography variant="caption">{t("observationArea.image.hint")}</Typography>
                            <FormControl {...getRootProps()} sx={ImageUploadStyles.dropzoneStyle}>
                                <input {...getInputProps()} />
                                <Typography variant="overline">{t("observationArea.image")}</Typography>
                                {previewUrl && <img src={previewUrl} style={ImageUploadStyles.previewStyle} alt={t("observationArea.image.preview")} />}
                            </FormControl>
                            </Stack> 
                        </Grid>
                    </Grid>
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

ObservationAreaDialog.propTypes = {
    open: PropTypes.bool.isRequired,
    onSubmit: PropTypes.func.isRequired,
    selected: PropTypes.object,
    mode: PropTypes.string,
    update: PropTypes.func.isRequired
};

export default ObservationAreaDialog;
