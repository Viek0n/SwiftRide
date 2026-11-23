import { useState } from "react";

const orders = [
  { id: "SR-28491", rider: "Trần Ngọc Anh", from: "Vincom Center Đồng Khởi", to: "Sân bay Tân Sơn Nhất", distance: "8.4 km", duration: "24 phút", fare: "128.000", rating: "4.9", initials: "TA", color: "lavender" },
  { id: "SR-28490", rider: "Lê Hoàng Nam", from: "Phố đi bộ Nguyễn Huệ", to: "Landmark 81, Bình Thạnh", distance: "5.2 km", duration: "18 phút", fare: "86.000", rating: "5.0", initials: "LN", color: "peach" },
  { id: "SR-28489", rider: "Phạm Thùy Linh", from: "Bệnh viện Từ Dũ", to: "Thảo Điền, TP. Thủ Đức", distance: "11.6 km", duration: "32 phút", fare: "164.000", rating: "4.8", initials: "PL", color: "mint" },
];

const trips = [
  { id: "SR-28482", date: "13/11/2026 · 09:12", rider: "Đỗ Minh Châu", route: "Quận 1 → Quận 3", duration: "24 phút", status: "Hoàn thành" },
  { id: "SR-28475", date: "13/11/2026 · 08:34", rider: "Nguyễn Hoàng Long", route: "Bình Thạnh → Quận 1", duration: "31 phút", status: "Hoàn thành" },
  { id: "SR-28461", date: "12/11/2026 · 21:18", rider: "Vũ Thanh Hà", route: "Quận 7 → Quận 4", duration: "19 phút", status: "Hoàn thành" },
  { id: "SR-28453", date: "12/11/2026 · 20:02", rider: "Phan Quốc Bảo", route: "Quận 3 → Phú Nhuận", duration: "16 phút", status: "Hoàn thành" },
];

function SectionTitle({ title, detail, action, onAction }) {
  return <div className="section-title"><div><h2>{title}</h2>{detail && <p>{detail}</p>}</div>{action && <button className="text-button" onClick={onAction}>{action} <span>→</span></button>}</div>;
}

function StatCard({ label, value, suffix, change, icon, tone = "blue" }) {
  return (
    <article className="stat-card">
      <div className={`stat-icon ${tone}`}>{icon}</div>
      <div className="stat-change">{change}</div>
      <p>{label}</p>
      <div className="stat-value">{value}<small>{suffix}</small></div>
    </article>
  );
}

function Avatar({ initials, tone = "lavender" }) {
  return <span className={`avatar avatar-${tone}`}>{initials}</span>;
}

