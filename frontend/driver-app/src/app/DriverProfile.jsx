import { useState } from "react";

const PROFILE_KEY = "swiftride-driver-profile";

const defaultProfile = {
  fullName: "Nguyễn Văn An",
  phone: "",
  vehicle: "Honda Wave",
  licensePlate: "",
};

function loadProfile() {
  try {
    const saved = JSON.parse(localStorage.getItem(PROFILE_KEY));

    if (!saved || typeof saved !== "object") {
      return { ...defaultProfile };
    }

    return {
      fullName:
        typeof saved.fullName === "string"
          ? saved.fullName
          : defaultProfile.fullName,
      phone: typeof saved.phone === "string" ? saved.phone : "",
      vehicle:
        typeof saved.vehicle === "string"
          ? saved.vehicle
          : defaultProfile.vehicle,
      licensePlate:
        typeof saved.licensePlate === "string"
          ? saved.licensePlate
          : "",
    };
  } catch {
    return { ...defaultProfile };
  }
}

export default function DriverProfile() {
  const [profile, setProfile] = useState(loadProfile);
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");

  function handleChange(event) {
    const { name, value } = event.target;

    setProfile((current) => ({
      ...current,
      [name]: value,
    }));

    setMessage("");
    setError("");
  }

  function handleSave(event) {
    event.preventDefault();

    const cleanedProfile = {
      fullName: profile.fullName.trim(),
      phone: profile.phone.trim(),
      vehicle: profile.vehicle.trim(),
      licensePlate: profile.licensePlate.trim().toUpperCase(),
    };

    if (!/^0\d{9}$/.test(cleanedProfile.phone)) {
  setError("Số điện thoại phải có 10 chữ số và bắt đầu bằng số 0.");
  setMessage("");
  return;
}
const normalizedPlate = cleanedProfile.licensePlate.replace(
  /[\s.-]/g,
  ""
);

if (!/^\d{2}[A-Z][A-Z0-9]\d{4,5}$/.test(normalizedPlate)) {
  setError(
    "Nhập biển số xe máy theo dạng mẫu: 59-X1 123.45."
  );
  setMessage("");
  return;
}

    try {
      localStorage.setItem(
        PROFILE_KEY,
        JSON.stringify(cleanedProfile)
      );

      setProfile(cleanedProfile);
      setError("");
      setMessage("Đã lưu hồ sơ trên trình duyệt này.");
    } catch {
      setMessage("");
      setError("Không thể lưu hồ sơ. Vui lòng thử lại.");
    }
  }

  return (
    <section className="status-card">
      <h2>Hồ sơ tài xế</h2>

      <form className="profile-form" onSubmit={handleSave}>
        <label htmlFor="fullName">Họ và tên</label>
        <input
          id="fullName"
          name="fullName"
          value={profile.fullName}
          onChange={handleChange}
          autoComplete="name"
          maxLength={100}
          required
        />

        <label htmlFor="phone">Số điện thoại</label>
        <input
          id="phone"
          name="phone"
          type="tel"
          value={profile.phone}
          onChange={handleChange}
          autoComplete="tel"
          maxLength={20}
          placeholder="Nhập số điện thoại"
          required
        />

        <label htmlFor="vehicle">Phương tiện</label>
        <input
          id="vehicle"
          name="vehicle"
          value={profile.vehicle}
          onChange={handleChange}
          maxLength={100}
          placeholder="Ví dụ: Honda Wave"
          required
        />

        <label htmlFor="licensePlate">Biển số xe</label>
        <input
          id="licensePlate"
          name="licensePlate"
          value={profile.licensePlate}
          onChange={handleChange}
          maxLength={20}
          placeholder="Nhập biển số xe"
          required
        />

        <button type="submit" className="toggle-button">
          Lưu hồ sơ
        </button>

        {error && (
          <p className="form-error" role="alert">
            {error}
          </p>
        )}

        <p className="feedback" role="status">
          {message}
        </p>
      </form>

      <p className="note">
        Hồ sơ thực hành lưu trên trình duyệt,
        chưa xác thực danh tính hoặc kết nối backend.
      </p>
    </section>
  );
}   