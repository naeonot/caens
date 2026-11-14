import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

export default defineConfig({
  root: '..',
  base: '/caens/',
  plugins: [react()],
  publicDir: 'web/public',
  build: {
    outDir: 'web/dist',
    emptyOutDir: true
  }
});