function Dashboard({ navigate }) {
  const bars = [36, 54, 43, 76, 62, 92, 68, 51, 80, 57, 72, 46, 88, 63, 74, 95, 60, 82, 67, 45, 70, 56, 90, 72, 62, 84, 49, 77];
  return (
    <>
      <section className="stats-grid">
        <StatCard label="Thu nhập hôm nay" value="1.245.000" suffix="đ" change="+12,8%" icon="₫" tone="blue" />
        <StatCard label="Chuyến hoàn thành" value="12" suffix=" chuyến" change="+3 chuyến" icon="↗" tone="green" />
        <StatCard label="Thời gian online" value="6,5" suffix=" giờ" change="Hôm nay" icon="◷" tone="purple" />
        <StatCard label="Đánh giá của bạn" value="4,92" suffix="/5" change="Rất tốt" icon="★" tone="orange" />
      </section>

      <div className="dashboard-grid">
        <section className="panel performance-panel">
          <SectionTitle title="Hoạt động chuyến đi" detail="Tóm tắt các chuyến gần đây" action="Lịch sử chuyến" onAction={() => navigate("/rider/history")} />
          <div className="chart-summary"><strong>5.860.000đ</strong><span className="trend-label">↗ 8,2% <small>so với tuần trước</small></span></div>
          <div className="chart">
            <div className="chart-y-labels"><span>1,5tr</span><span>1tr</span><span>500k</span><span>0</span></div>
            <div className="chart-content"><div className="chart-gridlines"><i /><i /><i /><i /></div><div className="chart-bars">{bars.slice(0, 7).map((height, index) => <div className="chart-bar-wrap" key={index}><span className={`chart-bar ${index === 5 ? "highlight" : ""}`} style={{ height: `${height}%` }} /><small>{["T2", "T3", "T4", "T5", "T6", "T7", "CN"][index]}</small></div>)}</div></div>
          </div>
        </section>
        <section className="panel goal-panel">
          <SectionTitle title="Mục tiêu hôm nay" detail="Bạn đang làm rất tốt!" />
          <div className="goal-amount"><strong>12</strong><span>/ 15 chuyến</span></div>
          <div className="progress-track"><span style={{ width: "80%" }} /></div>
          <p className="muted-text">Hoàn thành thêm <strong>3 chuyến</strong> để đạt mục tiêu ngày.</p>
          <div className="goal-reward"><span>✦</span><div><strong>Thưởng năng suất</strong><small>Nhận thêm 150.000đ khi đạt mục tiêu</small></div><b>+150k</b></div>
          <button className="button button-outline button-full" onClick={() => navigate("/rider/orders")}>Tìm chuyến tiếp theo <span>→</span></button>
        </section>
      </div>

      <section className="panel recent-panel">
        <SectionTitle title="Chuyến đi gần đây" detail="Hoạt động mới nhất của bạn" action="Xem tất cả" onAction={() => navigate("/rider/history")} />
        <div className="table-scroll"><table className="data-table"><thead><tr><th>MÃ CHUYẾN</th><th>KHÁCH HÀNG</th><th>LỘ TRÌNH</th><th>THỜI GIAN</th><th>THỜI LƯỢNG</th><th>TRẠNG THÁI</th></tr></thead><tbody>{trips.slice(0, 3).map((trip) => <TripRow key={trip.id} trip={trip} />)}</tbody></table></div>
      </section>
    </>
  );
}

function Status({ isOnline, setIsOnline }) {
  const [schedule, setSchedule] = useState(true);
  return (
    <div className="content-grid status-grid">
      <section className={`panel status-hero ${isOnline ? "status-online" : "status-offline"}`}>
        <div className="status-hero-top"><span className={`status-large-icon ${isOnline ? "" : "is-offline"}`}>{isOnline ? "✓" : "Ⅱ"}</span><span className={`status-label ${isOnline ? "green-label" : "gray-label"}`}><i className={isOnline ? "online-dot" : "offline-dot"} />{isOnline ? "ĐANG HOẠT ĐỘNG" : "ĐANG NGOẠI TUYẾN"}</span></div>
        <h2>{isOnline ? "Bạn đã sẵn sàng nhận chuyến!" : "Bạn đang tạm nghỉ"}</h2>
        <p>{isOnline ? "Các yêu cầu chuyến đi phù hợp sẽ được gửi đến bạn." : "Bật trạng thái hoạt động để bắt đầu nhận yêu cầu chuyến."}</p>
        <button className={`button ${isOnline ? "button-dark" : "button-primary"}`} onClick={() => setIsOnline((value) => !value)}>{isOnline ? "Tạm nghỉ" : "Bắt đầu hoạt động"}</button>
        <div className="status-illustration"><div className="orbit orbit-one" /><div className="orbit orbit-two" /><span>⌖</span></div>
      </section>
      <div className="stacked-panels">
        <section className="panel">
          <SectionTitle title="Thống kê hôm nay" />
          <div className="status-metrics"><div><span className="metric-icon blue">↗</span><p>Thời gian online</p><strong>6 giờ 32 phút</strong></div><div><span className="metric-icon green">▤</span><p>Chuyến hoàn thành</p><strong>12 chuyến</strong></div><div><span className="metric-icon orange">◷</span><p>Thời gian nghỉ</p><strong>1 giờ 08 phút</strong></div></div>
        </section>
        <section className="panel schedule-panel">
          <div className="section-title"><div><h2>Lịch hoạt động</h2><p>Tự động chuyển online theo lịch của bạn</p></div><button className={`switch ${schedule ? "switch-on" : ""}`} aria-label="Bật hoặc tắt lịch hoạt động" onClick={() => setSchedule((value) => !value)}><i /></button></div>
          <div className="schedule-row"><span>Hôm nay, Thứ Sáu</span><strong>08:00 – 20:00</strong><span className="schedule-status">{schedule ? "Đang bật" : "Đang tắt"}</span></div>
          <button className="text-button">Chỉnh sửa lịch <span>→</span></button>
        </section>
      </div>
    </div>
  );
}

