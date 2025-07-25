import { Outlet, useLocation } from "react-router-dom";
import { Navbar } from "./Navbar";
import { InvitationProvider } from "../../providers/InviteProvider";

export function Layout() {
  const location = useLocation();
  const isPrintPage = location.pathname.includes('/print');

  return isPrintPage ? (
    <Outlet />
  ) :(
    <InvitationProvider>
    <div className="d-flex flex-column vh-100">
      <Navbar />
      <div className="flex-grow-1 overflow-auto bg-body-tertiary">
        <Outlet />
      </div>
    </div>
    </InvitationProvider>
  );
}
