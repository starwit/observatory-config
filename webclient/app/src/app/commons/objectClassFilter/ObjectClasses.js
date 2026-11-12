// Single source of truth for traffic object classes (ids as delivered by the detector).

export const CLASS_COLORS = {
    0: [255, 100, 100, 255], // pedestrian - red
    1: [100, 200, 100, 255], // bicycle - green
    2: [100, 100, 255, 255], // vehicle - blue
    3: [50, 220, 220, 255],  // motorcycle - cyan
    5: [255, 200, 50, 255],  // bus - orange
    7: [200, 100, 255, 255], // truck - purple
};

export const CLASS_MAPPING = {
    0: 'pedestrian', 1: 'bicycle', 2: 'vehicle', 3: 'motorcycle', 5: 'bus', 7: 'truck'
};

export const DEFAULT_CLASS_COLOR = [180, 180, 180, 255];

export function classColor(classId, alpha) {
    const color = CLASS_COLORS[classId] ?? DEFAULT_CLASS_COLOR;
    return alpha !== undefined ? [color[0], color[1], color[2], alpha] : color;
}

export function classColorCss(classId) {
    const [r, g, b, a] = classColor(classId);
    return `rgba(${r}, ${g}, ${b}, ${a / 255})`;
}

// An empty selection means "all classes".
export function isClassVisible(classId, selectedClassIds = []) {
    return selectedClassIds.length === 0 || selectedClassIds.includes(Number(classId));
}
