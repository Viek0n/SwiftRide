import { useEffect, useRef, useState } from "react";
import * as maplibregl from "maplibre-gl";
import "maplibre-gl/dist/maplibre-gl.css";

maplibregl.setWorkerUrl(`${process.env.PUBLIC_URL || ""}/maplibre-gl-worker.mjs?v=${maplibregl.getVersion()}`);

const API = (process.env.REACT_APP_API_GATEWAY_URL || "http://localhost:8080").replace(/\/+$/, "");
const SESSION_KEY = "swiftride.driver.session";
const DEFAULT_CENTER = [106.70098, 10.77653];

const rasterStyle = {
  version: 8,
  sources: {
    osm: {
      type: "raster",
      tiles: ["https://tile.openstreetmap.org/{z}/{x}/{y}.png"],
      tileSize: 256,
      attribution: "© OpenStreetMap contributors",
    },
  },
  layers: [{ id: "osm", type: "raster", source: "osm" }],
};

function getSession() {
  try { return JSON.parse(window.sessionStorage.getItem(SESSION_KEY) || "{}"); }
  catch { return {}; }
}

function getDriverId() {
  const session = getSession();
  return session.driver?.id || session.driver?.userId || session.user?.id;
}

async function api(path, options = {}) {
  const session = getSession();
  const response = await fetch(`${API}${path}`, {
    ...options,
    headers: {
      "Content-Type": "application/json",
      ...(session.accessToken ? { Authorization: `Bearer ${session.accessToken}` } : {}),
      ...(options.headers || {}),
    },
  });
  const text = await response.text();
  let body = {};
  if (text) {
    try { body = JSON.parse(text); } catch { body = { message: text }; }
  }
  if (!response.ok) throw new Error(body.message || body.error || `Yêu cầu thất bại (${response.status})`);
  return body;
}

function formatDistance(meters) {
  if (!Number.isFinite(Number(meters))) return "";
  return Number(meters) >= 1000 ? `${(Number(meters) / 1000).toFixed(1)} km` : `${Math.round(Number(meters))} m`;
}

function formatPrice(price) {
  if (price == null) return "Đang tính";
  return `${Number(price).toLocaleString("vi-VN")}đ`;
}