function Orders({ navigate }) {
  return (
    <>
      <div className="filter-toolbar"><div className="tab-switch"><button className="selected">Tất cả <span>3</span></button><button>Gần tôi</button><button>Giá tốt</button></div><button className="button button-outline">⌖ <span className="hide-small">Trong bán kính</span> 5 km⌄</button></div>
      <div className="orders-list">{orders.map((order, index) => <article className="panel order-card" key={order.id}>
        <div className="order-top"><div><span className="order-code">YÊU CẦU #{order.id}</span><span className="new-tag">MỚI</span></div><span className="order-time">{index === 0 ? "2 phút trước" : `${index * 3 + 4} phút trước`}</span></div>
        <div className="order-main">
          <div className="route-block"><div className="route-point"><i className="pickup-dot" /><div><small>ĐIỂM ĐÓN</small><strong>{order.from}</strong></div></div><div className="route-connector" /><div className="route-point"><i className="drop-dot" /><div><small>ĐIỂM ĐẾN</small><strong>{order.to}</strong></div></div></div>
          <div className="order-aside"><strong className="fare">{order.fare}<small>đ</small></strong><span className="fare-caption">Thu nhập dự kiến</span></div>
        </div>
        <div className="order-meta"><span>↗ {order.distance}</span><span>◷ {order.duration}</span><span>★ {order.rating} · {order.rider}</span><div className="order-actions"><button className="button button-outline" onClick={() => navigate(`/rider/orders/${order.id}`)}>Chi tiết</button><button className="button button-primary" onClick={() => navigate("/rider/active-trip")}>Nhận chuyến</button></div></div>
      </article>)}</div>
    </>
  );
}

function OrderDetail({ navigate, orderId }) {
  const order = orders.find((item) => item.id === orderId) || orders[0];
  return (
    <div className="detail-layout">
      <section className="panel detail-main">
        <div className="detail-head"><div><span className="order-code">MÃ YÊU CẦU</span><h2>{order.id}</h2></div><span className="new-tag">ĐANG CHỜ TÀI XẾ</span></div>
        <div className="detail-fare"><div><small>GIÁ CHUYẾN DỰ KIẾN</small><strong>{order.fare}<span>đ</span></strong></div><div className="fare-per-km"><span>{order.distance}</span><small>{order.duration} · Xe máy</small></div></div>
        <div className="detail-section"><h3>Thông tin lộ trình</h3><div className="route-block route-detail"><div className="route-point"><i className="pickup-dot" /><div><small>ĐIỂM ĐÓN · 1,2 KM TỪ BẠN</small><strong>{order.from}</strong><span>Địa chỉ đón khách đã được xác nhận</span></div></div><div className="route-connector" /><div className="route-point"><i className="drop-dot" /><div><small>ĐIỂM ĐẾN</small><strong>{order.to}</strong><span>Điểm đến của hành khách</span></div></div></div></div>
        <div className="rider-card"><Avatar initials="NH" /><div><strong>Nguyễn Hoàng Nam</strong><span>★ 4,9 <small>· Tài xế SwiftRide</small></span><small className="driver-contact">090 123 4567 · Honda Vision · 59-X2 123.45</small></div><a className="icon-button" href="tel:0901234567" aria-label="Gọi tài xế">☎</a></div>
        <div className="button-row"><button className="button button-outline" onClick={() => navigate("/rider/map")}>Quay lại bản đồ</button><button className="button button-primary button-grow" onClick={() => navigate("/rider/active-trip")}>Đặt chuyến này <span>→</span></button></div>
      </section>
      <aside className="panel detail-aside"><SectionTitle title="Tóm tắt chuyến" /><div className="summary-map"><div className="map-road road-a" /><div className="map-road road-b" /><div className="map-road road-c" /><div className="map-route" /><span className="map-pin map-pin-a">●</span><span className="map-pin map-pin-b">●</span></div><div className="summary-line"><span>Khoảng cách</span><strong>{order.distance}</strong></div><div className="summary-line"><span>Thời gian dự kiến</span><strong>{order.duration}</strong></div><div className="summary-line"><span>Loại dịch vụ</span><strong>SwiftRide Bike</strong></div><p className="secure-note">♧ Thông tin khách hàng được bảo mật theo chính sách SwiftRide.</p></aside>
    </div>
  );
}

