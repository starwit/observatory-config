import { Button, Dialog, DialogActions, DialogContent, FormControl, Grid, Stack, Typography } from "@mui/material";
import { useSnackbar } from "notistack";
import PropTypes from "prop-types";
import React, { useEffect, useMemo, useState } from "react";
import { useDropzone } from 'react-dropzone';
import { useTranslation } from "react-i18next";
import { useImmer } from "use-immer";
import ImageUploadStyles from "../../assets/styles/ImageUploadStyles";
import CamIDList from "./CamIDList";
import DialogHeader from "../../commons/dialog/DialogHeader";
import UpdateField from "../../commons/form/UpdateField";
import { handleChange, isValid, prepareForSave } from "../../modifiers/DefaultModifier";
import { entityDefault, entityFields } from "../../modifiers/ObservationAreaModifier";
import ImageRest from "../../services/ImageRest";
import ObservationAreaRest from "../../services/ObservationAreaRest";

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
    const entityRest = useMemo(() => new ObservationAreaRest(), []);
    const imageRest = useMemo(() => new ImageRest(), []);
    const [hasFormError, setHasFormError] = React.useState(false);
    const [selectedFile, setSelectedFile] = useState(null);
    const [previewUrl, setPreviewUrl] = useState('');
    const {enqueueSnackbar} = useSnackbar();

    useEffect(() => {
        if (mode === MODE.CREATE) {
            setEntity(entityDefault);
        } else if (mode === MODE.UPDATE) {
            setEntity(selectedArea);
        } else if (mode === MODE.COPY) {
            let newArea = structuredClone(selectedArea);
            newArea.id = null;
            newArea.name = `${newArea.name} - Copy`;
            setEntity(newArea);
        }
    }, [selectedArea, mode]);
    
    useEffect(() => {
        onEntityChange();
    }, [entity]);
    
    function onEntityChange() {
        setHasFormError(!isValid(fields, entity));
    }

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
        enqueueSnackbar(t("observationArea.fileTooLarge"), {variant: "warning"});
    };

    const {getRootProps, getInputProps} = useDropzone({onDropAccepted, onDropRejected, maxSize: 4194304});

    function onDialogClose() {
        setSelectedFile(null);
        setPreviewUrl('');
        onSubmit();
    }

    function handleSubmit(event) {
        // turn off page reload
        event.preventDefault();
        const tmpOrg = prepareForSave(entity, fields);
        if (entity.id) {
            entityRest.update(tmpOrg)
                .then(response => {
                    uploadFile(response.data, response.data?.id);
                });
        } else {
            entityRest.create(tmpOrg).then(response => {
                uploadFile(response.data, response.data?.id);
            });
        }
        onSubmit();
    }

    function uploadFile(data, observationAreaId) {
        if (!selectedFile) {
            update();
            return;
        }
        const formData = new FormData();
        formData.append('image', selectedFile);
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
                                    focused
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
                                    <CamIDList value={entity?.saeIds} handleChange={handleSaeIdsChange}/>
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
