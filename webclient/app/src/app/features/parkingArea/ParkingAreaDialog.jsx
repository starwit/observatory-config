import React from "react";
import {Box, Button, Dialog, DialogActions, DialogTitle, IconButton, TextField} from "@mui/material";
import {Close} from "@mui/icons-material";
import PropTypes from "prop-types";
import {useTranslation} from "react-i18next";
import DialogStyles from "./DialogStyles";

function ParkingAreaDialog(props) {
    const {open, onClose} = props;
    const dialogStyles = DialogStyles();
    const {t} = useTranslation();

    function onDialogClose() {
        onClose();
    }

    return (
        <Dialog onClose={onDialogClose} open={open} spacing={2}>
            <DialogTitle className={dialogStyles.dialogHeaderBar}>
                {t("parkingArea.title")}
                <div className={dialogStyles.flex} />
                <IconButton
                    aria-label="close"
                    onClick={onDialogClose}
                >
                    <Close />
                </IconButton>
            </DialogTitle>
            <Box
                component="form"
                sx={{
                    "& .MuiTextField-root": {m: 1, width: "90%"}

                }}
                noValidate
                autoComplete="off">
                <TextField
                    fullWidth
                    label={t("parkingArea.name")}
                    value={"test"}
                    name="name"
                />
                <DialogActions>
                    <Button>{t("button.cancel")}</Button>
                    <Button disabled={false}
                    >{t("button.save")}</Button>
                </DialogActions>
            </Box >
        </Dialog >

    );
}

ParkingAreaDialog.propTypes = {
    open: PropTypes.bool.isRequired,
    onClose: PropTypes.func.isRequired
};

export default ParkingAreaDialog;