function ActiveTrip({ navigate }) {
  const [finished, setFinished] = useState(false);
  return (
    <div className="detail-layout">
      <section className="panel trip-progress-panel">
        <div className="trip-live-head"><div><span className="live-tag"><i /> {finished ? "ĐÃ HOÀN THÀNH" : "ĐANG DIỄN RA"}</span><h2>Chuyến đi #{finished ? "SR-28491" : "SR-28491"}</h2></div><span className="trip-timer">{finished ? "00:24:18" : "00:18:42"}</span></div>
        <div className="trip-rider"><Avatar initials="NH" /><div><strong>Nguyễn Hoàng Nam</strong><span>★ 4,9 <small>· Tài xế SwiftRide</small></span><small className="driver-contact">090 123 4567 · Honda Vision · 59-X2 123.45</small></div><a className="button button-outline" href="tel:0901234567">☎ Gọi tài xế</a></div>
        <div className="trip-steps">
          <div className="trip-step is-done"><span>✓</span><div><strong>Tài xế đã đón bạn</strong><small>09:42 · Vị trí hiện tại</small></div></div>
          <div className="trip-step is-current"><span>2</span><div><strong>Đang trên đường</strong><small>Còn khoảng 4,2 km · 12 phút</small></div></div>
          <div className={`trip-step ${finished ? "is-done" : ""}`}><span>{finished ? "✓" : "3"}</span><div><strong>Đến điểm trả khách</strong><small>Sân bay Tân Sơn Nhất</small></div></div>
        </div>
        <div className="trip-bottom"><div><small>GIÁ CHUYẾN</small><strong>128.000đ</strong></div>{finished ? <button className="button button-primary" onClick={() => navigate("/rider/history")}>Xem lịch sử</button> : <button className="button button-primary" onClick={() => setFinished(true)}>Xác nhận hoàn thành <span>✓</span></button>}</div>
      </section>
      <aside className="panel trip-route-aside"><SectionTitle title="Lộ trình chuyến đi" action="Mở bản đồ" onAction={() => navigate("/rider/map")} /><div className="summary-map large-map"><div className="map-road road-a" /><div className="map-road road-b" /><div className="map-road road-c" /><div className="map-road road-d" /><div className="map-route" /><span className="map-pin map-pin-a">●</span><span className="map-pin map-pin-b">●</span><span className="map-car">➤</span></div><div className="route-point"><i className="pickup-dot" /><div><small>ĐIỂM ĐÓN</small><strong>Vincom Center Đồng Khởi</strong></div></div><div className="route-point route-destination"><i className="drop-dot" /><div><small>ĐIỂM ĐẾN</small><strong>Sân bay Tân Sơn Nhất</strong></div></div><div className="summary-line"><span>Khoảng cách còn lại</span><strong>4,2 km</strong></div></aside>
    </div>
  );
}

const destinationOptions = [
  "Sân bay Tân Sơn Nhất",
  "Vincom Center Đồng Khởi",
  "Landmark 81",
  "Bến Thành, Quận 1",
  "Bệnh viện Từ Dũ",
];

