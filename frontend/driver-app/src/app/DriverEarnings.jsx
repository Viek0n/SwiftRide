export default function DriverEarnings({ tripHistory }) {
  const totalFare = tripHistory.reduce(
    (total, item) => total + item.fare,
    0
  );

  return (
    <>
      <section className="status-card">
        <h2>Thống kê mô phỏng</h2>

        <p>
          Số chuyến đã thanh toán:{" "}
          <strong>{tripHistory.length}</strong>
        </p>

        <p>Tổng giá trị chuyến:</p>

        <p className="total-fare">
          {totalFare.toLocaleString("vi-VN")} đ
        </p>

        <p className="note">
          Tổng giá chuyến mẫu, chưa trừ phí nền tảng.
          Lịch sử được lưu trên trình duyệt này.
        </p>
      </section>

      <section className="status-card">
        <h2>Lịch sử chuyến</h2>

        {tripHistory.length === 0 ? (
          <p className="description">
            Chưa có chuyến nào được thanh toán mô phỏng.
          </p>
        ) : (
          <ul className="history-list">
            {tripHistory.map((item) => (
              <li key={item.id} className="history-item">
                <strong>{item.riderName}</strong>

                <p>Đón: {item.pickup}</p>
                <p>Đến: {item.destination}</p>

                <p>
                  {new Date(item.completedAt).toLocaleString("vi-VN")}
                </p>

                <strong className="history-fare">
                  {item.fare.toLocaleString("vi-VN")} đ
                </strong>

                <p className="note">
                  Đã thanh toán mô phỏng
                </p>
              </li>
            ))}
          </ul>
        )}
      </section>
    </>
  );
}