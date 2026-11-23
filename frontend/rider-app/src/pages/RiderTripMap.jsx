import { useEffect, useRef, useState } from "react";
import * as maplibregl from "maplibre-gl";
import "maplibre-gl/dist/maplibre-gl.css";

maplibregl.setWorkerUrl(`${process.env.PUBLIC_URL || ""}/maplibre-gl-worker.mjs?v=${maplibregl.getVersion()}`);

const API = (process.env.REACT_APP_API_GATEWAY_URL || "http://localhost:8080").replace(/\/+$/, "");
const SESSION_KEY = "swiftride.rider.session";
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

async function geocode(query) {
  const url = `https://nominatim.openstreetmap.org/search?format=jsonv2&limit=5&countrycodes=vn&q=${encodeURIComponent(query)}`;
  const response = await fetch(url, { headers: { "Accept-Language": "vi" } });
  if (!response.ok) return [];
  return response.json();
}

async function reverseGeocode(lng, lat) {
  const response = await fetch(`https://nominatim.openstreetmap.org/reverse?format=jsonv2&lat=${lat}&lon=${lng}`, {
    headers: { "Accept-Language": "vi" },
  });
  if (!response.ok) return `${lat.toFixed(5)}, ${lng.toFixed(5)}`;
  const data = await response.json();
  return data.display_name || `${lat.toFixed(5)}, ${lng.toFixed(5)}`;
}

function formatTripDistance(distanceMeters) {
  if (distanceMeters == null) return "Chưa có dữ liệu";
  const distance = Number(distanceMeters);
  if (!Number.isFinite(distance)) return "Chưa có dữ liệu";
  return distance >= 1000 ? `${(distance / 1000).toFixed(1)} km` : `${Math.round(distance)} m`;
}

function formatTripFare(fare) {
  if (fare == null) return "Chưa có dữ liệu";
  const amount = Number(fare);
  return Number.isFinite(amount) ? `${amount.toLocaleString("vi-VN")}đ` : "Chưa có dữ liệu";
}

function drawRoute(map, coordinates) {
  if (!map.isStyleLoaded() || !Array.isArray(coordinates) || coordinates.length < 2) return false;
  const validCoordinates = coordinates.filter((point) =>
    Array.isArray(point) && point.length >= 2 && point.every(Number.isFinite)
  );
  if (validCoordinates.length < 2) return false;

  const feature = {
    type: "Feature",
    properties: {},
    geometry: { type: "LineString", coordinates: validCoordinates },
  };
  if (map.getSource("trip-route")) {
    map.getSource("trip-route").setData(feature);
  } else {
    map.addSource("trip-route", { type: "geojson", data: feature });
  }
  if (!map.getLayer("trip-route")) {
    map.addLayer({
      id: "trip-route",
      type: "line",
      source: "trip-route",
      paint: { "line-color": "#2563eb", "line-width": 5 },
      layout: { "line-cap": "round", "line-join": "round" },
    });
  }

  const bounds = new maplibregl.LngLatBounds();
  validCoordinates.forEach((coordinate) => bounds.extend(coordinate));
  map.fitBounds(bounds, { padding: 100, duration: 500 });
  return true;
}