function MapPage({ navigate }) {
  const [destination, setDestination] = useState("");
  const [destinationQuery, setDestinationQuery] = useState("");
  const [isDestinationListOpen, setIsDestinationListOpen] = useState(false);
  const [rideRequested, setRideRequested] = useState(false);
  const filteredDestinations = destinationOptions.filter((place) =>
    place.toLocaleLowerCase("vi").includes(destinationQuery.toLocaleLowerCase("vi"))
  );
  return (
    <section className="driver-map-fullscreen">
      <div className="full-map driver-map-canvas">
        <div className="map-neighborhood">PHƯỜNG BẾN NGHÉ</div>
        <div className="map-neighborhood second">QUẬN 3</div>
        <div className="map-road map-r1" /><div className="map-road map-r2" /><div className="map-road map-r3" />
        <div className="map-road map-r4" /><div className="map-road map-r5" /><div className="map-road map-r6" />
        <div className="map-park">CÔNG VIÊN<br />TAO ĐÀN</div>
        <div className="map-route" />
        <span className="map-pin map-pin-a">●</span><span className="map-pin map-pin-b">●</span><span className="map-car">➤</span>
        <div className="map-location-chip"><i className="online-dot" /><span>Điểm đón: Vị trí hiện tại</span></div>
        <button className="map-location-button" aria-label="Vị trí của tôi">◎</button>
        <span className="map-attribution">Bản đồ minh họa · TP. Hồ Chí Minh</span>
      </div>
      <div className="map-start-panel">
        <div className="ride-location-fields">
          <label className="ride-location-field">
            <span className="ride-location-marker pickup-marker" />
            <span className="ride-location-copy"><small>ĐIỂM ĐÓN</small><strong>Vị trí hiện tại</strong></span>
            <span className="fixed-location-tag">CỐ ĐỊNH</span>
          </label>
          <div className="ride-location-divider" />
          <div className="destination-picker">
            <label className="ride-location-field" htmlFor="ride-destination">
              <span className="ride-location-marker destination-marker" />
              <span className="ride-location-copy"><small>ĐIỂM ĐẾN</small>
                <input
                  id="ride-destination"
                  value={destination || destinationQuery}
                  placeholder="Chọn địa điểm muốn đến"
                  onFocus={() => setIsDestinationListOpen(true)}
                  onChange={(event) => {
                    setDestination("");
                    setDestinationQuery(event.target.value);
                    setIsDestinationListOpen(true);
                    setRideRequested(false);
                  }}
                  autoComplete="off"
                />
              </span>
            </label>
            {isDestinationListOpen && (
              <div className="destination-options">
                {filteredDestinations.length ? filteredDestinations.map((place) => (
                  <button
                    key={place}
                    onMouseDown={(event) => event.preventDefault()}
                    onClick={() => {
                      setDestination(place);
                      setDestinationQuery("");
                      setIsDestinationListOpen(false);
                      setRideRequested(false);
                    }}
                  >
                    <span>⌖</span>{place}
                  </button>
                )) : <p>Không tìm thấy địa điểm phù hợp.</p>}
              </div>
            )}
          </div>
        </div>
        <button
          className={`map-start-button rider-book-button ${rideRequested ? "is-receiving" : ""}`}
          disabled={!destination}
          aria-pressed={rideRequested}
          onClick={() => setRideRequested(true)}
        >
          {rideRequested ? "ĐANG TÌM TÀI XẾ" : destination ? "ĐẶT CHUYẾN" : "CHỌN ĐIỂM ĐẾN"}
        </button>
        {rideRequested && (
          <button
            className="active-trip-link"
            onClick={() => navigate("/rider/active-trip")}
          >
            Xem chuyến đang thực hiện <span>→</span>
          </button>
        )}
      </div>
    </section>
  );
}

function TripRow({ trip }) {
  return <tr><td><strong className="table-code">{trip.id}</strong></td><td><div className="table-rider"><Avatar initials={trip.rider.split(" ").slice(-1)[0].slice(0, 1)} tone="mint" /><span>{trip.rider}</span></div></td><td>{trip.route}</td><td>{trip.date}</td><td><strong>{trip.duration}</strong></td><td><span className="complete-tag"><i /> {trip.status}</span></td></tr>;
}

function FinanceTabs({ selected, navigate }) {
  return (
    <div className="finance-tabs" role="tablist" aria-label="Thông tin tài chính">
      <button role="tab" aria-selected={selected === "history"} className={selected === "history" ? "selected" : ""} onClick={() => navigate("/rider/history")}>Lịch sử chuyến</button>
      <button role="tab" aria-selected={selected === "earnings"} className={selected === "earnings" ? "selected" : ""} onClick={() => navigate("/rider/earnings")}>Thu nhập</button>
    </div>
  );
}

