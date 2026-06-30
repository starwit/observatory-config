import {createContext, useState, useContext} from 'react';

// Default values
const DEFAULT_TRAJECTORY_DECAY = 120;


// LocalStorage keys
const STORAGE_KEYS = {
    TRAJECTORY_DECAY: 'sae_trajectory_decay'
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

export const SettingsProvider = ({children}) => {
    const [trajectoryDecay, setTrajectoryDecayState] = useState(() =>
        getStoredValue(STORAGE_KEYS.TRAJECTORY_DECAY, DEFAULT_TRAJECTORY_DECAY)
    );


    // Wrapper functions that update both state and localStorage
    const setTrajectoryDecay = (value) => {
        setTrajectoryDecayState(value);
        setStoredValue(STORAGE_KEYS.TRAJECTORY_DECAY, value);
    };



    // Reset all settings to defaults
    const resetSettings = () => {
        setTrajectoryDecay(DEFAULT_TRAJECTORY_DECAY);
    };

    const value = {
        trajectoryDecay,
        setTrajectoryDecay,
        trajectoryDecayMs: trajectoryDecay * 1000,
        resetSettings,
    };

    return (
        <SettingsContext.Provider value={value}>
            {children}
        </SettingsContext.Provider>
    );
};

export default SettingsContext;
