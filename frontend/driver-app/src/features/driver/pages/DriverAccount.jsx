import { useEffect, useState } from "react";

const SESSION_KEY = "swiftride.driver.session";

export default function DriverAccount() {
  const [data, setData] = useState({ user: null, driver: null });

  useEffect(() => {
    try { setData(JSON.parse(window.sessionStorage.getItem(SESSION_KEY) || "{}")); }
    catch { setData({ user: null, driver: null }); }
  }, []);

  const initials = (data.user?.name || "Tài xế").split(" ").filter(Boolean).slice(-1)[0]?.[0]?.toUpperCase() || "T";

  return (
    <div className="profile-layout">
      <section className="panel profile-card">
        <div className="profile-cover"><span>ĐỐI TÁC TÀI XẾ SWIFTRIDE</span></div>
        <div className="profile-overview">
          <div className="profile-avatar">{initials}<span>✓</span></div>
          <div><h2>{data.user?.name || "-"}</h2><p>Tài xế SwiftRide</p></div>
        </div>
        <div className="profile-form">
          <div className="form-grid">
            <label>Họ và tên<input value={data.user?.name || ""} readOnly /></label>
            <label>Số điện thoại<input value={data.user?.phone || ""} readOnly /></label>
          </div>
        </div>
      </section>
      <aside className="profile-side">
        <section className="panel account-panel">
          <div className="section-title"><div><h2>Phương tiện</h2></div></div>
          <div className="vehicle-info">
            <div><span>BẰNG LÁI XE</span><strong>{data.driver?.driverLicense || "-"}</strong></div>
            <div><span>BIỂN SỐ XE</span><strong>{data.driver?.licensePlate || "-"}</strong></div>
          </div>
        </section>
      </aside>
    </div>
  );
}