function History() {
  return <HistoryContent />;
}

function HistoryContent() {
  const [selectedTrip, setSelectedTrip] = useState(null);
  return (
    <>
      <section className="stats-grid history-stats"><StatCard label="Tổng chuyến tháng này" value="186" suffix=" chuyến" change="+14,2%" icon="↗" tone="blue" /><StatCard label="Tổng quãng đường" value="2.840" suffix=" km" change="Tháng 11" icon="⌖" tone="purple" /><StatCard label="Thời lượng trung bình" value="23" suffix=" phút" change="Mỗi chuyến" icon="◷" tone="green" /><StatCard label="Đánh giá trung bình" value="4,92" suffix="/5" change="Rất tốt" icon="★" tone="orange" /></section>
      <section className="panel history-panel"><div className="section-title"><div><h2>Lịch sử chuyến</h2><p>Tháng 11, 2026 · 186 chuyến</p></div><div className="table-tools"><select aria-label="Lọc theo thời gian" defaultValue="month"><option value="month">Tháng này</option><option value="week">Tuần này</option><option value="all">Tất cả</option></select><button className="button button-outline">⇩ Xuất báo cáo</button></div></div><div className="table-scroll"><table className="data-table history-data-table"><thead><tr><th>LỘ TRÌNH</th><th>THỜI GIAN</th><th>THỜI LƯỢNG</th><th>TRẠNG THÁI</th><th>CHI TIẾT</th></tr></thead><tbody>{trips.map((trip) => <tr key={trip.id}><td>{trip.route}</td><td>{trip.date}</td><td><strong>{trip.duration}</strong></td><td><span className="complete-tag"><i /> {trip.status}</span></td><td><button className="table-detail-button" onClick={() => setSelectedTrip(trip)}>Chi tiết</button></td></tr>)}</tbody></table></div><div className="pagination"><span>Hiển thị 1–4 trên 186 chuyến</span><div><button disabled aria-label="Trang trước">‹</button><button className="current-page">1</button><button>2</button><button>3</button><span>…</span><button>47</button><button aria-label="Trang sau">›</button></div></div></section>
      {selectedTrip && <div className="trip-detail-backdrop" onClick={() => setSelectedTrip(null)}>
        <section className="trip-detail-dialog" role="dialog" aria-modal="true" aria-labelledby="trip-detail-title" onClick={(event) => event.stopPropagation()}>
          <div className="trip-detail-heading"><div><small>CHI TIẾT CHUYẾN</small><h2 id="trip-detail-title">{selectedTrip.id}</h2></div><button className="icon-button" aria-label="Đóng chi tiết" onClick={() => setSelectedTrip(null)}>×</button></div>
          <div className="trip-detail-row"><span>Lộ trình</span><strong>{selectedTrip.route}</strong></div>
          <div className="trip-detail-row"><span>Thời lượng chuyến</span><strong>{selectedTrip.duration}</strong></div>
          <div className="trip-detail-row"><span>Thời gian</span><strong>{selectedTrip.date}</strong></div>
          <div className="trip-detail-row"><span>Trạng thái</span><span className="complete-tag"><i /> {selectedTrip.status}</span></div>
          <button className="button button-primary button-full" onClick={() => setSelectedTrip(null)}>Đóng</button>
        </section>
      </div>}
    </>
  );
}

function Earnings({ navigate }) {
  return <><FinanceTabs selected="earnings" navigate={navigate} /><EarningsContent /></>;
}

