import { Link } from "react-router-dom";
import { useContext, useEffect, useState } from "react";
import { AuthContext } from "../../providers/AuthProvider";
import { InvitationBell } from "../invitations/InvitationBell";
import { OAUTH_ENDPOINT } from "../../auth/oauth_endpoint";
import { logOut } from "../../http/userApi";

export const Navbar = () => {
  const { user } = useContext(AuthContext);
  
  const handleLogout = async () => {
    await logOut();
    window.location.href = "/";
  };

  const handleLogin = () => {
    window.location.href = OAUTH_ENDPOINT;
  };

  return (
    <nav className="navbar navbar-expand-lg navbar-dark bg-primary">
      <div className="container-fluid">
        <Link className="navbar-brand d-flex align-items-center gap-2" to="/">
          <img src="/logo.png" alt="Logo" height="30" />
          markdown2slides
        </Link>
        <div className="navbar-nav me-auto">
          <Link className="nav-link" to="/">Home</Link>
          <Link className="nav-link" to="/projects">My Projects</Link>
          <Link className="nav-link" to="/about">About</Link>
          <Link className="nav-link" to="/contact">Contact</Link>
        </div>

        <div className="navbar-text d-flex align-items-center gap-3">
          {user ? (
            <>
              <InvitationBell />

              <span className="me-2">Hello, {user.name}</span>
              <button className="btn btn-sm btn-outline-light" onClick={handleLogout}>
                Logout
              </button>
            </>
          ) : (
            <button className="btn btn-sm btn-outline-light" onClick={handleLogin}>
              Login with Google
            </button>
          )}
        </div>
      </div>
    </nav>
  );
};
