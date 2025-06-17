import React, { useEffect, useState } from "react";

interface ProjectBasicInfo {
  id: string;
  name: string;
  description: string | null;
  updatedAt: string; // ISO string
}

interface Invitation {
  id: string;
  project: ProjectBasicInfo;
  email: string;
  role: string;
  status: string; // "PENDING", "ACCEPTED", etc.
  invited_by: string | null;
  invited_at: string;
}

export default function InvitationsPage() {
  const [invitations, setInvitations] = useState<Invitation[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    fetchInvitations();
  }, []);

  const fetchInvitations = async () => {
    try {
      setLoading(true);
      const res = await fetch("http://localhost:8080/invitations", {
        credentials: "include",
      });
      if (!res.ok) throw new Error("Failed to fetch invitations");
      const data: Invitation[] = await res.json();
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
      const res = await fetch(
        `http://localhost:8080/invitations/${invitationId}/respond`,
        {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          credentials: "include",
          body: JSON.stringify({
            status: accept ? "ACCEPTED" : "DECLINED",
          }),
        }
      );
      if (!res.ok) throw new Error("Failed to respond to invitation");
      fetchInvitations(); // Refresh after action
    } catch (err) {
      alert("Error responding to invitation");
      console.error(err);
    }
  };

  if (loading) return <p>Loading invitations...</p>;
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