function EarningsContent() {
  const [period, setPeriod] = useState("Tuần này");
  return (
    <>
      <div className="earnings-toolbar"><div><span className="period-label">KHOẢNG THỜI GIAN</span><select value={period} onChange={(event) => setPeriod(event.target.value)}><option>Tuần này</option><option>Tháng này</option><option>Năm nay</option></select></div><button className="button button-outline">⇩ Tải báo cáo</button></div>
      <section className="stats-grid earnings-stats"><StatCard label="Tổng thu nhập" value={period === "Tuần này" ? "5.860.000" : "18.650.000"} suffix="đ" change="+8,2% so kỳ trước" icon="₫" tone="blue" /><StatCard label="Tiền thưởng" value="850.000" suffix="đ" change="3 phần thưởng" icon="✦" tone="orange" /><StatCard label="Tiền mặt đã thu" value="1.240.000" suffix="đ" change="8 chuyến" icon="◉" tone="purple" /><StatCard label="Chuyển khoản" value="4.620.000" suffix="đ" change="36 chuyến" icon="▤" tone="green" /></section>
      <div className="dashboard-grid earnings-grid"><section className="panel performance-panel"><SectionTitle title="Biểu đồ thu nhập" detail="Thứ Hai, 09/11 – Chủ Nhật, 15/11" /><div className="chart-summary"><strong>5.860.000đ</strong><span className="trend-label">↗ 8,2% <small>so với kỳ trước</small></span></div><div className="chart earnings-chart"><div className="chart-y-labels"><span>1,5tr</span><span>1tr</span><span>500k</span><span>0</span></div><div className="chart-content"><div className="chart-gridlines"><i /><i /><i /><i /></div><div className="chart-bars">{[48, 71, 57, 83, 68, 96, 76].map((height, index) => <div className="chart-bar-wrap" key={index}><span className={`chart-bar ${index === 5 ? "highlight" : ""}`} style={{ height: `${height}%` }} /><small>{["T2", "T3", "T4", "T5", "T6", "T7", "CN"][index]}</small></div>)}</div></div></div></section>
      <section className="panel payout-panel"><SectionTitle title="Thanh toán" detail="Tài khoản nhận tiền" /><div className="bank-card"><span className="bank-chip">▦</span><span>Vietcombank</span><strong>•••• •••• 4821</strong><small>NGUYEN MINH</small></div><div className="payout-row"><div><span>Số dư khả dụng</span><strong>4.620.000đ</strong></div><span className="complete-tag"><i /> Đã đối soát</span></div><button className="button button-primary button-full">Yêu cầu rút tiền <span>→</span></button><p className="payout-note">Tiền sẽ được chuyển trong 1–2 ngày làm việc.</p></section></div>
    </>
  );
}

function Notifications() {
  const initialNotifications = [
    { type: "trip", title: "Chuyến đi hoàn thành", body: "Bạn đã hoàn thành chuyến SR-28482. Thu nhập 72.000đ đã được cộng vào ví.", time: "5 phút trước", unread: true },
    { type: "reward", title: "Bạn vừa nhận thưởng!", body: "Chúc mừng! Bạn nhận được thưởng năng suất 50.000đ cho chuyến đi vừa rồi.", time: "38 phút trước", unread: true },
    { type: "system", title: "Cập nhật chính sách hoạt động", body: "Một số quy định dành cho đối tác tài xế được cập nhật từ ngày 15/11/2026.", time: "Hôm qua", unread: false },
    { type: "trip", title: "Tóm tắt thu nhập trong tuần", body: "Tuần này bạn đã hoàn thành 42 chuyến và nhận 5.860.000đ. Tiếp tục phát huy nhé!", time: "Hôm qua", unread: false },
    { type: "reward", title: "Hồ sơ phương tiện đã xác minh", body: "Giấy tờ xe của bạn đã được xác minh thành công và đang có hiệu lực.", time: "12/11/2026", unread: false },
  ];
  const [items, setItems] = useState(initialNotifications);
  function markAllRead() { setItems((current) => current.map((item) => ({ ...item, unread: false }))); }
  return (
    <section className="panel notifications-panel"><div className="section-title"><div><h2>Cập nhật mới nhất</h2><p>{items.filter((item) => item.unread).length} thông báo chưa đọc</p></div><button className="text-button" onClick={markAllRead}>Đánh dấu đã đọc <span>✓</span></button></div><div className="notification-list">{items.map((item, index) => <article className={`notification-item ${item.unread ? "unread" : ""}`} key={`${item.title}-${index}`}><span className={`notification-icon ${item.type}`}>{item.type === "trip" ? "↗" : item.type === "reward" ? "✦" : "i"}</span><div className="notification-copy"><div><strong>{item.title}</strong>{item.unread && <i className="unread-dot" />}</div><p>{item.body}</p><small>{item.time}</small></div><button className="more-button" aria-label="Tùy chọn thông báo">···</button></article>)}</div><button className="button button-outline button-full load-more">Xem thông báo cũ hơn</button></section>
  );
}

