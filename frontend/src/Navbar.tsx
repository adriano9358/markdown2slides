import { Link } from "react-router-dom";
import { useContext, useEffect, useState } from "react";
import { AuthContext } from "./AuthProvider";

export const Navbar = () => {
  const { username } = useContext(AuthContext);

  const handleLogout = async () => {
    await fetch("http://localhost:8080/logout", {
      method: "GET",
      credentials: "include",
    });
    window.location.href = "/";
  };

  return (
    <nav className="navbar navbar-expand-lg navbar-dark bg-primary">
      <div className="container">
        <Link className="navbar-brand" to="/">Markdown Slides</Link>
        <div className="navbar-nav me-auto">
          <Link className="nav-link" to="/">Home</Link>
          <Link className="nav-link" to="/projects">My Projects</Link>
          <Link className="nav-link" to="/about">About</Link>
          <Link className="nav-link" to="/contact">Contact</Link>
        </div>

        <div className="navbar-text">
          {username ? (
            <>
              <span className="me-2">Hello, {username}</span>
              <button className="btn btn-sm btn-outline-light" onClick={handleLogout}>
                Logout
              </button>
            </>
          ) : (
            <a className="btn btn-sm btn-light" href="http://localhost:8080/oauth2/authorization/google">
              Login with Google
            </a>
          )}
        </div>
      </div>
    </nav>
  );
};
