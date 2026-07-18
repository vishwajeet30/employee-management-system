import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";

/**
 * Vite configuration for the React frontend.
 *
 * Requests beginning with /api are forwarded to the
 * Spring Boot backend running on port 8080.
 *
 * This avoids CORS problems during local development.
 */
export default defineConfig({
  plugins: [react()],

  server: {
    port: 5173,

    proxy: {
      "/api": {
        target: "http://localhost:8080",
        changeOrigin: true,
      },
    },
  },
});