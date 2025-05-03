import * as React from "react";
import { Link, Outlet } from "react-router-dom";
import { Navbar } from "./Navbar";

export function Layout() {
  return (
    <div className="d-flex flex-column vh-100">
      <Navbar />
      <div className="container-fluid flex-grow-1 d-flex">
        <Outlet />
      </div>
    </div>
  );
}
