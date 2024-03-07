import {
    Button,
    Dialog, Typography,
    DialogActions, DialogContent, FormControl, Stack, TextField, Grid
} from "@mui/material";
import PropTypes from "prop-types";
import React, {useMemo, useEffect, useState} from "react";
import {useImmer} from "use-immer";
import {useTranslation} from "react-i18next";
import DialogHeader from "../../commons/dialog/DialogHeader";
import ValidatedTextField from "../../commons/form/ValidatedTextField";
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
import UpdateField from "../../commons/form/UpdateField";

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

    function handleSaeIdsChange(value) {
        setEntity(draft => {
            draft["saeIds"] = value;
        });
    }

    return (
        <Dialog onClose={onDialogClose} open={open} spacing={2} sx={{zIndex: 10000}} maxWidth="lg">
            <DialogHeader onClose={onDialogClose} title={t(getDialogTitle())}/>
            <form autoComplete="off">
                <DialogContent>
                    <Grid container spacing={2}>
                        <Grid item xs={8}>
                            <FormControl key={fields[0].name} fullWidth>
                                <UpdateField
                                    focused
                                    entity={entity}
                                    field={fields[0]}
                                    prefix="parkingArea"
                                    handleChange={e => handleChange(e, setEntity)}
                                />
                            </FormControl>
                        </Grid>
                        <Grid item xs={4}>
                            <FormControl key={fields[1].name} fullWidth>
                                <UpdateField
                                    entity={entity}
                                    field={fields[1]}
                                    prefix="parkingArea"
                                    handleChange={e => handleChange(e, setEntity)}
                                />
                            </FormControl>
                        </Grid>
                        <Grid item xs={8}>
                            <Grid container spacing={2}>
                                <Grid item xs={12}>                 
                                    <CamIDField value={entity?.saeIds} handleChange={handleSaeIdsChange}/>
                                </Grid>
                                {fields?.slice(2).map(field => {
                                        return (
                                            <Grid key={field.name} item xs={6}>
                                                <FormControl key={field.name} fullWidth>
                                                    <UpdateField
                                                        entity={entity}
                                                        field={field}
                                                        prefix="parkingArea"
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
                            <Typography variant="caption">{t("parkingArea.image.hint")}</Typography>
                            <FormControl {...getRootProps()} sx={ImageUploadStyles.dropzoneStyle}>
                                <input {...getInputProps()} />
                                <Typography variant="overline">{t("parkingArea.image")}</Typography>
                                {previewUrl && <img src={previewUrl} style={ImageUploadStyles.previewStyle} alt={t("parkingArea.image.preview")} />}
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

ParkingAreaDialog.propTypes = {
    open: PropTypes.bool.isRequired,
    onClose: PropTypes.func.isRequired,
    selected: PropTypes.object,
    isCreate: PropTypes.bool.isRequired,
    update: PropTypes.func.isRequired
};

export default ParkingAreaDialog;
