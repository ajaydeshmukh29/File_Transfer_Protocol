import { useState } from "react";
import "./Login.css";

function Login({ loginSuccess, goToRegister }) {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");

  const loginUser = async (event) => {
    event.preventDefault();

    try {
      const response = await fetch("http://localhost:8080/api/auth/login", {
        method: "POST",
        headers: {
          "Content-Type": "application/json"
        },
        body: JSON.stringify({
          email: email,
          password: password
        })
      });

      // Read the response as text first.
      // Backend may return either JSON or plain text.
      const responseText = await response.text();

      let data;

      try {
        data = JSON.parse(responseText);
      } catch {
        data = null;
      }

      if (!response.ok) {
        const message =
          (data && data.message) ||
          responseText ||
          "Login failed!";

        throw new Error(message);
      }

      // Successful login returns:
      // { message, token, name, email }

      loginSuccess(data.token, {
        name: data.name,
        email: data.email
      });

    } catch (error) {
      console.error("Login error:", error);
      alert(error.message || "Login failed!");
    }
  };

  return (
    <div className="login-page">
      <div className="login-container">

        <div className="login-header">
          <h1>FileFlow</h1>
          <p>Welcome back!</p>
        </div>

        <form onSubmit={loginUser}>

          <div className="input-group">
            <label>Email</label>

            <input
              type="email"
              placeholder="Enter your email"
              value={email}
              onChange={(event) => setEmail(event.target.value)}
              required
            />
          </div>

          <div className="input-group">
            <label>Password</label>

            <input
              type="password"
              placeholder="Enter your password"
              value={password}
              onChange={(event) => setPassword(event.target.value)}
              required
            />
          </div>

          <button type="submit" className="login-button">
            Login
          </button>

        </form>

        <p className="register-text">
          Don't have an account?
          <span onClick={goToRegister}>{" "}Register</span>
        </p>

      </div>
    </div>
  );
}

export default Login;