function Profile() {
  return (
    <section className="panel rider-profile-panel">
      <div className="rider-profile-avatar">NM</div>
      <div className="rider-profile-field"><span>Họ và tên</span><strong>Nguyễn Văn Minh</strong></div>
      <div className="rider-profile-field"><span>Số điện thoại</span><strong>090 123 4567</strong></div>
      <div className="rider-profile-field"><span>Ngày tham gia</span><strong>Tháng 03, 2024</strong></div>
    </section>
  );
}

function Vehicle() {
  const [showAll, setShowAll] = useState(false);
  return (
    <div className="vehicle-layout"><section className="panel vehicle-main"><div className="vehicle-banner"><div className="vehicle-banner-text"><span className="vehicle-type">PHƯƠNG TIỆN CHÍNH</span><h2>Honda Vision</h2><p>Phiên bản cao cấp · 2023</p><span className="verified-tag">✓ Đã xác minh</span></div><div className="scooter-illustration"><span>◒</span><i /></div></div><div className="vehicle-info"><div><span>BIỂN SỐ XE</span><strong>59-X2 123.45</strong></div><div><span>MÀU SẮC</span><strong>Đen nhám</strong></div><div><span>LOẠI PHƯƠNG TIỆN</span><strong>Xe máy</strong></div><div><span>NĂM SẢN XUẤT</span><strong>2023</strong></div></div><div className="document-section"><div className="section-title"><div><h2>Giấy tờ phương tiện</h2><p>Đảm bảo giấy tờ luôn còn hiệu lực</p></div><button className="text-button" onClick={() => setShowAll((value) => !value)}>{showAll ? "Thu gọn" : "Xem tất cả"} <span>→</span></button></div><DocumentRow name="Giấy đăng ký xe" detail="Cập nhật 20/03/2024 · Hết hạn 20/03/2028" status="Đã xác minh" /><DocumentRow name="Bảo hiểm trách nhiệm dân sự" detail="Cập nhật 12/06/2026 · Hết hạn 12/06/2027" status="Còn hiệu lực" /><DocumentRow name="Giấy kiểm định phương tiện" detail="Cập nhật 20/03/2024 · Hết hạn 20/03/2027" status="Còn hiệu lực" />{showAll && <DocumentRow name="Ảnh phương tiện" detail="Cập nhật 20/03/2024 · 4 ảnh" status="Đã xác minh" />}</div><button className="button button-outline edit-vehicle">✎ Cập nhật thông tin phương tiện</button></section><aside className="vehicle-side"><section className="panel vehicle-status-panel"><SectionTitle title="Trạng thái phương tiện" /><div className="vehicle-status"><span>✓</span><div><strong>Đang hoạt động</strong><small>Xe của bạn đủ điều kiện nhận chuyến</small></div></div><div className="vehicle-progress"><div><span>Hồ sơ hoàn tất</span><strong>100%</strong></div><div className="progress-track"><span style={{ width: "100%" }} /></div></div></section><section className="vehicle-tip"><span>i</span><div><strong>Lưu ý</strong><p>Hãy cập nhật giấy tờ trước khi hết hạn để tránh gián đoạn hoạt động.</p><button className="text-button">Tìm hiểu thêm →</button></div></section><button className="add-vehicle">＋ &nbsp;Đăng ký phương tiện mới</button></aside></div>
  );
}

function DocumentRow({ name, detail, status }) {
  return <div className="document-row"><span className="document-icon">▤</span><div><strong>{name}</strong><small>{detail}</small></div><span className="complete-tag"><i /> {status}</span><button className="more-button" aria-label={`Tùy chọn ${name}`}>···</button></div>;
}

export {
  Dashboard,
  Status,
  Orders,
  OrderDetail,
  ActiveTrip,
  MapPage,
  History,
  Earnings,
  Notifications,
  Profile,
  Vehicle,
};
