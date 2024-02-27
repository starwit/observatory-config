import React from "react";
import {Fab} from "@mui/material";
import {styled} from "@mui/material/styles";
import {Add} from "@mui/icons-material";
import AddFabButtonStyles from "./AddFabButtonStyles";
import PropTypes from "prop-types";
import {useTranslation} from "react-i18next";

function AddFabButton(props) {
    const {t} = useTranslation();
    const {onClick} = props;

    const FabWrapper = styled('div')(({theme}) => AddFabButtonStyles.fabWrapper(theme));

    return (
        <FabWrapper>
            <Fab aria-label={t("button.create")} onClick={onClick}>
                <Add/>
            </Fab>
        </FabWrapper>
    );
}

AddFabButton.propTypes = {
    onClick: PropTypes.func.isRequired
};

export default AddFabButton;
