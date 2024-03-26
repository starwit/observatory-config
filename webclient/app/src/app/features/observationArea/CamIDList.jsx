import AddIcon from '@mui/icons-material/Add';
import RemoveIcon from '@mui/icons-material/Remove';
import { Stack } from '@mui/material';
import IconButton from '@mui/material/IconButton';
import PropTypes from "prop-types";
import { useTranslation } from "react-i18next";
import UpdateFieldStyles from '../../commons/form/UpdateFieldStyles';
import ValidatedTextField from '../../commons/form/ValidatedTextField';

function CamIDList(props) {
  const {values, handleChange} = props;
  const {t} = useTranslation();

  const handleAddTextField = () => {
    handleChange([...values, '']);
  };

  const handleRemoveTextField = (removeIdx) => {
    if (removeIdx !== 0) {
      const newValues = values.filter((_, idx) => idx !== removeIdx);
      handleChange(newValues);
    }
  };

  const handleInputChange = (index, newValue) => {
    const newValues = structuredClone(values);
    newValues[index] = newValue;
    handleChange(newValues);
  };

  return (
    <>
      {values?.map((value, index) => (
        <Stack key={index} direction={'row'}>
          <ValidatedTextField
            value={value !== undefined ? value : ""}
            onChange={(e) => handleInputChange(index, e.target.value)}
            label={t("observationArea.saeIds") + ` ${index + 1}`}
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
          {index === values.length - 1 && (
            <IconButton onClick={handleAddTextField} aria-label={t("button.create")}>
              <AddIcon />
            </IconButton>
          )}
        </Stack>
      ))}
    </>
  );
}

CamIDList.propTypes = {
  values: PropTypes.array,
  handleChange: PropTypes.func
};

export default CamIDList;