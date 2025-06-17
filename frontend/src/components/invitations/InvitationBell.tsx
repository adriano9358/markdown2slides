import { useEffect, useState, useContext } from "react";
import { Link } from "react-router-dom";
import { AuthContext } from "../../providers/AuthProvider";

export const InvitationBell = () => {
  const { user } = useContext(AuthContext);
  const [hasInvitations, setHasInvitations] = useState(false);

  useEffect(() => {
    const fetchInvitations = async () => {
      if (!user) return;

      try {
        const res = await fetch("http://localhost:8080/invitations", {
          credentials: "include",
        });

        if (res.ok) {
          const data = await res.json();
          const pending = data.filter((inv: any) => inv.status === "PENDING");
          setHasInvitations(pending.length > 0);
        }
      } catch (error) {
        console.error("Failed to fetch invitations:", error);
      }
    };

    fetchInvitations();
  }, [user]);

  return (
    <Link to="/invitations" className="text-white position-relative">
      <i className="bi bi-bell fs-5"></i>
      {hasInvitations && (
        <span className="position-absolute top-0 start-100 translate-middle p-1 bg-danger border border-light rounded-circle">
          <span className="visually-hidden">New invitations</span>
        </span>
      )}
    </Link>
  );
};
