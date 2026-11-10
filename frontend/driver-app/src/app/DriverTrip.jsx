
import DriverMap from "./DriverMap";
export default function DriverTrip({
  trip,
  isAccepted,
  tripStatus,
  onAccept,
  onReject,
  onPickup,
  onDropoff,
  onMockPaymentSuccess,
}) {
  if (!trip) {
    return null;
  }

  return (
    <section className="status-card">
      <h2>
        {isAccepted ? "Chuyến đã nhận" : "Có lời mời chuyến mới"}
      </h2>

      <dl className="trip-details">
        <div>
          <dt>Mã chuyến</dt>
          <dd>{trip.id}</dd>
        </div>

        <div>
          <dt>Khách hàng</dt>
          <dd>{trip.riderName}</dd>
        </div>

        <div>
          <dt>Điểm đón</dt>
          <dd>{trip.pickup}</dd>
        </div>

        <div>
          <dt>Điểm đến</dt>
          <dd>{trip.destination}</dd>
        </div>

        <div>
          <dt>Quãng đường</dt>
          <dd>{trip.distance}</dd>
        </div>

        <div>
          <dt>Giá chuyến</dt>
          <dd className="fare">
            {trip.fare.toLocaleString("vi-VN")} đ
          </dd>
        </div>
      </dl>
      {trip.pickupPosition && trip.destinationPosition && (
  <div>
    <h3>
      {tripStatus === "INPROGRESS" ||
      tripStatus === "PAYMENT_PENDING"
        ? "Vị trí điểm đến"
        : "Vị trí điểm đón"}
    </h3>

    <DriverMap
      position={
        tripStatus === "INPROGRESS" ||
        tripStatus === "PAYMENT_PENDING"
          ? trip.destinationPosition
          : trip.pickupPosition
      }
    />

    <p className="note">
      Tọa độ mẫu phục vụ thực hành, chưa có đường dẫn di chuyển.
    </p>
  </div>
)}

      {!isAccepted && (
        <div className="trip-actions">
          <button
            type="button"
            className="secondary-button"
            onClick={onReject}
          >
            Từ chối
          </button>

          <button
            type="button"
            className="toggle-button"
            onClick={onAccept}
          >
            Nhận cuốc
          </button>
        </div>
      )}

      {tripStatus === "ARRIVING" && (
        <button
          type="button"
          className="toggle-button"
          onClick={onPickup}
        >
          Đã đón khách
        </button>
      )}

      {tripStatus === "INPROGRESS" && (
        <button
          type="button"
          className="toggle-button"
          onClick={onDropoff}
        >
          Đã trả khách
        </button>
      )}

      {tripStatus === "PAYMENT_PENDING" && (
        <div>
          <p className="note">
            Đang chờ xác nhận thanh toán từ hệ thống.
          </p>

          <button
            type="button"
            className="secondary-button"
            onClick={onMockPaymentSuccess}
          >
            Mô phỏng thanh toán thành công
          </button>
        </div>
      )}
    </section>
  );
}