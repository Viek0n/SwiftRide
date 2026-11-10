export default function DriverStatus({
  isOnline,
  isAccepted,
  hasTrip,
  statusTitle,
  onToggleOnline,
  onCreateOffer,
}) {
  return (
    <section className="status-card">
      <span className={isOnline ? "status online" : "status"}>
        {isOnline ? "Đang trực tuyến" : "Đang ngoại tuyến"}
      </span>

      <h2>{statusTitle}</h2>

      {!isAccepted && (
        <button
          type="button"
          className={isOnline ? "toggle-button stop" : "toggle-button"}
          aria-pressed={isOnline}
          onClick={onToggleOnline}
        >
          {isOnline ? "Tắt nhận cuốc" : "Bật nhận cuốc"}
        </button>
      )}

      {isOnline && !hasTrip && (
        <button
          type="button"
          className="secondary-button"
          onClick={onCreateOffer}
        >
          Tạo lời mời mẫu
        </button>
      )}

      <p className="note">
        Dữ liệu thực hành, chưa kết nối backend.
      </p>
    </section>
  );
}