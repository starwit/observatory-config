import {defineConfig} from "vite";
import react from "@vitejs/plugin-react";

export default defineConfig(({command}) => {
  if (command === "serve") {
    return {
      plugins: [
        react(),
      ],
      base: "/observatory-config/",
      server: {
        proxy: {
          "/observatory-config/api": "http://localhost:8081",
          // SAE visualizer live-tracking STOMP websocket
          "/observatory-config/location-websocket": {
            target: "http://localhost:8081",
            ws: true
          }
        }
      },
    };
  } else {
    return {
      plugins: [react()],
      base: "./"
    };
  }
});
