import React, {useState} from 'react';
import axios from 'axios';
import {useDropzone} from 'react-dropzone';
import {Button, TextField, Typography, Container, Paper, Avatar, FormControl} from '@mui/material';

const ImageUpload = () => {
    const [selectedFile, setSelectedFile] = useState(null);
    const [name, setName] = useState('');
    const [email, setEmail] = useState('');
    const [previewUrl, setPreviewUrl] = useState('');

    const onDrop = (acceptedFiles) => {
        const file = acceptedFiles[0];
        setSelectedFile(file);

        // Vorschaubild erzeugen
        const reader = new FileReader();
        reader.onloadend = () => {
            setPreviewUrl(reader.result);
        };
        reader.readAsDataURL(file);
    };

    const uploadFile = async () => {
        if (!selectedFile || !name || !email) {
            alert('Bitte füllen Sie alle Felder aus und wählen Sie eine Datei aus.');
            return;
        }

        const formData = new FormData();
        formData.append('image', selectedFile);
        formData.append('name', name);
        formData.append('email', email);

        try {

            console.log('Upload successful:', response.data);
            // Hier kannst du die Antwort weiterverarbeiten, z.B. den Dateipfad anzeigen.
        } catch (error) {
            console.error('Upload failed:', error);
        }
    };

    const {getRootProps, getInputProps, isDragActive} = useDropzone({onDrop});

    return (
        <>
            <Typography variant="caption">Bild hochladen</Typography>
            <FormControl {...getRootProps()} style={dropzoneStyle}>
                <input {...getInputProps()} />
                {isDragActive ? (
                    <Typography variant="overline">Datei hier ablegen...</Typography>
                ) : (
                    <Typography variant="overline">Datei hier ablegen oder klicken, um eine Datei auszuwählen.</Typography>
                )}
                {previewUrl && <img src={previewUrl} alt="Vorschau" style={previewStyle} />}
            </FormControl>
            {/*
            <TextField
                fullWidth
                id="name"
                label="Name"
                name="name"
                value={name}
                onChange={(e) => setName(e.target.value)}
            />
            <TextField
                fullWidth
                id="email"
                label="Email"
                name="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
            />
            <Button
                type="button"
                fullWidth
                variant="contained"
                color="primary"
                onClick={uploadFile}
                style={uploadButtonStyle}
            >
                Bild hochladen
            </Button>
*/}
        </>
    );
};

const dropzoneStyle = {
    border: '2px dashed #cccccc',
    borderRadius: '4px',
    padding: '5px',
    textAlign: 'center',
    cursor: 'pointer',
    marginTop: '5px',
};

const previewStyle = {
    maxWidth: '100%',
    maxHeight: '200px',
    margin: '10px 0',
};

const uploadButtonStyle = {
    marginTop: '20px',
};

export default ImageUpload;
