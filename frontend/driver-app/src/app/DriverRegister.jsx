import { useState } from "react";

export default function DriverRegister({ onBackToLogin }) {
  const [fullName, setFullName] = useState("");
  const [phone, setPhone] = useState("");
  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [error, setError] = useState("");
  const [isSubmitted, setIsSubmitted] = useState(false);

  function handleSubmit(event) {
  event.preventDefault();
  setError("");

  if (
    !fullName.trim() ||
    !phone.trim() ||
    !password ||
    !confirmPassword
  ) {
    setError("Vui lòng nhập đầy đủ thông tin.");
    return;
  }

  if (!/^0\d{9}$/.test(phone.trim())) {
    setError(
      "Số điện thoại cần có 10 chữ số và bắt đầu bằng số 0."
    );
    return;
  }

  if (password.length < 6) {
    setError("Mật khẩu cần ít nhất 6 ký tự.");
    return;
  }

  if (password !== confirmPassword) {
    setError("Mật khẩu xác nhận không khớp.");
    return;
  }

  setPassword("");
  setConfirmPassword("");
  setIsSubmitted(true);
}

  if (isSubmitted) {
    return (
      <section className="status-card">
        <h2>Đã kiểm tra thông tin</h2>

        <p role="status">
          Thông tin đăng ký đúng định dạng. Bản demo chưa tạo
          tài khoản trên hệ thống.
        </p>

        <button
          type="button"
          className="toggle-button"
          onClick={onBackToLogin}
        >
          Về đăng nhập
        </button>
      </section>
    );
  }

  return (
    <section className="status-card">
      <h2>Đăng ký tài xế</h2>

      <form
  className="profile-form"
  onSubmit={handleSubmit}
  noValidate
>
        <label htmlFor="register-name">Họ và tên</label>
        <input
          id="register-name"
          value={fullName}
          onChange={(event) => setFullName(event.target.value)}
          autoComplete="name"
          maxLength={100}
          required
        />

        <label htmlFor="register-phone">Số điện thoại</label>
        <input
          id="register-phone"
          type="tel"
          value={phone}
          onChange={(event) => setPhone(event.target.value)}
          autoComplete="tel"
          maxLength={10}
          required
        />

        <label htmlFor="register-password">Mật khẩu</label>
        <input
          id="register-password"
          type="password"
          value={password}
          onChange={(event) => setPassword(event.target.value)}
          autoComplete="new-password"
          minLength={6}
          required
        />

        <label htmlFor="register-confirm">Nhập lại mật khẩu</label>
        <input
          id="register-confirm"
          type="password"
          value={confirmPassword}
          onChange={(event) =>
            setConfirmPassword(event.target.value)
          }
          autoComplete="new-password"
          minLength={6}
          required
        />

        {error && (
          <p className="form-error" role="alert">
            {error}
          </p>
        )}

        <button type="submit" className="toggle-button">
          Kiểm tra đăng ký demo
        </button>

        <button
          type="button"
          className="secondary-button"
          onClick={onBackToLogin}
        >
          Quay lại đăng nhập
        </button>
      </form>

      <p className="note">
        Dùng thông tin thử nghiệm. Chưa gửi lên backend
        và không lưu mật khẩu.
      </p>
    </section>
  );
}