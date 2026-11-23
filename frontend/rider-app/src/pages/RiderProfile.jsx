import { useEffect, useState } from "react";

const SESSION_KEY = "swiftride.rider.session";

export default function RiderProfile() {
  const [profile, setProfile] = useState(null);

  useEffect(() => {
    const session = JSON.parse(window.sessionStorage.getItem(SESSION_KEY) || "{}");
    setProfile(session.user || null);
  }, []);

  return (
    <section className="panel rider-profile-panel">
      <div className="rider-profile-avatar">{(profile?.name || "Hành khách").split(" ").slice(-1)[0].slice(0, 1).toUpperCase()}</div>
      <div className="rider-profile-field"><span>Họ và tên</span><strong>{profile?.name || "-"}</strong></div>
      <div className="rider-profile-field"><span>Số điện thoại</span><strong>{profile?.phone || "-"}</strong></div>
      <div className="rider-profile-field"><span>Ngày tham gia</span><strong>{profile?.createdAt ? new Date(profile.createdAt).toLocaleDateString("vi-VN") : "-"}</strong></div>
    </section>
  );
}
