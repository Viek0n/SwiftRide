import { useState } from "react";
import "./auth.css";

const API_GATEWAY_BASE_URL = (process.env.REACT_APP_API_GATEWAY_URL || "http://localhost:8080").replace(/\/+$/, "");
const SESSION_KEY = "swiftride.driver.session";

async function postAuthRequest(path, body) {
  const response = await fetch(`${API_GATEWAY_BASE_URL}/auth${path}`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(body),
  });
  const text = await response.text();
  let result = {};
  if (text) {
    try {
      result = JSON.parse(text);
    } catch {
      result = { message: text };
    }
  }
  if (!response.ok) {
    throw new Error(result.message || result.detail || result.error || `Yêu cầu thất bại (${response.status})`);
  }
  return result;
}

export default function AuthPage({ mode, navigate, onSuccess }) {
  const [values, setValues] = useState({
    name: "",
    phone: "",
    password: "",
    driverLicense: "",
    licensePlate: "",
  });
  const [pendingUserId, setPendingUserId] = useState("");
  const [error, setError] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);
  const isRegister = mode === "register";

  function updateField(event) {
    setValues((current) => ({ ...current, [event.target.name]: event.target.value }));
  }

  async function handleSubmit(event) {
      event.preventDefault();
      setError("");
      setIsSubmitting(true);

      try {
        if (isRegister) {
          let userId = pendingUserId;

          if (!userId) {
            const user = await postAuthRequest("/register/user", {
              name: values.name.trim(),
              phone: values.phone.trim(),
              password: values.password,
            });

            userId = user.id;
            setPendingUserId(userId);
          }

          await postAuthRequest("/register/driver", {
            userId,
            driverLicense: values.driverLicense.trim(),
            licensePlate: values.licensePlate.trim(),
          });
        }

        // Login after registration, or directly login in login mode.
        const result = await postAuthRequest("/login/driver", {
          phone: values.phone.trim(),
          password: values.password,
        });

        if (!result.accessToken || !result.user) {
          throw new Error("Invalid login response");
        }

        window.sessionStorage.setItem(
          SESSION_KEY,
          JSON.stringify({
            accessToken: result.accessToken,
            user: result.user,
            driver: result.driver,
          })
        );

        onSuccess();

    } catch (requestError) {
        setError(
            requestError.message ||
            "Unable to connect to server. Please try again."
        );
    } finally {
        setIsSubmitting(false);
    }
  }

  function switchMode(event, nextMode) {
    event.preventDefault();
    navigate(`/driver/${nextMode}`);
  }

  return (
    <main className="auth-page">
      <section className="auth-card" aria-labelledby="auth-title">
        <div className="auth-brand">
          <span className="auth-brand-mark">S</span>
          <span>SwiftRide</span>
          <span className="auth-role">TÀI XẾ</span>
        </div>
        <div className="auth-intro">
          <p className="auth-eyebrow">{isRegister ? "BẮT ĐẦU CÙNG SWIFTRIDE" : "CHÀO MỪNG TRỞ LẠI"}</p>
          <h1 id="auth-title">{isRegister ? "Tạo tài khoản tài xế" : "Đăng nhập tài xế"}</h1>
          <p>{isRegister ? "Đăng ký để nhận chuyến và đồng hành cùng SwiftRide." : "Đăng nhập để tiếp tục hành trình của bạn."}</p>
        </div>

        <div className="auth-tabs" role="tablist" aria-label="Tài khoản">
          <a
            href="/driver/login"
            className={!isRegister ? "active" : ""}
            aria-selected={!isRegister}
            role="tab"
            onClick={(event) => switchMode(event, "login")}
          >
            Đăng nhập
          </a>
          <a
            href="/driver/register"
            className={isRegister ? "active" : ""}
            aria-selected={isRegister}
            role="tab"
            onClick={(event) => switchMode(event, "register")}
          >
            Đăng ký
          </a>
        </div>

        <form className="auth-form" onSubmit={handleSubmit}>
          {isRegister && (
            <label>
              Họ và tên
              <input name="name" autoComplete="name" value={values.name} onChange={updateField} required minLength={2} />
            </label>
          )}
          <label>
            Số điện thoại
            <input name="phone" type="tel" autoComplete="tel" value={values.phone} onChange={updateField} required />
          </label>
          <label>
            Mật khẩu
            <input name="password" type="password" autoComplete={isRegister ? "new-password" : "current-password"} value={values.password} onChange={updateField} required minLength={8} />
          </label>
          {isRegister && (
            <div className="auth-field-row">
              <label>
                Bằng lái xe
                <input name="driverLicense" autoComplete="off" value={values.driverLicense} onChange={updateField} required />
              </label>
              <label>
                Biển số xe
                <input name="licensePlate" autoComplete="off" value={values.licensePlate} onChange={updateField} required />
              </label>
            </div>
          )}
          {error && <p className="auth-error" role="alert">{error}</p>}
          <button className="auth-submit" type="submit" disabled={isSubmitting}>
            {isSubmitting ? "Đang xử lý..." : isRegister ? "Tạo tài khoản" : "Đăng nhập"}
            {!isSubmitting && <span aria-hidden="true">→</span>}
          </button>
        </form>

        <p className="auth-switch">
          {isRegister ? "Đã có tài khoản?" : "Bạn chưa có tài khoản?"}{" "}
          <a href={`/driver/${isRegister ? "login" : "register"}`} onClick={(event) => switchMode(event, isRegister ? "login" : "register")}>
            {isRegister ? "Đăng nhập" : "Đăng ký tài xế"}
          </a>
        </p>
        <p className="auth-terms">Bằng việc tiếp tục, bạn đồng ý với Điều khoản dịch vụ và Chính sách quyền riêng tư của SwiftRide.</p>
      </section>
    </main>
  );
}
