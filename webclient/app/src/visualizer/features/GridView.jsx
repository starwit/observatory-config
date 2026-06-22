import React, { useEffect, useState, useMemo, useRef } from "react";
import { useTranslation } from 'react-i18next';
import StreamRest from "../services/StreamRest";
import { Box, Card, Fab, FormControl, IconButton, InputLabel, MenuItem, OutlinedInput, Select, Stack, Typography } from "@mui/material";
import PlayArrowIcon from '@mui/icons-material/PlayArrow';
import StopIcon from '@mui/icons-material/Stop';
import TrajectoryDrawer from "../commons/TrajectoryDrawer";
import Chip from '@mui/material/Chip';

function GridView() {
    const { t } = useTranslation();
    const streamRest = useMemo(() => new StreamRest(), []);

    const [streams, setStreams] = useState([]);
    const [selectedStreams, setSelectedStreams] = useState([]);

    const [running, setRunning] = useState(false);

    useEffect(() => {
        streamRest.getAvailableStreams().then(response => {
            setStreams(response.data);
        });
    }, []);

    // Reset tracked objects when stream is changed
    useEffect(() => {
        setRunning(false);
    }, [selectedStreams]);
    
    function toggleStream() {
        setRunning(!running);
    }

    const handleStreamSelectChange = (event) => {
        setSelectedStreams(event.target.value);
    };

    return (
        <>
            <div style={{
                position: 'absolute', 
                display: 'grid',
                placeItems: 'center',
                height: '100vh',
                width: '100vw',
                top: 0,
                left: 0,
            }}> 
                <div style={{
                    position: 'relative', 
                    aspectRatio: '16/9', 
                    height: 'calc(100vh - 250px)',
                    maxWidth: '100vw',
                    display: 'grid',
                    gridTemplateRows: '1fr 1fr',
                    gridTemplateColumns: '1fr 1fr',
                    gridGap: '10px',
                }}>
                    {selectedStreams.map(stream => (
                        <TrajectoryDrawer
                            key={stream}
                            stream={stream}
                            running={running}
                            label={stream}
                        />
                    ))}
                </div>
            </div>
            <Box sx={{
                position: 'fixed',
                top: 60,
                right: 10
            }}>
                <Typography variant="h1">
                    {t('grid.title')}
                </Typography>
            </Box>
            <Box sx={{
                position: 'fixed',
                top: 60,
                left: 10,
                width: '100%'
            }}>
                <Stack direction="row">
                    <FormControl sx={{ width: 500 }}>
                        <InputLabel id="stream-select">Stream</InputLabel>
                        <Select
                            labelId="stream-select"
                            id="stream-select-id"
                            value={selectedStreams}
                            label="Streams"
                            onChange={handleStreamSelectChange}
                            multiple={true}
                        >
                            {streams.map((stream) => (
                                <MenuItem key={stream} value={stream}>
                                    {stream}
                                </MenuItem>
                            ))}
                        </Select>
                    </FormControl>
                    <Fab 
                        color="primary"
                        onClick={toggleStream}
                    >
                        {!running ?
                            <PlayArrowIcon/> :
                            <StopIcon/>}
                    </Fab>
                </Stack>
            </Box>
        </>
    )
}

export default GridView;