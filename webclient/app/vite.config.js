import {defineConfig} from "vite";
import react from "@vitejs/plugin-react";
import { createUnplugin } from 'unplugin'

// This is a bugfix. Should be fixed in a future version of vite. See: https://github.com/vitejs/vite/issues/13597
const unplugin = createUnplugin((mode) => {
  return {
    name: 'unplugin-fixenv',
    transform(code) {
      if (code.includes('process.env.NODE_ENV')) {
        return code.replaceAll(/F\.process\.env\.NODE_ENV/g, `"${mode}"`)
      }
      return code
    },
  }
})

export default defineConfig(({command}) => {
  if (command === "serve") {
    return {
      plugins: [
        react(),
        unplugin.vite()
      ],
      base: "/smartparkingconfig/",
      server: {
        proxy: {
          "/smartparkingconfig/api": "http://localhost:8081"
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
