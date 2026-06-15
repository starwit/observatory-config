import convert from 'color-convert';

class ColorFunctions {

    generateDistinctColors(n) {
        const colors = [];
        // Use golden ratio to help spread the hues evenly
        const goldenRatio = 0.618033988749895;
        let hue = 0;

        for (let i = 0; i < n; i++) {
            hue = (hue + goldenRatio) % 1; // Use golden ratio to increment hue
            
            const h = Math.round(hue * 360); // Convert to 0-360 range for hue
            const s = 100; // Saturation at 100%
            const l = 30;  // Lightness at 50% for vibrant colors
            
            // Use color-convert to transform HSL to RGB (0-255 range)
            const rgb = convert.hsl.rgb([h, s, l]);
            
            colors.push(rgb);
        }

        return colors;
    }
}

export default ColorFunctions;