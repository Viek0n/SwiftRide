import { useCallback, useEffect, useState } from "react";
import DriverDashboard from "../../features/driver/pages/DriverDashboard";
import DriverStatus from "../../features/driver/pages/DriverStatus";
import DriverOrders from "../../features/driver/pages/DriverOrders";
import DriverOrderDetail from "../../features/driver/pages/DriverOrderDetail";
import DriverActiveTrip from "../../features/driver/pages/DriverActiveTrip";
import DriverMap from "../../features/driver/pages/DriverMap";
import DriverHistory from "../../features/driver/pages/DriverHistory";
import DriverProfile from "../../features/driver/pages/DriverProfile";
import DriverVehicle from "../../features/driver/pages/DriverVehicle";
import AuthPage from "../../features/auth/AuthPage";

const SESSION_KEY = "swiftride.driver.session";

const pages = [
  { match: (path) => path === "/driver/dashboard", title: "Tổng quan", subtitle: "Chào buổi sáng, Minh! Sẵn sàng cho một ngày hiệu quả nhé.", component: DriverDashboard },
  { match: (path) => path === "/driver/status", title: "Trạng thái hoạt động", subtitle: "Quản lý trạng thái sẵn sàng nhận chuyến.", component: DriverStatus },
  { match: (path) => path === "/driver/orders", title: "Danh sách chuyến mới", subtitle: "Các yêu cầu chuyến đi phù hợp gần bạn.", component: DriverOrders },
  { match: (path) => /^\/driver\/orders\/[^/]+$/.test(path), title: "Chi tiết chuyến", subtitle: "Xem thông tin và quyết định nhận chuyến.", component: DriverOrderDetail },
  { match: (path) => path === "/driver/active-trip", title: "Chuyến đang thực hiện", subtitle: "Theo dõi tiến trình chuyến đi hiện tại.", component: DriverActiveTrip },
  { match: (path) => path === "/driver/map", title: "Bản đồ điều hướng", subtitle: "Tìm đường nhanh và thuận tiện nhất.", component: DriverMap },
  { match: (path) => path === "/driver/history", title: "Lịch sử chuyến", subtitle: "Theo dõi các chuyến đi đã hoàn thành.", component: DriverHistory },
  { match: (path) => path === "/driver/profile", title: "Hồ sơ cá nhân", subtitle: "Quản lý thông tin tài khoản của bạn.", component: DriverProfile },
  { match: (path) => path === "/driver/vehicle", title: "Quản lý phương tiện", subtitle: "Thông tin xe đã đăng ký với SwiftRide.", component: DriverVehicle },
];

function getPath() {
  return window.location.pathname.replace(/\/+$/, "")
    || (window.sessionStorage.getItem(SESSION_KEY) ? "/driver/map" : "/driver/login");
}

export default function AppRouter() {
  const [path, setPath] = useState(getPath);
  const [isOnline, setIsOnline] = useState(true);
  const page = pages.find((item) => item.match(path)) || pages[0];
  const Page = page.component;
  const orderId = path.match(/^\/driver\/orders\/([^/]+)$/)?.[1];
  const isMapPage = path === "/driver/map";
  const isFinancePage = path === "/driver/history";
  const authMode = path.match(/^\/driver\/(login|register)$/)?.[1];
  const isAuthenticated = Boolean(window.sessionStorage.getItem(SESSION_KEY));

  useEffect(() => {
    const handlePopState = () => setPath(getPath());
    window.addEventListener("popstate", handlePopState);
    return () => window.removeEventListener("popstate", handlePopState);
  }, []);

  const navigate = useCallback((nextPath) => {
    if (nextPath === path) return;
    window.history.pushState({}, "", nextPath);
    setPath(nextPath);
    window.scrollTo(0, 0);
  }, [path]);

  useEffect(() => {
    if (!authMode && !isAuthenticated) navigate("/driver/login");
    if (authMode && isAuthenticated) navigate("/driver/map");
  }, [authMode, isAuthenticated, navigate]);

  function handleLink(event, nextPath) {
    if (event.metaKey || event.ctrlKey || event.shiftKey || event.altKey) return;
    event.preventDefault();
    navigate(nextPath);
  }

  function logout() {
    window.sessionStorage.removeItem(SESSION_KEY);
    navigate("/driver/login");
  }

  if (authMode) {
    return <AuthPage mode={authMode} navigate={navigate} onSuccess={() => navigate("/driver/map")} />;
  }
  if (!isAuthenticated) return null;

  return (
    <div className="app-shell">
      <div className="main-column">
        <header className="topbar">
          <a className="mobile-brand" href="/driver/dashboard" onClick={(event) => handleLink(event, "/driver/dashboard")}>
            <span className="brand-mark">S</span>
            <span>SwiftRide</span>
          </a>
          <div className="topbar-actions">
            <button className="auth-logout" onClick={logout}>Đăng xuất</button>
          </div>
        </header>
        <main className={`page-main ${isMapPage ? "map-screen-main" : ""}`}>
          {!isMapPage && !isFinancePage && <div className="page-heading">
            <div><h1>{page.title}</h1><p>{page.subtitle}</p></div>
            <div className="page-heading-right"><span className="date-chip"><span>▦</span> Thứ Sáu, 13 tháng 11, 2026</span></div>
          </div>}
          <Page
            navigate={navigate}
            isOnline={isOnline}
            setIsOnline={setIsOnline}
            orderId={orderId}
          />
          <footer className="page-footer"><span>© 2025 SwiftRide. Đồng hành cùng tài xế Việt.</span><span>Trung tâm hỗ trợ&nbsp; · &nbsp;Chính sách quyền riêng tư</span></footer>
        </main>
      </div>
      <nav className="bottom-nav" aria-label="Điều hướng chính">
        <button
          className={`bottom-nav-item ${path === "/driver/history" || path === "/driver/earnings" ? "active" : ""}`}
          onClick={() => navigate("/driver/history")}
        >
          <span className="bottom-nav-icon">◷</span><span>Lịch sử</span>
        </button>
        <button
          className={`bottom-nav-item ${path === "/driver/map" || path.startsWith("/driver/orders") || path === "/driver/active-trip" ? "active" : ""}`}
          onClick={() => navigate("/driver/map")}
        >
          <span className="bottom-nav-icon map-nav-icon">⌖</span><span>Bản đồ</span>
        </button>
        <button
          className={`bottom-nav-item ${path === "/driver/profile" || path === "/driver/vehicle" ? "active" : ""}`}
          onClick={() => navigate("/driver/profile")}
        >
          <span className="bottom-nav-icon">♙</span><span>Hồ sơ</span>
        </button>
      </nav>
    </div>
  );
}
