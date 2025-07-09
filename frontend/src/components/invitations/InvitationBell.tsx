import { useEffect, useState, useContext } from "react";
import { Link } from "react-router-dom";
import { AuthContext } from "../../providers/AuthProvider";
import { getUserInvitations } from "../../http/invitationsApi";
import { useInvitationContext } from "../../providers/InviteProvider";

export const InvitationBell = () => {
  const { user } = useContext(AuthContext);
  const { refreshFlag } = useInvitationContext();
  const [hasInvitations, setHasInvitations] = useState(false);

  const fetchInvitations = async () => {
    if (!user) return;
    try {
      const invitations = await getUserInvitations();
      const pending = invitations.filter((inv: any) => inv.status === "PENDING");
      setHasInvitations(pending.length > 0);
    } catch (error) {
      console.error("Failed to fetch invitations:", error);
    }
  };


  useEffect(() => {
    fetchInvitations();
  }, [user, refreshFlag]);

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
