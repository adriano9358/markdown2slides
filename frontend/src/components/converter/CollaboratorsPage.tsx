import React, { useEffect, useState, useContext } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { AuthContext } from "../../providers/AuthProvider";
import 'bootstrap-icons/font/bootstrap-icons.css';
import { Collaborator } from "../../domain/Collaborator";
import { Invitation } from "../../domain/Invitation";
import { SendInvitationData } from "../../domain/SendInvitationData";
import { getCollaborators } from "../../http/collaboratorsApi";
import { getProjectInvitations, sendNewInvitation } from "../../http/invitationsApi";



export default function CollaboratorsPage() {
  const { projectId } = useParams<{ projectId: string }>();
  const [collaborators, setCollaborators] = useState<Collaborator[]>([]);
  const [invitations, setInvitations] = useState<Invitation[]>([]);
  const [email, setEmail] = useState("");
  const [role, setRole] = useState("EDITOR");
  const [isOwner, setIsOwner] = useState(false);
  const { user } = useContext(AuthContext);
  const navigate = useNavigate();

  useEffect(() => {
    fetchCollaborators();
    fetchInvitations();
  }, [projectId]);

  const fetchCollaborators = async () => {
    getCollaborators(projectId)
      .then((data) => { 
        setCollaborators(data); 
        setIsOwner(data.some((c) => c.role === "ADMIN" && c.user.id === user?.id));
      })
      .catch((err) => console.error("Failed to fetch collaborators", err));
  };

  const fetchInvitations = async () => {
    getProjectInvitations(projectId)
      .then((data) => { 
        setInvitations(data); 
      })
      .catch((err) => console.error("Failed to fetch project invitations", err));
  };

  const sendInvitation = async (e: React.FormEvent) => {
    e.preventDefault();
    const sendData: SendInvitationData = {email: email, role: role} 
    sendNewInvitation(projectId, sendData)
      .then((data) => { 
        setEmail(""); 
        fetchInvitations();
      })
      .catch((err) => console.error("Failed to fetch project invitations", err));
  };

  const removeCollaborator = async (userId: string) => {
    // TODO: call backend endpoint to remove collaborator
    alert(`Removing collaborator with user id: ${userId}`);
  };

  const changeRole = async (userId: string, newRole: string) => {
    // TODO: call backend endpoint to update role
    alert(`Changing role for user ${userId} to ${newRole}`);
  };

  return (
    <div className="container py-4" >
      <div className="mb-3">
        <button
          className="btn btn-outline-secondary d-flex align-items-center"
          onClick={() => navigate(`/projects/${projectId}`)}
        >
          <i className="bi bi-arrow-left me-2"></i> Back to Project
        </button>
      </div>
      <h2 className="mb-4">Collaborators</h2>
      {/* Collaborators List */}
      <div className="table-responsive mb-5">
        <table className="table table-bordered table-hover">
          <thead className="table-light">
            <tr>
              <th>Name</th>
              <th>Email</th>
              <th>Role</th>
              {isOwner && <th>Actions</th>}
            </tr>
          </thead>
          <tbody>
            {collaborators.map((c) => (
                <tr key={c.user.id}>
                <td>{c.user.name}</td>
                <td>{c.user.email}</td>
                <td>{c.role}</td>
                {isOwner && c.role !== "ADMIN" ? (
                    <td>
                    <div className="d-flex gap-2">
                        <select
                        className="form-select form-select-sm w-auto"
                        value={c.role}
                        onChange={(e) => changeRole(c.user.id, e.target.value)}
                        >
                        <option value="OWNER">Owner</option>
                        <option value="EDITOR">Editor</option>
                        <option value="VIEWER">Viewer</option>
                        </select>
                        <button
                        className="btn btn-sm btn-danger"
                        onClick={() => removeCollaborator(c.user.id)}
                        >
                        Remove
                        </button>
                    </div>
                    </td>
                ) : (
                    <td></td>
                )}
                </tr>
            ))}
            </tbody>
        </table>
      </div>

      {/* Invite Section (owner only) */}
      {isOwner && (
        <>
          <h4>Invite New Collaborator</h4>
          <div className="card shadow-sm mb-4">
            <div className="card-body">
              <form onSubmit={sendInvitation} className="row g-3 align-items-end">
                <div className="col-md-5">
                  <label className="form-label">Email</label>
                  <input
                    type="email"
                    className="form-control"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    required
                  />
                </div>
                <div className="col-md-3">
                  <label className="form-label">Role</label>
                  <select
                    className="form-select"
                    value={role}
                    onChange={(e) => setRole(e.target.value)}
                  >
                    <option value="EDITOR">Editor</option>
                    <option value="VIEWER">Viewer</option>
                  </select>
                </div>
                <div className="col-md-4">
                  <button type="submit" className="btn btn-success w-100">
                    Send Invite
                  </button>
                </div>
              </form>
            </div>
          </div>

          <h4>Pending Invitations</h4>
          <div className="table-responsive">
            <table className="table table-bordered table-hover">
              <thead className="table-light">
                <tr>
                  <th>Email</th>
                  <th>Role</th>
                  <th>Status</th>
                </tr>
              </thead>
              <tbody>
                {invitations.map((inv) => (
                  <tr key={inv.id}>
                    <td>{inv.email}</td>
                    <td>{inv.role}</td>
                    <td>{inv.status}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </>
      )}
    </div>
  );
}
