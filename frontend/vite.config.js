/// <reference types="vitest" />
import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    watch: {
      usePolling: true,
      interval: 1000,
    },
    proxy: {
      '/api/ai': {
        target: 'http://localhost:62000',
        changeOrigin: true,
      },
      '/api': {
        target: 'http://localhost:62080',
        changeOrigin: true,
      },
    },
  },
  test: {
    globals: true,
    environment: 'jsdom',
    setupFiles: ['./src/__tests__/setup.js'],
    css: false,
  },
})
