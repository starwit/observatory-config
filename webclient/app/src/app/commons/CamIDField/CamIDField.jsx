import React, {useEffect, useState} from "react";
import PropTypes from "prop-types";
import IconButton from '@mui/material/IconButton';
import AddIcon from '@mui/icons-material/Add';
import RemoveIcon from '@mui/icons-material/Remove';
import ValidatedTextField from '../form/ValidatedTextField';
import { Stack } from '@mui/material';
import {useTranslation} from "react-i18next";
import UpdateFieldStyles from '../form/UpdateFieldStyles';

function CamIDField(props) {
  const {value, handleChange} = props;
  const {t} = useTranslation();
  const [inputs, setInputs] = useState([""]);

  useEffect(() => {
    if (value !== null && value !== undefined ) {
      setInputs(value);
    } else {
      setInputs([""])
    }
  }, []);

  useEffect(() => {
    handleChange(inputs)
  }, [inputs]);

  const handleAddTextField = () => {
    setInputs([...inputs, '']);
  };

  const handleRemoveTextField = (index) => {
    if (index !== 0) {
      const newInputs = [...inputs];
      newInputs.splice(index, 1);
      setInputs(newInputs);
    }
  };

  const handleInputChange = (index, value) => {
    const newInputs = [...inputs];
    newInputs[index] = value;
    setInputs(newInputs);
  };

  return (
    <>
      {inputs.map((input, index) => (
        <Stack key={index} direction={'row'}>
          <ValidatedTextField
            value={input !== undefined ? input : ""}
            onChange={(e) => handleInputChange(index, e.target.value)}
            label={t("parkingArea.saeIds") + ` ${index + 1}`}
            sx={UpdateFieldStyles.textField}
            variant="standard"
            fullWidth
            helperText={""}
            notNull={index === 0}
          />
          {index !== 0 && (
            <IconButton onClick={() => handleRemoveTextField(index)} aria-label={t("button.delete")}>
              <RemoveIcon />
            </IconButton>
          )}
          {index === inputs.length - 1 && (
            <IconButton onClick={handleAddTextField} aria-label={t("button.create")}>
              <AddIcon />
            </IconButton>
          )}
        </Stack>
      ))}
    </>
  );
}

CamIDField.propTypes = {
  value: PropTypes.array,
  handleChange: PropTypes.func
};
export default CamIDField;