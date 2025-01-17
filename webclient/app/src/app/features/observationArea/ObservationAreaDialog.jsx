import {Button, Dialog, DialogActions, DialogContent, FormControl, Stack, Typography} from "@mui/material";
import Grid from "@mui/material/Grid2";
import {toast} from "react-toastify";
import PropTypes from "prop-types";
import React, {useEffect, useMemo, useState} from "react";
import {useDropzone} from 'react-dropzone';
import {useTranslation} from "react-i18next";
import {useImmer} from "use-immer";
import ImageUploadStyles from "../../assets/styles/ImageUploadStyles";
import DialogHeader from "../../commons/dialog/DialogHeader";
import UpdateField from "../../commons/form/UpdateField";
import {handleChange, isValid, prepareForSave} from "../../modifiers/DefaultModifier";
import {entityDefault, entityFields} from "../../modifiers/ObservationAreaModifier";
import ImageRest, {imageFileUrlForId} from "../../services/ImageRest";
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
    const [hasFormError, setHasFormError] = useState(false);
    const [imageChanged, setImageChanged] = useState(false);
    const [imageBlob, setImageBlob] = useState(null);

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

    async function loadExistingImage() {
        if (selectedArea.image !== null) {
            const imageBlob = await (await fetch(imageFileUrlForId(selectedArea.image.id))).blob();
            setImageBlob(imageBlob);
        }
    }

    useEffect(() => {
        setHasFormError(!allFieldsValid())
    }, [entity, imageBlob]);

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
        if (imageBlob === null) {
            return false;
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

    function handleSaeIdsChange(newIds) {
        setEntity(draft => {
            draft["saeIds"] = newIds;
        });
    }

    function makeEntityUpdateField(field, {width = 12, autofocus = false}) {
        return (
            <Grid key={field.name} item xs={width}>
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

    return (
        <Dialog onClose={onDialogClose} open={open} spacing={2} sx={{zIndex: 10000, "& .MuiDialog-container": {alignItems: "flex-start", mt: "10vh"}}} maxWidth="lg">
            <DialogHeader onClose={onDialogClose} title={t(`observationArea.${mode}.title`)} />
            <form autoComplete="off">
                <DialogContent>
                    <Grid container spacing={2}>
                        <Grid item container spacing={2} xs={8}>
                            {makeEntityUpdateField(fields[0], {width: 12, autofocus: true})}
                            {fields?.slice(2, 4).map(field => makeEntityUpdateField(field, {width: 6}))}
                            <Grid item xs={12}>
                                <CamIDList values={entity?.saeIds} handleChange={handleSaeIdsChange} />
                            </Grid>
                            {makeEntityUpdateField(fields[1], {width: 12})}
                            {entity.geoReferenced && fields?.slice(4).map(field => makeEntityUpdateField(field, {width: 6}))}
                        </Grid>
                        <Grid item xs={4}>
                            <Stack>
                                <FormControl {...getRootProps()} sx={ImageUploadStyles.dropzoneStyle}>
                                    <input {...getInputProps()} />
                                    <Typography variant="overline">{t("observationArea.image")}</Typography>
                                    {imageBlob && <img src={URL.createObjectURL(imageBlob)} style={ImageUploadStyles.previewStyle} alt={t("observationArea.image.preview")} />}
                                </FormControl>
                                {imageBlob === null ?
                                    <Typography variant="caption" color="#d32f2f">{t("observationArea.image.hint")}</Typography> : null
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
