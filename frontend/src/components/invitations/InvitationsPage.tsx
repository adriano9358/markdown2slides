import { useEffect, useState } from "react";
import { LoadingSpinner } from "../common/LoadingSpinner";
import { getUserInvitations, respondToInvitation } from "../../http/invitationsApi";
import { UserInvitation } from "../../domain/UserInvitation";
import { useInvitationContext } from "../../providers/InviteProvider";

export default function InvitationsPage() {
  const [invitations, setInvitations] = useState<UserInvitation[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const { triggerRefresh } = useInvitationContext();

  useEffect(() => {
    fetchInvitations();
  }, []);

  const fetchInvitations = async () => {
    try {
      setLoading(true);
      const data: UserInvitation[] = await getUserInvitations();
      setInvitations(data);
      setError(null);
    } catch (err: any) {
      setError(err.message || "Unknown error");
    } finally {
      setLoading(false);
    }
  };

  const respondInvitation = async (invitationId: string, accept: boolean) => {
    try {
      await respondToInvitation(invitationId, { status: accept ? "ACCEPTED" : "DECLINED" });
      fetchInvitations(); 
      triggerRefresh();
    } catch (err) {
      console.error("Error responding to invitation:" + err);
    }
  };

  if (loading) return <LoadingSpinner/>;
  if (error) return <p className="text-danger">Error: {error}</p>;
  return (
    <div className="container py-4">
      <h2>Project Invitations</h2>
      {invitations.length === 0 ? (
        <p>No invitations found.</p>
      ) : (
        <div className="table-responsive">
          <table className="table table-bordered table-hover align-middle">
            <thead className="table-light">
              <tr>
                <th>Project</th>
                <th>Description</th>
                <th>Last Updated</th>
                <th>Role</th>
                <th>Status</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {invitations.map((inv) => (
                <tr key={inv.id}>
                  <td>{inv.project.name}</td>
                  <td>{inv.project.description || <em>No description</em>}</td>
                  <td>{new Date(inv.project.updatedAt).toLocaleString()}</td>
                  <td>{inv.role}</td>
                  <td>{inv.status}</td>
                  <td>
                    {inv.status === "PENDING" ? (
                      <>
                        <button
                          className="btn btn-sm btn-success me-2"
                          onClick={() => respondInvitation(inv.id, true)}
                        >
                          Accept
                        </button>
                        <button
                          className="btn btn-sm btn-danger"
                          onClick={() => respondInvitation(inv.id, false)}
                        >
                          Decline
                        </button>
                      </>
                    ) : (
                      <em>No actions available</em>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}