export default function RiderTripMap({ navigate }) {
  const containerRef = useRef(null);
  const mapRef = useRef(null);
  const pickupMarkerRef = useRef(null);
  const destinationMarkerRef = useRef(null);
  const [pickup, setPickup] = useState(null);
  const [destination, setDestination] = useState(null);
  const [mapLoaded, setMapLoaded] = useState(false);
  const [pickupText, setPickupText] = useState("Vị trí hiện tại");
  const [isLocating, setIsLocating] = useState(false);
  const [destinationText, setDestinationText] = useState("");
  const [searchResults, setSearchResults] = useState([]);
  const [phase, setPhase] = useState("form");
  const [trip, setTrip] = useState(null);
  const [routeCoordinates, setRouteCoordinates] = useState(null);
  const routeCoordinatesRef = useRef(null);
  const [error, setError] = useState("");
  const [paymentUrl, setPaymentUrl] = useState("");
  const [isCreatingTrip, setIsCreatingTrip] = useState(false);
  const [isConfirmingTrip, setIsConfirmingTrip] = useState(false);
  const [isRejectingTrip, setIsRejectingTrip] = useState(false);

  useEffect(() => {
    if (!containerRef.current) return;
    const map = new maplibregl.Map({
      container: containerRef.current,
      style: rasterStyle,
      center: DEFAULT_CENTER,
      zoom: 13,
    });
    map.addControl(new maplibregl.NavigationControl({ showCompass: false }), "top-right");
    mapRef.current = map;
    map.once("load", () => setMapLoaded(true));
    map.on("click", async (event) => {
      const point = [event.lngLat.lng, event.lngLat.lat];
      setDestination({ latitude: event.lngLat.lat, longitude: event.lngLat.lng });
      setDestinationText(await reverseGeocode(point[0], point[1]));
    });
    return () => map.remove();
  }, []);

  useEffect(() => {
    if (!mapRef.current) return;
    if (pickup) {
      if (!pickupMarkerRef.current) pickupMarkerRef.current = new maplibregl.Marker({ color: "#2563eb" }).setLngLat([pickup.longitude, pickup.latitude]).addTo(mapRef.current);
      else pickupMarkerRef.current.setLngLat([pickup.longitude, pickup.latitude]);
    }
    if (destination) {
      if (!destinationMarkerRef.current) destinationMarkerRef.current = new maplibregl.Marker({ color: "#ef4444" }).setLngLat([destination.longitude, destination.latitude]).addTo(mapRef.current);
      else destinationMarkerRef.current.setLngLat([destination.longitude, destination.latitude]);
    }
  }, [pickup, destination]);

  useEffect(() => {
    if (!pickup || !destination || !mapRef.current) return;
    let cancelled = false;
    const straightLine = [[pickup.longitude, pickup.latitude], [destination.longitude, destination.latitude]];
    (async () => {
      let coordinates = straightLine;
      try {
        const route = await api("/matching/route", {
          method: "POST",
          body: JSON.stringify({ origin: pickup, destination }),
        });
        if (route?.geometry?.coordinates?.length > 1) coordinates = route.geometry.coordinates;
      } catch {}
      if (!cancelled) setRouteCoordinates(coordinates);
    })();
    return () => { cancelled = true; };
  }, [pickup, destination]);

  useEffect(() => {
    const map = mapRef.current;
    routeCoordinatesRef.current = routeCoordinates;
    if (!map || !mapLoaded || !routeCoordinates?.length || routeCoordinates.length < 2) return undefined;
    let cancelled = false;
    const renderRoute = () => {
      if (cancelled) return;
      if (!drawRoute(map, routeCoordinatesRef.current)) {
        map.once("idle", renderRoute);
      }
    };
    renderRoute();
    return () => {
      cancelled = true;
      map.off("load", renderRoute);
      map.off("idle", renderRoute);
    };
  }, [mapLoaded, routeCoordinates]);

  useEffect(() => {
    if (phase !== "waiting" && phase !== "assigned" && phase !== "inprogress") return;
    const tripId = trip?.id;
    if (!tripId) return;
    const timer = window.setInterval(async () => {
      try {
        const current = await api(`/trips/${tripId}`);
        setTrip(current);
        if (["MATCHINGFAIL", "CANCELLED"].includes(current.status)) setPhase("failed");
        if (current.status === "PICKUP") setPhase("assigned");
        if (current.status === "INPROGRESS") setPhase("inprogress");
        if (current.status === "COMPLETE") setPhase("payment");
        if (current.status === "PAID") setPhase("paid");
      } catch (requestError) {
        setError(requestError.message);
      }
    }, 2000);
    return () => window.clearInterval(timer);
  }, [phase, trip?.id]);

  async function locateSelf() {
    setError("");
    if (!navigator.geolocation) {
      setError("Trình duyệt không hỗ trợ định vị.");
      return;
    }
    setIsLocating(true);
    navigator.geolocation.getCurrentPosition(async ({ coords }) => {
      const point = { latitude: coords.latitude, longitude: coords.longitude };
      setPickup(point);
      setPickupText(`${point.latitude.toFixed(5)}, ${point.longitude.toFixed(5)}`);
      mapRef.current?.flyTo({ center: [point.longitude, point.latitude], zoom: 16 });
      try {
        setPickupText(await reverseGeocode(point.longitude, point.latitude));
      } catch {
        // Keep the coordinates visible if address lookup is unavailable.
      } finally {
        setIsLocating(false);
      }
    }, () => {
      setError("Không thể truy cập vị trí. Hãy cho phép trình duyệt sử dụng vị trí của bạn.");
      setIsLocating(false);
    }, { enableHighAccuracy: true, timeout: 10000 });
  }

  async function searchDestination(value) {
    setDestinationText(value);
    setDestination(null);
    if (value.trim().length < 3) {
      setSearchResults([]);
      return;
    }
    setSearchResults(await geocode(value));
  }

  async function createTrip() {
    if (!pickup || !destination) {
      setError("Hãy chọn điểm đón và điểm đến.");
      return;
    }
    setError("");
    setIsCreatingTrip(true);
    try {
      const session = getSession();
      const riderId = session.user?.id;
      if (!riderId) throw new Error("Phiên đăng nhập không hợp lệ.");
      const requestedTrip = await api("/trips", {
        method: "POST",
        body: JSON.stringify({ riderId, from: pickup, to: destination }),
      });
      const persistedRoute = requestedTrip.route?.geometry?.coordinates;
      if (Array.isArray(persistedRoute) && persistedRoute.length > 1) {
        setRouteCoordinates(persistedRoute);
      } else {
        setRouteCoordinates([[pickup.longitude, pickup.latitude], [destination.longitude, destination.latitude]]);
      }
      setTrip(requestedTrip);
      setPhase("confirm");
    } catch (requestError) {
      setError(requestError.message);
    } finally {
      setIsCreatingTrip(false);
    }
  }

  async function confirmTrip() {
    if (!trip?.id) return;
    setError("");
    setIsConfirmingTrip(true);
    try {
      const matchingTrip = await api(`/trips/${trip.id}/confirm`, { method: "PATCH" });
      setTrip(matchingTrip);
      await api("/matching/match", {
        method: "POST",
        body: JSON.stringify({ tripId: trip.id, origin: pickup }),
      });
      setPhase("waiting");
    } catch (requestError) {
      setError(requestError.message);
    } finally {
      setIsConfirmingTrip(false);
    }
  }

  async function rejectRequestedTrip() {
    if (!trip?.id) return;
    setError("");
    setIsRejectingTrip(true);
    try {
      await api(`/trips/${trip.id}/driver-reject`, { method: "POST" });
      setTrip(null);
      setPhase("form");
    } catch (requestError) {
      setError(requestError.message);
    } finally {
      setIsRejectingTrip(false);
    }
  }

  async function cancelTrip() {
    if (!trip?.id) return;
    setError("");
    try {
      await api(`/trips/${trip.id}/cancel`, { method: "PATCH" });
      setTrip(null);
      setPhase("form");
    } catch (requestError) {
      setError(requestError.message);
    }
  }

  async function openPayment() {
    try {
      const session = getSession();
      const result = await api(`/payments/${trip.id}/checkout`, {
        method: "POST",
        headers: {
          "X-Rider-Id": session.user?.id || "",
          "Idempotency-Key": `${trip.id}-${Date.now()}`,
        },
      });
      const url = result.paymentUrl || result.payUrl || result.url;
      setPaymentUrl(url || "");
      if (url) window.open(url, "_blank", "noopener,noreferrer");
    } catch (requestError) {
      setError(requestError.message);
    }
  }

  return (
    <section className="driver-map-fullscreen rider-trip-map">
      <div ref={containerRef} className="full-map maplibre-container" />
      <button className="map-location-button" aria-label="Vị trí của tôi" onClick={locateSelf}>◎</button>
      {phase === "form" && (
        <div className="map-start-panel rider-location-panel">
          <div className="ride-location-fields">
            <button
              type="button"
              className="ride-location-field pickup-location-button"
              onClick={locateSelf}
              disabled={isLocating}
              aria-label="Xác định vị trí hiện tại của tôi"
              aria-busy={isLocating}
            >
              <span className="ride-location-marker pickup-marker" />
              <span className="ride-location-copy"><small>ĐIỂM ĐÓN · NHẤN ĐỂ ĐỊNH VỊ</small><strong>{isLocating ? "Đang xác định vị trí..." : pickupText}</strong></span>
              <span className="locate-icon" aria-hidden="true">◎</span>
            </button>
            <div className="ride-location-divider" />
            <div className="destination-picker">
              <label className="ride-location-field" htmlFor="destination-address">
                <span className="ride-location-marker destination-marker" />
                <span className="ride-location-copy"><small>ĐIỂM ĐẾN</small>
                  <input id="destination-address" value={destinationText} placeholder="Nhập địa chỉ hoặc nhấp bản đồ" onChange={(event) => searchDestination(event.target.value)} />
                </span>
              </label>
              {searchResults.length > 0 && (
                <div className="destination-options">
                  {searchResults.map((result) => (
                    <button key={result.place_id} onClick={() => {
                      const point = { latitude: Number(result.lat), longitude: Number(result.lon) };
                      setDestination(point);
                      setDestinationText(result.display_name);
                      setSearchResults([]);
                      mapRef.current?.flyTo({ center: [point.longitude, point.latitude], zoom: 16 });
                    }}>{result.display_name}</button>
                  ))}
                </div>
              )}
            </div>
          </div>
          {error && <p className="map-error" role="alert">{error}</p>}
          <button className="map-start-button rider-book-button" disabled={!pickup || !destination || isCreatingTrip} onClick={createTrip}>
            {isCreatingTrip ? "ĐANG TẢI CHUYẾN..." : "HIỂN THỊ CHUYẾN"}
          </button>
        </div>
      )}
      {phase === "confirm" && (
        <div className="map-action-card">
          <strong>Xác nhận chuyến đi</strong>
          <p>{pickupText} → {destinationText}</p>
          <div className="trip-quote-details">
            <div><span>Khoảng cách</span><strong>{formatTripDistance(trip?.distance)}</strong></div>
            <div><span>Giá chuyến</span><strong>{formatTripFare(trip?.price)}</strong></div>
          </div>
          {error && <p className="map-error" role="alert">{error}</p>}
          <div className="button-row">
            <button className="button button-outline" onClick={rejectRequestedTrip} disabled={isRejectingTrip || isConfirmingTrip}>
              {isRejectingTrip ? "Đang từ chối..." : "Từ chối"}
            </button>
            <button className="button button-primary" onClick={confirmTrip} disabled={isRejectingTrip || isConfirmingTrip}>
              {isConfirmingTrip ? "Đang xác nhận..." : "Xác nhận"}
            </button>
          </div>
        </div>
      )}
      {(phase === "waiting" || phase === "assigned" || phase === "inprogress" || phase === "payment" || phase === "failed" || phase === "paid") && (
        <div className="map-action-card">
          {phase === "waiting" && <><strong>Đang tìm tài xế</strong><p>Chuyến {trip?.id} đang được ghép...</p><button className="button button-outline" onClick={cancelTrip}>Hủy chuyến</button></>}
          {phase === "failed" && <><strong>Không tìm được tài xế</strong><p>Chuyến đi thất bại hoặc hết hạn.</p><button className="button button-primary" onClick={() => { setTrip(null); setPhase("form"); }}>Quay lại bản đồ</button></>}
          {phase === "assigned" && <><strong>Tài xế đang đến đón</strong><p>Chuyến {trip?.id} · Trạng thái {trip?.status}</p></>}
          {phase === "inprogress" && <><strong>Chuyến đi đang diễn ra</strong><p>Đang di chuyển đến điểm đến.</p></>}
          {phase === "payment" && <><strong>Chuyến đi đã hoàn thành</strong><p>Thanh toán để hoàn tất chuyến.</p><button className="button button-primary" onClick={openPayment}>{paymentUrl ? "Mở lại thanh toán" : "Thanh toán"}</button></>}
          {phase === "paid" && <><strong>Thanh toán thành công</strong><p>Cảm ơn bạn đã sử dụng SwiftRide.</p><button className="button button-primary" onClick={() => { setTrip(null); setPhase("form"); }}>Đặt chuyến mới</button></>}
        </div>
      )}
    </section>
  );
}
