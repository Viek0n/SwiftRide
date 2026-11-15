import { useState } from "react";

export default function DriverLogin({
  onDemoLogin,
  onOpenRegister,
}) {
  const [phone, setPhone] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");

  function handleSubmit(event) {
    event.preventDefault();

    if (!/^0\d{9}$/.test(phone.trim())) {
      setError("Số điện thoại cần có 10 chữ số, bắt đầu bằng 0.");
      return;
    }

    if (password.length < 6) {
      setError("Mật khẩu cần ít nhất 6 ký tự.");
      return;
    }

    setError("");
    onDemoLogin();
  }

  return (
    <section className="status-card">
      <h2>Đăng nhập tài xế</h2>

      <form className="profile-form" onSubmit={handleSubmit}>
        <label htmlFor="login-phone">Số điện thoại</label>
        <input
          id="login-phone"
          type="tel"
          value={phone}
          onChange={(event) => {
            setPhone(event.target.value);
            setError("");
          }}
          autoComplete="username"
          placeholder="Nhập số điện thoại"
          maxLength={10}
          required
        />

        <label htmlFor="login-password">Mật khẩu</label>
        <input
          id="login-password"
          type="password"
          value={password}
          onChange={(event) => {
            setPassword(event.target.value);
            setError("");
          }}
          autoComplete="current-password"
          placeholder="Nhập mật khẩu"
          minLength={6}
          required
        />

        {error && (
          <p className="form-error" role="alert">
            {error}
          </p>
        )}

        <button type="submit" className="toggle-button">
          Đăng nhập bản demo
        </button>
        <button
  type="button"
  className="secondary-button"
  onClick={onOpenRegister}
>
  Chưa có tài khoản? Đăng ký
</button>
      </form>

      <p className="note">
        Chưa kết nối API xác thực. Bản demo chỉ kiểm tra
        định dạng nhập, không xác minh tài khoản.
        Hãy dùng thông tin thử nghiệm.
      </p>
    </section>
  );
}