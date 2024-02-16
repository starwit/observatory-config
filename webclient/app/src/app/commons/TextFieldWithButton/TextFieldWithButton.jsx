import React, { useState } from 'react';
import TextField from '@mui/material/TextField';
import IconButton from '@mui/material/IconButton';
import AddIcon from '@mui/icons-material/Add';
import RemoveIcon from '@mui/icons-material/Remove';

function TextFieldWithAddAndRemoveButton() {
  const [inputs, setInputs] = useState(['']); // Zustand für Eingabefelder

  const handleAddTextField = () => {
    setInputs([...inputs, '']);  // Füge ein weiteres Eingabefeld hinzu
  };

  const handleRemoveTextField = (index) => {
    // Überprüfe, ob es sich nicht um das erste Feld handelt
    if (index !== 0) {
      const newInputs = [...inputs];
      newInputs.splice(index, 1);
      setInputs(newInputs);
    }
  };

  const handleInputChange = (index, value) => {
    const newInputs = [...inputs];
    newInputs[index] = value;
    setInputs(newInputs); // Aktualisiere den Zustand für das entsprechende Eingabefeld
  };

  return (
    <div>
      {inputs.map((input, index) => (
        <div key={index} style={{ display: 'flex', alignItems: 'center', marginBottom: '8px' }}>
          <TextField
            value={input}
            onChange={(e) => handleInputChange(index, e.target.value)}
            label={`CamID ${index + 1}`}
            variant="filled" // Ansonsten weißer Cursor auf weißen Grund
            // fullWidth - sieht extrem scheisse aus!!!
            required={index === 0} // Setze das erste Textfeld als Pflichtfeld
          />
          {index !== 0 && ( // Ein Feld muss stehen bleiben
            <IconButton onClick={() => handleRemoveTextField(index)} aria-label="Löschen">
              <RemoveIcon />
            </IconButton>
          )}
          {index === inputs.length - 1 && (
            <IconButton onClick={handleAddTextField} aria-label="Hinzufügen">
              <AddIcon />
            </IconButton>
          )}
        </div>
      ))}
    </div>
  );
}

export default TextFieldWithAddAndRemoveButton;