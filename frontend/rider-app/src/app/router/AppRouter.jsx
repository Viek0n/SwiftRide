import { useCallback, useEffect, useState } from "react";
import DriverOrders from "../../pages/DriverOrders";
import DriverOrderDetail from "../../pages/DriverOrderDetail";
import DriverActiveTrip from "../../pages/DriverActiveTrip";
import DriverMap from "../../pages/DriverMap";
import DriverHistory from "../../pages/DriverHistory";
import DriverProfile from "../../pages/DriverProfile";
import AuthPage from "../../pages/auth/AuthPage";

const SESSION_KEY = "swiftride.rider.session";

const pages = [
  { match: (path) => path === "/rider/orders", title: "Danh sách chuyến mới", subtitle: "Các yêu cầu chuyến đi phù hợp gần bạn.", component: DriverOrders },
  { match: (path) => /^\/rider\/orders\/[^/]+$/.test(path), title: "Chi tiết chuyến", subtitle: "Xem thông tin và quyết định nhận chuyến.", component: DriverOrderDetail },
  { match: (path) => path === "/rider/active-trip", title: "Chuyến đang thực hiện", subtitle: "Theo dõi tiến trình chuyến đi hiện tại.", component: DriverActiveTrip },
  { match: (path) => path === "/rider/map", title: "Bản đồ điều hướng", subtitle: "Tìm đường nhanh và thuận tiện nhất.", component: DriverMap },
  { match: (path) => path === "/rider/history", title: "Lịch sử chuyến", subtitle: "Theo dõi các chuyến đi đã hoàn thành.", component: DriverHistory },
  { match: (path) => path === "/rider/profile", title: "Hồ sơ cá nhân", subtitle: "Quản lý thông tin tài khoản của bạn.", component: DriverProfile },
];

function getPath() {
  return window.location.pathname.replace(/\/+$/, "")
    || (window.sessionStorage.getItem(SESSION_KEY) ? "/rider/map" : "/rider/login");
}

export default function AppRouter() {
  const [path, setPath] = useState(getPath);
  const [isOnline, setIsOnline] = useState(true);
  const page = pages.find((item) => item.match(path)) || pages[0];
  const Page = page.component;
  const orderId = path.match(/^\/rider\/orders\/([^/]+)$/)?.[1];
  const isMapPage = path === "/rider/map";
  const authMode = path.match(/^\/rider\/(login|register)$/)?.[1];
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
    if (!authMode && !isAuthenticated) navigate("/rider/login");
    if (authMode && isAuthenticated) navigate("/rider/map");
  }, [authMode, isAuthenticated, navigate]);

  function handleLink(event, nextPath) {
    if (event.metaKey || event.ctrlKey || event.shiftKey || event.altKey) return;
    event.preventDefault();
    navigate(nextPath);
  }

  function logout() {
    window.sessionStorage.removeItem(SESSION_KEY);
    navigate("/rider/login");
  }

  if (authMode) {
    return <AuthPage mode={authMode} navigate={navigate} onSuccess={() => navigate("/rider/map")} />;
  }
  if (!isAuthenticated) return null;

  return (
    <div className="app-shell">
      <div className="main-column">
        <header className="topbar">
          <a className="mobile-brand" href="/rider/dashboard" onClick={(event) => handleLink(event, "/rider/dashboard")}>
            <span className="brand-mark">S</span>
            <span>SwiftRide</span>
          </a>
          <div className="topbar-actions"><button className="auth-logout" onClick={logout}>Đăng xuất</button></div>
        </header>
        <main className={`page-main ${isMapPage ? "map-screen-main" : ""}`}>
          {!isMapPage && <div className="page-heading">
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
          className={`bottom-nav-item ${path === "/rider/history" ? "active" : ""}`}
          onClick={() => navigate("/rider/history")}
        >
          <span className="bottom-nav-icon">◷</span><span>Lịch sử chuyến</span>
        </button>
        <button
          className={`bottom-nav-item ${path === "/rider/map" || path.startsWith("/rider/orders") || path === "/rider/active-trip" ? "active" : ""}`}
          onClick={() => navigate("/rider/map")}
        >
          <span className="bottom-nav-icon map-nav-icon">⌖</span><span>Bản đồ</span>
        </button>
        <button
          className={`bottom-nav-item ${path === "/rider/profile" || path === "/rider/vehicle" ? "active" : ""}`}
          onClick={() => navigate("/rider/profile")}
        >
          <span className="bottom-nav-icon">♙</span><span>Hồ sơ</span>
        </button>
      </nav>
    </div>
  );
}
