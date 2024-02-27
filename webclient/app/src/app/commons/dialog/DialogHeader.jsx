import {Close} from "@mui/icons-material";
import {
    DialogTitle, IconButton
} from "@mui/material";
import PropTypes from "prop-types";
import React from "react";
import {useTranslation} from "react-i18next";

function DialogHeader(props) {
    const {t} = useTranslation();
    const {title, onClose} = props;

    function onDialogClose() {
        onClose();
    }

    return (
        <>
            <DialogTitle>
                {title}
            </DialogTitle>
            <IconButton
                aria-label={t("button.close")}
                onClick={onDialogClose}
                sx={{
                    position: "absolute",
                    right: 8,
                    top: 8,
                    color: theme => theme.palette.grey[500]
                }}
            >
                <Close />
            </IconButton>
        </>
    );
}

DialogHeader.propTypes = {
    title: PropTypes.string,
    onClose: PropTypes.func.isRequired
};

export default DialogHeader;
