import {Button, CircularProgress, Dialog, DialogActions, DialogContent, FormControl, IconButton, Stack, Tooltip, Typography, TextField, Divider} from "@mui/material";
import {Grid} from '@mui/material';
import CameraAltIcon from "@mui/icons-material/CameraAlt";
import DeleteIcon from "@mui/icons-material/Delete";
import {toast} from "react-toastify";
import PropTypes from "prop-types";
import React, {useEffect, useMemo, useState} from "react";
import {useDropzone} from 'react-dropzone';
import {useTranslation} from "react-i18next";
import {useImmer} from "use-immer";
import ImageUploadStyles from "../../assets/styles/ImageUploadStyles";
import DialogHeader from "../../commons/dialog/DialogHeader";
import UpdateField from "../../commons/form/UpdateField";
import UpdateFieldStyles from "../../commons/form/UpdateFieldStyles";
import ValidatedTextField from "../../commons/form/ValidatedTextField";
import {handleChange, isValid, prepareForSave} from "../../modifiers/DefaultModifier";
import {entityDefault, entityFields} from "../../modifiers/ObservationAreaModifier";
import ImageRest, {imageFileUrlForId} from "../../services/ImageRest";
import ObservationAreaRest from "../../services/ObservationAreaRest";
import {Add} from "@mui/icons-material";

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
    const [hasFormError, setHasFormError] = useState(false);
    const [imageChanged, setImageChanged] = useState(false);
    const [imageBlob, setImageBlob] = useState(null);
    const [saeImageLoading, setSaeImageLoading] = useState(false);

    useEffect(() => {
        if (open === true) {
            if (mode === MODE.CREATE) {
                setEntity(entityDefault);
            } else if (mode === MODE.UPDATE) {
                setEntity(selectedArea);
                loadExistingImage();
            } else if (mode === MODE.COPY) {
                let newArea = structuredClone(selectedArea);
                newArea.id = null;
                newArea.name = `${newArea.name} - ${t("observationArea.copy.suffix")}`;
                setEntity(newArea);
                loadExistingImage();
                setImageChanged(true);
            }
        } else {
            setImageBlob(null);
            setImageChanged(false);
        }
    }, [selectedArea, mode, open]);

    function loadExistingImage() {
        if (selectedArea.image !== null) {
            fetch(imageFileUrlForId(selectedArea.image.id))
                .then(response => response.blob())
                .then(imageBlob => setImageBlob(imageBlob));
        }
    }

    useEffect(() => {
        setHasFormError(!allFieldsValid())
    }, [entity, imageBlob]);

    function allFieldsValid() {
        if (!isValid(fields, entity)) {
            return false;
        }
        if (entity.saeStreamKey === undefined ||
            entity.saeStreamKey === null ||
            entity.saeStreamKey === "") {
            return false;
        }
        // Validate links if present
        let linkList = entity.links.reduce((acc, link) => acc && link.name && link.name.trim() !== "" && link.url && link.url.trim() !== "", true);
        if (linkList && linkList.length > 0) {
            for (const link of entity.links) {
                if (!link.name || link.name.trim() === "" || !link.url || link.url.trim() === "") {
                    return false;
                }
            }
        }
        return true;
    }

    const onDropAccepted = (acceptedFiles) => {
        const file = acceptedFiles[0];
        setImageBlob(file);
        setImageChanged(true);
    };

    const onDropRejected = event => {
        if (event[0].errors.map(e => e.code).includes("file-invalid-type")) {
            toast.warn(t("observationArea.image.invalidType"));
        } else if (event[0].errors.map(e => e.code).includes("file-too-large")) {
            toast.warn(t("observationArea.image.tooLarge"));
        } else {
            toast.error(t("observationArea.image.unknownError"));
        }
    };

    const {getRootProps, getInputProps} = useDropzone({
        onDropAccepted,
        onDropRejected,
        maxSize: 4194304,
        multiple: false,
        accept: {
            "image/png": [".png", ".PNG"],
            "image/jpg": [".jpg", ".JPG", ".jpeg", ".JPEG"],
        },
    });

    function onDialogClose(_, reason) {
        if (["backdropClick", "escapeKeyDown"].includes(reason)) {
            return;
        }
        onSubmit();
    }

    function handleSubmit(event) {
        // turn off page reload
        event.preventDefault();
        const preparedEntity = prepareForSave(entity, fields);
        if (mode === MODE.UPDATE) {
            observationAreaRest.update(preparedEntity).then(({data: newArea}) => {
                uploadFile(newArea.id);
            });
        } else if (mode === MODE.CREATE) {
            observationAreaRest.create(preparedEntity).then(({data: newArea}) => {
                uploadFile(newArea.id);
            });
        } else if (mode === MODE.COPY) {
            observationAreaRest.create(preparedEntity).then(({data: newArea}) => {
                uploadFile(newArea.id);
                observationAreaRest.copyPolygons(newArea.id, selectedArea.id);
            });
        }
        onSubmit();
    }

    function uploadFile(observationAreaId) {
        if (!imageChanged || imageBlob === null) {
            update();
            return;
        }
        const formData = new FormData();
        formData.append('image', imageBlob);
        try {
            imageRest.upload(formData, observationAreaId).then(() => {
                update();
            });
        } catch (error) {
            console.error(error);
            update();
        }
    }

    function handleSaeStreamKeyChange(newValue) {
        setEntity(draft => {
            draft["saeStreamKey"] = newValue;
        });
    }

    const saeImageDisabled = saeImageLoading || entity.geoReferenced || !entity?.saeStreamKey;

    function handleGrabFromSae() {
        setSaeImageLoading(true);
        imageRest.fetchFromSae(entity.saeStreamKey)
            .then(({data: blob}) => {
                setImageBlob(blob);
                setImageChanged(true);
            })
            .catch(() => {/* error is reported by the global error handler */})
            .finally(() => setSaeImageLoading(false));
    }

    function makeEntityUpdateField(field, {width = 12, autofocus = false}) {
        return (
            <Grid key={field.name} size={width}>
                <FormControl key={field.name} fullWidth>
                    <UpdateField
                        autoFocus={autofocus}
                        entity={entity}
                        field={field}
                        prefix="observationArea"
                        handleChange={e => handleChange(e, setEntity)}
                    />
                </FormControl>
            </Grid>
        );
    }

    function handleAddLink() {
        setEntity(draft => {
            if (!draft.links) {
                draft.links = [];
            }
            draft.links.push({id: null, name: "", url: ""});
        });
    }

    function handleUpdateLink(index, field, value) {
        setEntity(draft => {
            if (draft.links && draft.links[index]) {
                draft.links[index][field] = value;
            }
        });
    }

    function handleDeleteLink(index) {
        setEntity(draft => {
            if (draft.links) {
                draft.links.splice(index, 1);
            }
        });
    }

    function renderLinksSection() {
        return (
            <Grid size={{xs: 12}} sx={{mt: 2}}>
                <Divider sx={{mb: 2}} />
                <Stack spacing={2}>
                    <Typography variant="h6">{t("observationArea.links")}</Typography>
                    {(!entity?.links || entity.links.length === 0) &&
                        <Stack direction="row" spacing={1} alignitems="flex-start">
                            <IconButton
                                onClick={handleAddLink}
                                size="small"
                                sx={{mt: 1}}
                            >
                                <Add />
                            </IconButton>
                        </Stack>
                    }
                    {entity?.links?.map((link, index) => (
                        <Stack key={index} spacing={1}>
                            <Stack direction="row" spacing={1} alignitems="flex-start">
                                <FormControl fullWidth>
                                    <TextField
                                        label={t("observationArea.link.name")}
                                        value={link.name || ""}
                                        onChange={(e) => handleUpdateLink(index, "name", e.target.value)}
                                        variant="standard"
                                        size="small"
                                        fullWidth
                                        required
                                        error={!link.name || link.name.trim() === ""}
                                    />
                                </FormControl>
                                <FormControl fullWidth>
                                    <TextField
                                        label={t("observationArea.link.url")}
                                        value={link.url || ""}
                                        onChange={(e) => handleUpdateLink(index, "url", e.target.value)}
                                        variant="standard"
                                        size="small"
                                        fullWidth
                                        required
                                        error={!link.url || link.url.trim() === ""}
                                    />
                                </FormControl>
                                <IconButton
                                    onClick={() => handleDeleteLink(index)}
                                    size="small"
                                    color="error"
                                    sx={{mt: 1}}
                                >
                                    <DeleteIcon />
                                </IconButton>
                                {index === entity.links.length - 1 && (
                                    <IconButton
                                        onClick={handleAddLink}
                                        size="small"
                                        sx={{mt: 1}}
                                    >
                                        <Add />
                                    </IconButton>
                                )}
                            </Stack>

                        </Stack>
                    ))}
                </Stack>
            </Grid>
        );
    }

    return (
        <Dialog onClose={onDialogClose} open={open} spacing={2} sx={{zIndex: 10000, "& .MuiDialog-container": {alignItems: "flex-start", mt: "10vh"}}} maxWidth="lg">
            <DialogHeader onClose={onDialogClose} title={t(`observationArea.${mode}.title`)} />
            <form autoComplete="off">
                <DialogContent>
                    <Grid container spacing={2} size={{xs: 12}}>
                        <Grid container spacing={2} size={{xs: 8}}>
                            {makeEntityUpdateField(fields[0], {width: 12, autofocus: true})}
                            {fields?.slice(2, 4).map(field => makeEntityUpdateField(field, {width: 6}))}
                            <Grid size={{xs: 12}}>
                                <Stack direction="row" alignitems="flex-end" spacing={1}>
                                    <FormControl fullWidth>
                                        <ValidatedTextField
                                            value={entity?.saeStreamKey ?? ""}
                                            onChange={(e) => handleSaeStreamKeyChange(e.target.value)}
                                            label={t("observationArea.saeStreamKey")}
                                            sx={UpdateFieldStyles.textField}
                                            variant="standard"
                                            fullWidth
                                            helperText={""}
                                            notNull
                                        />
                                    </FormControl>
                                    <Tooltip
                                        slotProps={{popper: {sx: {zIndex: 10001}}}}
                                        title={
                                            entity.geoReferenced
                                                ? t("observationArea.renewImage.geoReferencedDisabled")
                                                : !entity?.saeStreamKey
                                                    ? ""
                                                    : t("observationArea.getImageFromSae")
                                        }>
                                        <span>
                                            <IconButton
                                                onClick={handleGrabFromSae}
                                                disabled={saeImageDisabled}
                                            >
                                                {saeImageLoading ? <CircularProgress size={20} /> : <CameraAltIcon color={saeImageDisabled ? "disabled" : "primary"} />}
                                            </IconButton>
                                        </span>
                                    </Tooltip>
                                </Stack>
                            </Grid>
                            {makeEntityUpdateField(fields[1], {width: 12})}
                            {entity.geoReferenced && fields?.slice(4).map(field => makeEntityUpdateField(field, {width: 6}))}
                            {renderLinksSection()}
                        </Grid>
                        <Grid size={{xs: 4}}>
                            <Stack>
                                <FormControl {...getRootProps()} sx={ImageUploadStyles.dropzoneStyle}>
                                    <input {...getInputProps()} />
                                    <Typography variant="overline">{t("observationArea.image")}</Typography>
                                    {imageBlob && <img src={URL.createObjectURL(imageBlob)} style={ImageUploadStyles.previewStyle} alt={t("observationArea.image.preview")} />}
                                </FormControl>
                                {imageBlob === null ?
                                    <Typography variant="caption" color="text.secondary">{t("observationArea.image.hint")}</Typography> : null
                                }
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
