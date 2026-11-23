const fs = require("fs");
const path = require("path");

const source = path.resolve(__dirname, "../node_modules/maplibre-gl/dist/maplibre-gl-worker.mjs");
const destination = path.resolve(__dirname, "../public/maplibre-gl-worker.mjs");

if (!fs.existsSync(source)) {
  throw new Error(`MapLibre worker not found: ${source}. Install frontend dependencies first.`);
}

fs.copyFileSync(source, destination);
console.log("Prepared MapLibre worker in public/");
