import React, { createContext, useState, useContext, useEffect } from 'react';

// Default values
const DEFAULT_TRAJECTORY_DECAY = 120;
const DEFAULT_HEATMAP_EXPIRY = 60;
const DEFAULT_HEATMAP_RADIUS = 20;
const DEFAULT_HEATMAP_USE_COORDINATES = false;
const DEFAULT_HEATMAP_MIN_UPDATE_INTERVAL = 200; // in ms

// LocalStorage keys
const STORAGE_KEYS = {
    TRAJECTORY_DECAY: 'sae_trajectory_decay',
    HEATMAP_EXPIRY: 'sae_heatmap_expiry',
    HEATMAP_RADIUS: 'sae_heatmap_radius',
    HEATMAP_USE_COORDINATES: 'sae_heatmap_use_coordinates',
    HEATMAP_MIN_UPDATE_INTERVAL: 'sae_heatmap_min_update_interval',
};

// Helper functions for localStorage
const getStoredValue = (key, defaultValue) => {
    try {
        const stored = localStorage.getItem(key);
        return stored !== null ? JSON.parse(stored) : defaultValue;
    } catch {
        return defaultValue;
    }
};

const setStoredValue = (key, value) => {
    try {
        localStorage.setItem(key, JSON.stringify(value));
    } catch {
        // Silently fail if localStorage is not available
    }
};

const SettingsContext = createContext();

export const useSettings = () => {
    const context = useContext(SettingsContext);
    if (!context) {
        throw new Error('useSettings must be used within a SettingsProvider');
    }
    return context;
};

export const SettingsProvider = ({ children }) => {
    const [trajectoryDecay, setTrajectoryDecayState] = useState(() => 
        getStoredValue(STORAGE_KEYS.TRAJECTORY_DECAY, DEFAULT_TRAJECTORY_DECAY)
    );
    const [heatmapExpiry, setHeatmapExpiryState] = useState(() => 
        getStoredValue(STORAGE_KEYS.HEATMAP_EXPIRY, DEFAULT_HEATMAP_EXPIRY)
    );
    const [heatmapRadius, setHeatmapRadiusState] = useState(() => 
        getStoredValue(STORAGE_KEYS.HEATMAP_RADIUS, DEFAULT_HEATMAP_RADIUS)
    );
    const [heatmapUseCoordinates, setHeatmapUseCoordinatesState] = useState(() => 
        getStoredValue(STORAGE_KEYS.HEATMAP_USE_COORDINATES, DEFAULT_HEATMAP_USE_COORDINATES)
    );
    const [heatmapMinUpdateInterval, setHeatmapMinUpdateIntervalState] = useState(() => 
        getStoredValue(STORAGE_KEYS.HEATMAP_MIN_UPDATE_INTERVAL, DEFAULT_HEATMAP_MIN_UPDATE_INTERVAL)
    );

    // Wrapper functions that update both state and localStorage
    const setTrajectoryDecay = (value) => {
        setTrajectoryDecayState(value);
        setStoredValue(STORAGE_KEYS.TRAJECTORY_DECAY, value);
    };

    const setHeatmapExpiry = (value) => {
        setHeatmapExpiryState(value);
        setStoredValue(STORAGE_KEYS.HEATMAP_EXPIRY, value);
    };

    const setHeatmapRadius = (value) => {
        setHeatmapRadiusState(value);
        setStoredValue(STORAGE_KEYS.HEATMAP_RADIUS, value);
    };

    const setHeatmapUseCoordinates = (value) => {
        setHeatmapUseCoordinatesState(value);
        setStoredValue(STORAGE_KEYS.HEATMAP_USE_COORDINATES, value);
    };

    const setHeatmapMinUpdateInterval = (value) => {
        setHeatmapMinUpdateIntervalState(value);
        setStoredValue(STORAGE_KEYS.HEATMAP_MIN_UPDATE_INTERVAL, value);
    };

    // Reset all settings to defaults
    const resetSettings = () => {
        setTrajectoryDecay(DEFAULT_TRAJECTORY_DECAY);
        setHeatmapExpiry(DEFAULT_HEATMAP_EXPIRY);
        setHeatmapRadius(DEFAULT_HEATMAP_RADIUS);
        setHeatmapUseCoordinates(DEFAULT_HEATMAP_USE_COORDINATES);
        setHeatmapMinUpdateInterval(DEFAULT_HEATMAP_MIN_UPDATE_INTERVAL);
    };

    const value = {
        trajectoryDecay,
        setTrajectoryDecay,
        trajectoryDecayMs: trajectoryDecay * 1000,
        
        heatmapExpiry,
        setHeatmapExpiry,
        heatmapExpiryMs: heatmapExpiry * 1000,
        
        heatmapRadius,
        setHeatmapRadius,

        heatmapUseCoordinates,
        setHeatmapUseCoordinates,

        heatmapMinUpdateInterval,
        setHeatmapMinUpdateInterval,

        resetSettings,
    };

    return (
        <SettingsContext.Provider value={value}>
            {children}
        </SettingsContext.Provider>
    );
};

export default SettingsContext;
