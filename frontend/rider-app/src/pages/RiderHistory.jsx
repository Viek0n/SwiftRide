import { useEffect, useState } from "react";

const API = (process.env.REACT_APP_API_GATEWAY_URL || "http://localhost:8080").replace(/\/+$/, "");
const SESSION_KEY = "swiftride.rider.session";

export default function RiderHistory() {
  const [trips, setTrips] = useState([]);
  const [selected, setSelected] = useState(null);
  const [error, setError] = useState("");

  useEffect(() => {
    let active = true;
    async function load() {
      try {
        const session = JSON.parse(window.sessionStorage.getItem(SESSION_KEY) || "{}");
        const riderId = session.user?.id;
        if (!riderId) throw new Error("Phiên đăng nhập không hợp lệ.");
        const response = await fetch(`${API}/api/trips?riderId=${encodeURIComponent(riderId)}`, {
          headers: session.accessToken ? { Authorization: `Bearer ${session.accessToken}` } : {},
        });
        if (!response.ok) throw new Error(`Không thể tải lịch sử (${response.status})`);
        const data = await response.json();
        if (active) setTrips(Array.isArray(data) ? data : []);
      } catch (requestError) {
        if (active) setError(requestError.message);
      }
    }
    load();
    return () => { active = false; };
  }, []);

  return (
    <section className="panel history-panel">
      <div className="section-title"><div><h2>Lịch sử chuyến</h2><p>Các chuyến đi bạn đã đặt</p></div></div>
      {error && <p className="map-error" role="alert">{error}</p>}
      <div className="table-scroll">
        <table className="data-table history-data-table">
          <thead><tr><th>GIÁ</th><th>TRẠNG THÁI</th><th>NGÀY</th><th>CHI TIẾT</th></tr></thead>
          <tbody>
            {trips.map((trip) => (
              <tr key={trip.id}>
                <td><strong>{trip.price == null ? "-" : `${Number(trip.price).toLocaleString("vi-VN")}đ`}</strong></td>
                <td><span className="complete-tag"><i />{trip.status}</span></td>
                <td>{trip.createdAt ? new Date(trip.createdAt).toLocaleString("vi-VN") : "-"}</td>
                <td><button className="table-detail-button" onClick={() => setSelected(trip)}>Chi tiết</button></td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
      {!trips.length && !error && <p className="muted-text">Chưa có chuyến đi nào.</p>}
      {selected && (
        <div className="trip-detail-backdrop" onClick={() => setSelected(null)}>
          <section className="trip-detail-dialog" role="dialog" aria-modal="true" onClick={(event) => event.stopPropagation()}>
            <div className="trip-detail-heading"><div><small>MÃ CHUYẾN</small><h2>{selected.id}</h2></div><button className="icon-button" onClick={() => setSelected(null)}>×</button></div>
            <div className="trip-detail-row"><span>Giá</span><strong>{selected.price == null ? "-" : `${Number(selected.price).toLocaleString("vi-VN")}đ`}</strong></div>
            <div className="trip-detail-row"><span>Trạng thái</span><strong>{selected.status}</strong></div>
            <div className="trip-detail-row"><span>Ngày</span><strong>{selected.createdAt ? new Date(selected.createdAt).toLocaleString("vi-VN") : "-"}</strong></div>
            <button className="button button-primary button-full" onClick={() => setSelected(null)}>Đóng</button>
          </section>
        </div>
      )}
    </section>
  );
}
