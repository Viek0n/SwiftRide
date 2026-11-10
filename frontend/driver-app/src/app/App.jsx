import DriverRegister from "./DriverRegister";
import DriverLogin from "./DriverLogin";
import { useEffect, useState } from "react";
import DriverProfile from "./DriverProfile";
import DriverEarnings from "./DriverEarnings";
import DriverNavigation from "./DriverNavigation";
import DriverStatus from "./DriverStatus";
import DriverTrip from "./DriverTrip";
import DriverLocation from "./DriverLocation";
const HISTORY_KEY = "swiftride-driver-history";

const sampleTrip = {
  riderName: "Nguyễn Minh Anh",
  pickup: "273 An Dương Vương, Quận 5",
  destination: "Chợ Bến Thành, Quận 1",
  distance: "4,2 km",
  fare: 35000,

  pickupPosition: {
    latitude: 10.7595,
    longitude: 106.6822,
  },

  destinationPosition: {
    latitude: 10.7725,
    longitude: 106.6980,
  },
};

export default function App() {
  const [isOnline, setIsOnline] = useState(false);
  const [trip, setTrip] = useState(null);
  const [isAccepted, setIsAccepted] = useState(false);
  const [tripStatus, setTripStatus] = useState("");
  const [message, setMessage] = useState("");
  const [activeTab, setActiveTab] = useState("home");
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [authScreen, setAuthScreen] = useState("login");

  const [tripHistory, setTripHistory] = useState(() => {
    try {
      const saved = localStorage.getItem(HISTORY_KEY);
      const parsed = saved ? JSON.parse(saved) : [];

      if (!Array.isArray(parsed)) {
        return [];
      }

      return parsed.filter(
        (item) =>
          item &&
          typeof item.id === "string" &&
          typeof item.fare === "number" &&
          Number.isFinite(item.fare) &&
          item.fare >= 0 &&
          typeof item.completedAt === "string" &&
          !Number.isNaN(Date.parse(item.completedAt))
      );
    } catch {
      return [];
    }
  });

  useEffect(() => {
    try {
      localStorage.setItem(HISTORY_KEY, JSON.stringify(tripHistory));
    } catch (error) {
      console.error("Không thể lưu lịch sử chuyến:", error);
    }
  }, [tripHistory]);

  
  const statusTitle =
    tripStatus === "ARRIVING"
      ? "Đang đến đón khách"
      : tripStatus === "INPROGRESS"
        ? "Đang chở khách"
        : tripStatus === "PAYMENT_PENDING"
          ? "Đã trả khách — chờ thanh toán"
          : isOnline
            ? "Sẵn sàng nhận cuốc"
            : "Bạn đang nghỉ";

  function handleToggleOnline() {
    if (isAccepted) return;

    setIsOnline((current) => !current);
    setTrip(null);
    setIsAccepted(false);
    setTripStatus("");
    setMessage("");
  }

  function handleCreateOffer() {
    if (!isOnline || trip) return;

    setTrip({
      ...sampleTrip,
      id: crypto.randomUUID(),
    });
    setIsAccepted(false);
    setTripStatus("");
    setMessage("");
  }

  function handleAcceptTrip() {
    if (!trip || isAccepted) return;

    setIsAccepted(true);
    setTripStatus("ARRIVING");
    setMessage("Bạn đã nhận cuốc. Hãy di chuyển đến điểm đón.");
  }

  function handleRejectTrip() {
    if (isAccepted) return;

    setTrip(null);
    setTripStatus("");
    setMessage("Bạn đã từ chối cuốc. Đang chờ lời mời mới.");
  }

  function handlePickup() {
    if (tripStatus !== "ARRIVING") return;

    setTripStatus("INPROGRESS");
    setMessage("Đã đón khách. Hãy di chuyển đến điểm đến.");
  }

  function handleDropoff() {
    if (tripStatus !== "INPROGRESS") return;

    setTripStatus("PAYMENT_PENDING");
    setMessage("Đã trả khách. Đang chờ xác nhận thanh toán.");
  }

  function handleMockPaymentSuccess() {
    if (!trip || tripStatus !== "PAYMENT_PENDING") return;

    const completedTrip = {
      ...trip,
      completedAt: new Date().toISOString(),
    };

    setTripHistory((currentHistory) => {
      const alreadySaved = currentHistory.some(
        (item) => item.id === completedTrip.id
      );

      return alreadySaved
        ? currentHistory
        : [completedTrip, ...currentHistory];
    });

    setTrip(null);
    setIsAccepted(false);
    setTripStatus("");
    setMessage(
      "Mô phỏng: thanh toán thành công. Bạn có thể nhận cuốc mới."
    );
  }
  function handleLogout() {
  if (isAccepted) {
    window.alert(
      "⚠️ Không thể đăng xuất\n\n" +
      "Bạn cần hoàn thành chuyến hiện tại trước khi đăng xuất."
    );
    return;
  }

  setIsLoggedIn(false);
  setIsOnline(false);
  setTrip(null);
  setIsAccepted(false);
  setTripStatus("");
  setMessage("");
  setActiveTab("home");
}
  if (!isLoggedIn) {
  return (
    <main className="driver-app">
      <header>
        <p className="brand">SwiftRide Driver</p>
        <h1>Chào mừng tài xế</h1>
        <p className="description">
          Đăng nhập hoặc đăng ký để bắt đầu.
        </p>
      </header>

      {authScreen === "register" ? (
        <DriverRegister
          onBackToLogin={() => setAuthScreen("login")}
        />
      ) : (
        <DriverLogin
          onDemoLogin={() => {
            setIsLoggedIn(true);
            setActiveTab("home");
          }}
          onOpenRegister={() => setAuthScreen("register")}
        />
      )}
    </main>
  );
}

    return (
    <main className="driver-app">
      <header>
        <p className="brand">SwiftRide Driver</p>
        <h1>Xin chào, tài xế!</h1>
        <p className="description">
          Bật nhận cuốc khi bạn sẵn sàng bắt đầu.
        </p>
      </header>

      <DriverNavigation
        activeTab={activeTab}
        onChangeTab={setActiveTab}
      />
      <button
  type="button"
  className="logout-button"
  onClick={handleLogout}
>
  Đăng xuất
</button>

      {activeTab === "home" && (
        <>
          <DriverStatus
            isOnline={isOnline}
            isAccepted={isAccepted}
            hasTrip={Boolean(trip)}
            statusTitle={statusTitle}
            onToggleOnline={handleToggleOnline}
            onCreateOffer={handleCreateOffer}
          />
                    <DriverLocation />

          <DriverTrip
            trip={trip}
            isAccepted={isAccepted}
            tripStatus={tripStatus}
            onAccept={handleAcceptTrip}
            onReject={handleRejectTrip}
            onPickup={handlePickup}
            onDropoff={handleDropoff}
            onMockPaymentSuccess={handleMockPaymentSuccess}
          />

          <p className="feedback" role="status">
            {message}
          </p>
        </>
      )}

      {activeTab === "earnings" && (
        <DriverEarnings tripHistory={tripHistory} />
      )}

      {activeTab === "profile" && <DriverProfile />}
    </main>
  );
}