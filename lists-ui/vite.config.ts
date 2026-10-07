import react from "@vitejs/plugin-react";
import { readFileSync } from "fs";
import { resolve } from "path";
import { defineConfig, loadEnv, Plugin } from "vite";
import svgr from "vite-plugin-svgr";

/**
 * Dev only. In production a root app serves the auth service worker at the site
 * root; here `base` is /poc/species-lists/, so anything in `public/` would be
 * served below that and could not claim the "/" scope the worker needs (it
 * intercepts /biocache-service/ and /spatial-hub/portal/). Serve it from "/"
 * instead, reading on each request so rebuilds of the branding lib are picked up.
 */
const authServiceWorker = (): Plugin => ({
  name: "serve-auth-service-worker",
  apply: "serve",
  configureServer(server) {
    const file = resolve(
      __dirname,
      "node_modules/@inbo/vbp-branding/dist/service-worker.js",
    );
    server.middlewares.use("/service-worker.js", (_req, res) => {
      res.setHeader("Content-Type", "text/javascript");
      res.setHeader("Service-Worker-Allowed", "/");
      res.setHeader("Cache-Control", "no-cache");
      res.end(readFileSync(file));
    });
  },
});

export default ({ mode }: { mode: string }) => {
  process.env = { ...process.env, ...loadEnv(mode, "./config") };

  // https://vitejs.dev/config/
  return defineConfig({
    base: process.env.VITE_BASE_PATH || "/",
    plugins: [react(), svgr(), authServiceWorker()],
    resolve: {
      alias: {
        "#": "/src",
      },
      dedupe: [
        "react",
        "react-dom",
        "react/jsx-runtime",
        "react-cookie",
        "react-intl",
        "react-oidc-context",
        "oidc-client-ts",
        "@mantine/core",
        "@mantine/hooks",
        "@mantine/modals",
        "@mantine/notifications",
        "@mantine/nprogress",
        "@mantine/form",
        "@mantine/dropzone",
      ],
    },
    optimizeDeps: {
      exclude: ["@inbo/vbp-branding"],
    },
    // server: {
    //   https: {
    //     key: fs.readFileSync('./localhost-key.pem'),
    //     cert: fs.readFileSync('./localhost.pem'),
    //   },
    //   port: 5173
    // },
    server: {
      fs: {
        allow: [
          // Your existing project
          ".",
          // Add your linked library path
          //'/Users/dos009/Documents/Github/ala-mantine'
          resolve(__dirname, "../../vbp-branding"),
        ],
      },
      proxy: {
        "/api": {
          target: "http://localhost:8080", // Backend server
          changeOrigin: true, // Rewrite Host header
        },
        "/bie-index": {
          target: "https://natuurdata.inbo.be",
          changeOrigin: true,
          secure: true,
        },
      },
    },
  });
};
