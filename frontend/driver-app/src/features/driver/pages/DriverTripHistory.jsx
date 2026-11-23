import { useState } from "react";

const demoTrips = [
  { id: "SR-28482", date: "13/11/2026 · 09:12", route: "Quận 1 → Quận 3", price: 72000, status: "Hoàn thành" },
  { id: "SR-28475", date: "13/11/2026 · 08:34", route: "Bình Thạnh → Quận 1", price: 95000, status: "Hoàn thành" },
  { id: "SR-28461", date: "12/11/2026 · 21:18", route: "Quận 7 → Quận 4", price: 68000, status: "Hoàn thành" },
];

export default function DriverTripHistory() {
  const [selected, setSelected] = useState(null);
  return (
    <section className="panel history-panel">
      <div className="section-title"><div><h2>Lịch sử chuyến</h2><p>Các chuyến bạn đã nhận</p></div></div>
      <div className="table-scroll">
        <table className="data-table history-data-table">
          <thead><tr><th>GIÁ</th><th>TRẠNG THÁI</th><th>NGÀY</th><th>CHI TIẾT</th></tr></thead>
          <tbody>{demoTrips.map((trip) => (
            <tr key={trip.id}>
              <td><strong>{trip.price.toLocaleString("vi-VN")}đ</strong></td>
              <td><span className="complete-tag"><i />{trip.status}</span></td>
              <td>{trip.date}</td>
              <td><button className="table-detail-button" onClick={() => setSelected(trip)}>Chi tiết</button></td>
            </tr>
          ))}</tbody>
        </table>
      </div>
      {selected && (
        <div className="trip-detail-backdrop" onClick={() => setSelected(null)}>
          <section className="trip-detail-dialog" role="dialog" aria-modal="true" onClick={(event) => event.stopPropagation()}>
            <div className="trip-detail-heading"><div><small>MÃ CHUYẾN</small><h2>{selected.id}</h2></div><button className="icon-button" onClick={() => setSelected(null)}>×</button></div>
            <div className="trip-detail-row"><span>Giá</span><strong>{selected.price.toLocaleString("vi-VN")}đ</strong></div>
            <div className="trip-detail-row"><span>Trạng thái</span><strong>{selected.status}</strong></div>
            <div className="trip-detail-row"><span>Ngày</span><strong>{selected.date}</strong></div>
            <div className="trip-detail-row"><span>Lộ trình</span><strong>{selected.route}</strong></div>
            <button className="button button-primary button-full" onClick={() => setSelected(null)}>Đóng</button>
          </section>
        </div>
      )}
    </section>
  );
}
