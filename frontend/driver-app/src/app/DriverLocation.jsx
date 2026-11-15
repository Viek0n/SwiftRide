
import DriverMap from "./DriverMap";
import { useState } from "react";

export default function DriverLocation() {
  const [position, setPosition] = useState(null);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState("");

  function handleGetLocation() {
    if (!navigator.geolocation) {
      setError("Trình duyệt không hỗ trợ lấy vị trí.");
      return;
    }

    setIsLoading(true);
    setError("");

    navigator.geolocation.getCurrentPosition(
      (result) => {
        setPosition({
          latitude: result.coords.latitude,
          longitude: result.coords.longitude,
          accuracy: result.coords.accuracy,
        });

        setIsLoading(false);
      },
      (locationError) => {
        if (locationError.code === 1) {
          setError(
            "Bạn chưa cho phép truy cập vị trí. Hãy cấp quyền trong trình duyệt."
          );
        } else if (locationError.code === 2) {
          setError("Không xác định được vị trí. Vui lòng thử lại.");
        } else {
          setError("Lấy vị trí quá lâu. Vui lòng thử lại.");
        }

        setIsLoading(false);
      },
      {
        enableHighAccuracy: true,
        timeout: 15000,
        maximumAge: 0,
      }
    );
  }

  return (
    <section className="status-card">
      <h2>Vị trí tài xế</h2>

      <button
        type="button"
        className="secondary-button"
        onClick={handleGetLocation}
        disabled={isLoading}
      >
        {isLoading ? "Đang lấy vị trí..." : "Lấy vị trí hiện tại"}
      </button>

      {error && (
        <p className="form-error" role="alert">
          {error}
        </p>
      )}
      <DriverMap position={position} />

      <div role="status">
        {position && (
          <dl className="trip-details">
            <div>
              <dt>Vĩ độ</dt>
              <dd>{position.latitude.toFixed(6)}</dd>
            </div>

            <div>
              <dt>Kinh độ</dt>
              <dd>{position.longitude.toFixed(6)}</dd>
            </div>

            <div>
              <dt>Độ chính xác ước tính</dt>
              <dd>Khoảng {Math.round(position.accuracy)} mét</dd>
            </div>
          </dl>
        )}
      </div>

      <p className="note">
        Vị trí được lấy khi bấm nút và dùng để tải bản đồ
        OpenStreetMap, chưa gửi lên backend SwiftRide.
      </p>
    </section>
  );
}