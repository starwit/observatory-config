import React, {useEffect, useMemo, useState} from "react";
import {
    Button,
    Checkbox,
    Dialog,
    DialogActions,
    DialogContent,
    DialogTitle,
    Divider,
    List,
    ListItem,
    ListItemButton,
    ListItemIcon,
    ListItemText,
    Typography
} from "@mui/material";
import {useTranslation} from "react-i18next";
import PropTypes from "prop-types";
import ObservationAreaRest from "../../services/ObservationAreaRest";

function downloadJson(data) {
    const json = JSON.stringify(data, null, 2);
    const blob = new Blob([json], {type: "application/json"});
    const url = URL.createObjectURL(blob);
    const timestamp = new Date().toISOString().slice(0, 19).replace(/[:T]/g, "-");
    const link = document.createElement("a");
    link.href = url;
    link.download = `observatory-config-export-${timestamp}.json`;
    document.body.appendChild(link);
    link.click();
    link.remove();
    URL.revokeObjectURL(url);
}

function ObservationAreaExportDialog(props) {
    const {open, onClose, observationAreas} = props;
    const {t} = useTranslation();
    const observationAreaRest = useMemo(() => new ObservationAreaRest(), []);
    const [selectedIds, setSelectedIds] = useState([]);
    const [exporting, setExporting] = useState(false);

    const areas = observationAreas ?? [];
    const allSelected = areas.length > 0 && selectedIds.length === areas.length;
    const someSelected = selectedIds.length > 0 && !allSelected;

    // preselect all areas whenever the dialog is opened
    useEffect(() => {
        if (open) {
            setSelectedIds(areas.map(area => area.id));
        }
    }, [open, observationAreas]);

    function toggleArea(id) {
        setSelectedIds(current => current.includes(id) ?
            current.filter(selectedId => selectedId !== id) :
            [...current, id]);
    }

    function toggleAll() {
        setSelectedIds(allSelected ? [] : areas.map(area => area.id));
    }

    function exportSelected() {
        setExporting(true);
        observationAreaRest.exportAreas(selectedIds)
            .then(response => {
                downloadJson(response.data);
                onClose();
            })
            .finally(() => setExporting(false));
    }

    return (
        <Dialog sx={{zIndex: 10000}} open={open} onClose={onClose} fullWidth maxWidth="sm">
            <DialogTitle>{t("observationAreas.export.title")}</DialogTitle>
            <DialogContent dividers>
                {areas.length === 0 ?
                    <Typography variant="body1">{t("observationAreas.empty")}</Typography> :
                    <List dense disablePadding>
                        <ListItem disablePadding>
                            <ListItemButton onClick={toggleAll}>
                                <ListItemIcon>
                                    <Checkbox
                                        edge="start"
                                        checked={allSelected}
                                        indeterminate={someSelected}
                                        tabIndex={-1}
                                        disableRipple />
                                </ListItemIcon>
                                <ListItemText primary={t("observationAreas.export.selectAll")} />
                            </ListItemButton>
                        </ListItem>
                        <Divider />
                        {areas.map(area => (
                            <ListItem key={area.id} disablePadding>
                                <ListItemButton onClick={() => toggleArea(area.id)}>
                                    <ListItemIcon>
                                        <Checkbox
                                            edge="start"
                                            checked={selectedIds.includes(area.id)}
                                            tabIndex={-1}
                                            disableRipple />
                                    </ListItemIcon>
                                    <ListItemText primary={area.name} secondary={area.saeStreamKey} />
                                </ListItemButton>
                            </ListItem>
                        ))}
                    </List>
                }
            </DialogContent>
            <DialogActions>
                <Button onClick={onClose}>{t("button.cancel")}</Button>
                <Button
                    onClick={exportSelected}
                    disabled={selectedIds.length === 0 || exporting}
                    variant="contained">
                    {t("observationAreas.export.submit", {count: selectedIds.length})}
                </Button>
            </DialogActions>
        </Dialog>
    );
}

ObservationAreaExportDialog.propTypes = {
    open: PropTypes.bool.isRequired,
    onClose: PropTypes.func.isRequired,
    observationAreas: PropTypes.array
};

export default ObservationAreaExportDialog;
