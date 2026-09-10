import React, { useState } from "react";
import "./Login.css";
function Register({ goToLogin }) {
  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");

  const registerUser = (event) => {
    event.preventDefault();

    fetch("http://localhost:8080/api/auth/register", {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify({
        name: name,
        email: email,
        password: password
      })
    })
      .then((response) => response.text())
      .then((message) => {
        alert(message);

        if (message === "User registered successfully!") {
          setName("");
          setEmail("");
          setPassword("");

          goToLogin();
        }
      })
      .catch((error) => {
        console.error("Registration error:", error);
        alert("Registration failed!");
      });
  };

  return (
    <div className="login-page">

      <div className="login-container">

        <div className="login-header">
          <h1>FileFlow</h1>
          <p>Create your account</p>
        </div>

        <form onSubmit={registerUser}>

          <div className="input-group">
            <label>Name</label>

            <input
              type="text"
              placeholder="Enter your name"
              value={name}
              onChange={(event) =>
                setName(event.target.value)
              }
              required
            />
          </div>

          <div className="input-group">
            <label>Email</label>

            <input
              type="email"
              placeholder="Enter your email"
              value={email}
              onChange={(event) =>
                setEmail(event.target.value)
              }
              required
            />
          </div>

          <div className="input-group">
            <label>Password</label>

            <input
              type="password"
              placeholder="Enter your password"
              value={password}
              onChange={(event) =>
                setPassword(event.target.value)
              }
              required
            />
          </div>

          <button
            type="submit"
            className="login-button"
          >
            Register
          </button>

        </form>

        <p className="register-text">
          Already have an account?

          <span onClick={goToLogin}>
            {" "}Login
          </span>
        </p>

      </div>

    </div>
  );
}

export default Register;