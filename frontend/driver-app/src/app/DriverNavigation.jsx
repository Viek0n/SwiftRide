export default function DriverNavigation({
  activeTab,
  onChangeTab,
}) {
  return (
    <nav className="tab-nav" aria-label="Điều hướng chính">
      <button
        type="button"
        className={activeTab === "home" ? "tab active" : "tab"}
        aria-current={activeTab === "home" ? "page" : undefined}
        onClick={() => onChangeTab("home")}
      >
        Trang chủ
      </button>

      <button
        type="button"
        className={activeTab === "earnings" ? "tab active" : "tab"}
        aria-current={activeTab === "earnings" ? "page" : undefined}
        onClick={() => onChangeTab("earnings")}
      >
        Thu nhập
      </button>

      <button
        type="button"
        className={activeTab === "profile" ? "tab active" : "tab"}
        aria-current={activeTab === "profile" ? "page" : undefined}
        onClick={() => onChangeTab("profile")}
      >
        Hồ sơ
      </button>
    </nav>
  );
}