export default function DriverTripMap() {
  const containerRef = useRef(null);
  const mapRef = useRef(null);
  const driverMarkerRef = useRef(null);
  const riderMarkerRef = useRef(null);
  const [driverPosition, setDriverPosition] = useState(null);
  const [phase, setPhase] = useState("idle");
  const [offer, setOffer] = useState(null);
  const [trip, setTrip] = useState(null);
  const [countdown, setCountdown] = useState(0);
  const [error, setError] = useState("");

  useEffect(() => {
    const map = new maplibregl.Map({
      container: containerRef.current,
      style: rasterStyle,
      center: DEFAULT_CENTER,
      zoom: 13,
    });
    map.addControl(new maplibregl.NavigationControl({ showCompass: false }), "top-right");
    mapRef.current = map;
    return () => map.remove();
  }, []);

  useEffect(() => {
    if (!driverPosition || !mapRef.current) return;
    const lngLat = [driverPosition.longitude, driverPosition.latitude];
    if (!driverMarkerRef.current) driverMarkerRef.current = new maplibregl.Marker({ color: "#2563eb" }).setLngLat(lngLat).addTo(mapRef.current);
    else driverMarkerRef.current.setLngLat(lngLat);
  }, [driverPosition]);

  useEffect(() => {
    if (phase !== "matched" && phase !== "pickup" && phase !== "inprogress") return;
    if (!trip?.from || !mapRef.current) return;
    const point = trip.status === "INPROGRESS" ? trip.to : trip.from;
    if (!point) return;
    if (!riderMarkerRef.current) riderMarkerRef.current = new maplibregl.Marker({ color: "#ef4444" }).setLngLat([point.longitude, point.latitude]).addTo(mapRef.current);
    else riderMarkerRef.current.setLngLat([point.longitude, point.latitude]);
  }, [trip, phase]);

  useEffect(() => {
    if (phase !== "offer" || !offer?.expiresAt) return;
    const update = () => {
      const remaining = Math.max(0, Math.ceil((new Date(offer.expiresAt).getTime() - Date.now()) / 1000));
      setCountdown(remaining);
      if (remaining === 0) setPhase("idle");
    };
    update();
    const timer = window.setInterval(update, 1000);
    return () => window.clearInterval(timer);
  }, [phase, offer]);

  useEffect(() => {
    if (!["matching", "offer", "matched", "pickup", "inprogress", "payment"].includes(phase)) return;
    const timer = window.setInterval(async () => {
      const driverId = getDriverId();
      if (!driverId) return;
      try {
        if (phase === "matching") {
          const offers = await api("/trips/me/offers", { headers: { "X-Driver-Id": driverId } });
          if (Array.isArray(offers) && offers.length) {
            setOffer(offers[0]);
            setPhase("offer");
          }
        }
        if (phase === "matched" || phase === "pickup" || phase === "inprogress" || phase === "payment") {
          if (!trip?.id) return;
          const current = await api(`/trips/${trip.id}`);
          setTrip(current);
          if (current.status === "PICKUP") setPhase("pickup");
          if (current.status === "INPROGRESS") setPhase("inprogress");
          if (current.status === "COMPLETE") setPhase("payment");
          if (current.status === "PAID") setPhase("idle");
        }
      } catch (requestError) {
        setError(requestError.message);
      }
    }, 2000);
    return () => window.clearInterval(timer);
  }, [phase, trip?.id]);

  function updateLocation(position) {
    setDriverPosition(position);
    mapRef.current?.flyTo({ center: [position.longitude, position.latitude], zoom: 15 });
  }

  async function startMatching() {
    setError("");
    const driverId = getDriverId();
    if (!driverId) {
      setError("Phiên tài xế không hợp lệ.");
      return;
    }
    if (!navigator.geolocation) {
      setError("Trình duyệt không hỗ trợ định vị.");
      return;
    }
    navigator.geolocation.getCurrentPosition(async ({ coords }) => {
      const position = { latitude: coords.latitude, longitude: coords.longitude };
      updateLocation(position);
      try {
        await api(`/active-drivers/${encodeURIComponent(driverId)}`, {
          method: "PUT",
          body: JSON.stringify(position),
        });
        setPhase("matching");
      } catch (requestError) {
        setError(requestError.message);
      }
    }, () => setError("Không thể truy cập vị trí hiện tại."), {
      enableHighAccuracy: true,
      timeout: 10000,
    });
  }

  useEffect(() => {
    if (phase !== "matching") return;
    if (!navigator.geolocation) return;
    const watcher = navigator.geolocation.watchPosition(async ({ coords }) => {
      const position = { latitude: coords.latitude, longitude: coords.longitude };
      setDriverPosition(position);
      const driverId = getDriverId();
      if (driverId) {
        try {
          await api(`/active-drivers/${encodeURIComponent(driverId)}`, {
            method: "PUT",
            body: JSON.stringify(position),
          });
        } catch (requestError) {
          setError(requestError.message);
        }
      }
    });
    return () => navigator.geolocation.clearWatch(watcher);
  }, [phase]);

  async function rejectOffer() {
    if (!offer) return;
    const driverId = getDriverId();
    try {
      await api(`trips/${offer.tripId}/driver-reject?driverId=${encodeURIComponent(driverId)}`, { method: "POST" });
    } catch (requestError) {
      setError(requestError.message);
    }
    setOffer(null);
    setPhase("idle");
  }

  async function confirmOffer() {
    if (!offer) return;
    setError("");
    const driverId = getDriverId();
    try {
      await api(`/trips/${offer.tripId}/driver-accept?driverId=${encodeURIComponent(driverId)}`, {
        method: "PATCH",
        body: JSON.stringify({ driverId }),
      });
      const current = await api(`/trips/${offer.tripId}`);
      setTrip(current);
      setOffer(null);
      setPhase("pickup");
    } catch (requestError) {
      setError(requestError.message);
    }
  }

  async function pickedUp() {
    if (!trip?.id) return;
    const driverId = getDriverId();
    try {
      const current = await api(`/trips/${trip.id}/pickedup`, { method: "POST", body: JSON.stringify({ driverId }) });
      setTrip(current);
      setPhase("inprogress");
    } catch (requestError) {
      setError(requestError.message);
    }
  }

  async function completeTrip() {
    if (!trip?.id) return;
    const driverId = getDriverId();
    try {
      const current = await api(`/trips/${trip.id}/complete`, { method: "POST", body: JSON.stringify({ driverId }) });
      setTrip(current);
      setPhase("payment");
    } catch (requestError) {
      setError(requestError.message);
    }
  }

  return (
    <section className="driver-map-fullscreen">
      <div ref={containerRef} className="full-map maplibre-container" />
      <button className="map-location-button" aria-label="Vị trí của tôi" onClick={() => {
        navigator.geolocation?.getCurrentPosition(({ coords }) => updateLocation({ latitude: coords.latitude, longitude: coords.longitude }));
      }}>◎</button>
      {error && <div className="map-error map-error-floating" role="alert">{error}</div>}
      {phase === "idle" && (
        <div className="map-start-panel">
          <div><strong>Sẵn sàng nhận chuyến?</strong><small>Bật để tìm chuyến gần bạn</small></div>
          <button className="map-start-button" onClick={startMatching}>START</button>
        </div>
      )}
      {phase === "matching" && (
        <div className="map-start-panel">
          <div><strong>Đang tìm chuyến</strong><small>Vị trí của bạn đang được cập nhật</small></div>
          <button className="map-start-button is-receiving" onClick={() => setPhase("idle")}>DỪNG</button>
        </div>
      )}
      {phase === "offer" && offer && (
        <div className="map-action-card offer-card">
          <div className="offer-head"><strong>Chuyến mới</strong><span>{countdown}s</span></div>
          <p>Chuyến: {offer.tripId}</p>
          <div className="button-row">
            <button className="button button-outline" onClick={rejectOffer}>Từ chối</button>
            <button className="button button-primary" onClick={confirmOffer}>Xác nhận</button>
          </div>
        </div>
      )}
      {(phase === "pickup" || phase === "inprogress") && trip && (
        <div className="map-action-card trip-card">
          <strong>{phase === "pickup" ? "Đang đến điểm đón" : "Chuyến đang thực hiện"}</strong>
          <p>Chuyến {trip.id}</p>
          <div className="trip-summary-line"><span>{formatDistance(trip.distance)}</span><strong>{formatPrice(trip.price)}</strong></div>
          {phase === "pickup" ? (
            <button className="button button-primary button-full" onClick={pickedUp}>ĐÃ ĐÓN KHÁCH</button>
          ) : (
            <button className="button button-primary button-full" onClick={completeTrip}>HOÀN THÀNH</button>
          )}
        </div>
      )}
      {phase === "payment" && trip && (
        <div className="map-action-card">
          <strong>Chờ thanh toán</strong>
          <p>Chuyến {trip.id} đang chờ hành khách thanh toán.</p>
          <button className="button button-outline button-full" onClick={() => setPhase("idle")}>Quay lại trang chủ</button>
        </div>
      )}
    </section>
  );
}
