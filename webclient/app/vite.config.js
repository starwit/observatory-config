import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [react()],
  base: '/smartparkingconfig',
  server: {
    proxy: {
      '/api': 'http://localhost:8081/smartparkingconfig'
    }
  }
})
