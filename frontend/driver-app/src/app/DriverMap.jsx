import { useState } from "react";

export default function DriverMap({ position }) {
  const [reloadCount, setReloadCount] = useState(0);

  if (!position) {
    return null;
  }

  const { latitude, longitude } = position;

  const bounds = [
    longitude - 0.01,
    latitude - 0.01,
    longitude + 0.01,
    latitude + 0.01,
  ].join(",");

  const params = new URLSearchParams({
    bbox: bounds,
    layer: "mapnik",
    marker: `${latitude},${longitude}`,
  });

  const mapUrl =
    `https://www.openstreetmap.org/export/embed.html?${params}`;

  const fullMapUrl =
    `https://www.openstreetmap.org/?mlat=${latitude}` +
    `&mlon=${longitude}#map=16/${latitude}/${longitude}`;

  return (
    <div className="driver-map">
      <iframe
        key={`${latitude}-${longitude}-${reloadCount}`}
        title="Bản đồ vị trí"
        src={mapUrl}
        className="map-frame"
      />

      <button
        type="button"
        className="secondary-button"
        onClick={() => setReloadCount((current) => current + 1)}
      >
        Tải lại bản đồ
      </button>

      <a
        href={fullMapUrl}
        target="_blank"
        rel="noopener noreferrer"
        className="map-link"
      >
        Mở bản đồ lớn
      </a>
    </div>
  );
}