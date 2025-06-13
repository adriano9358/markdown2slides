import * as React from "react";
import { Link, Outlet, useLocation } from "react-router-dom";
import { Navbar } from "./Navbar";

export function Layout() {
  const location = useLocation();

  const isPrintPage = location.pathname.includes('/print');


  return isPrintPage ? (
    <Outlet />
  ) :(
    <div className="d-flex flex-column vh-100 bg-danger-subtle">
      <Navbar />
      <div className="flex-grow-1 d-flex flex-column overflow-hidden">
        <Outlet />
      </div>
    </div>
  );
}
