/*
import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
})*/

import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";
import dotenv from "dotenv";

dotenv.config({ path: `.env.${process.env.NODE_ENV}` });

export default defineConfig(({ mode }) => {
  const isDev = mode === "development";

  console.log("Loaded backend port:", process.env.VITE_BACKEND_PORT);
  console.log("Loaded backend URL:", process.env.VITE_BACKEND_URL);
  console.log("Loaded frontend port:", process.env.VITE_FRONTEND_PORT);

  const backendTarget = isDev
    ? `http://localhost:${process.env.VITE_BACKEND_PORT}`
    : process.env.VITE_BACKEND_URL;

  return {
    plugins: [react()],
    server: {
      port: Number(process.env.VITE_FRONTEND_PORT),
      proxy: {
        "/api": {
          target: backendTarget,
          changeOrigin: true
        }
      }
    }
  };
});