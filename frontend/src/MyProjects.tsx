import React, { useEffect, useState, useContext } from "react";
import { AuthContext } from "./AuthProvider";
import { Link } from "react-router-dom";

type ProjectMetadata = {
  id: string;
  name: string;
  createdAt: string;
};

export default function MyProjects() {
  const [projects, setProjects] = useState<ProjectMetadata[]>([]);
  const [loading, setLoading] = useState(true);
  const [showForm, setShowForm] = useState(false);
  const [projectName, setProjectName] = useState("");
  const { username } = useContext(AuthContext);

  useEffect(() => {
    fetchProjects();
  }, []);

  const fetchProjects = async () => {
    try {
      const response = await fetch("http://localhost:8080/projects", {
        credentials: "include"
      });
      if (!response.ok) throw new Error("Failed to fetch projects");
      const data = await response.json();
      setProjects(data);
    } catch (error) {
      console.error("Error fetching projects:", error);
    } finally {
      setLoading(false);
    }
  };

  const handleCreateProject = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      const response = await fetch("http://localhost:8080/projects", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        credentials: "include",
        body: JSON.stringify({ name: projectName, description: "descriptin", visibility: true })
      });
      if (!response.ok) throw new Error("Failed to create project");
      setProjectName("");
      setShowForm(false);
      fetchProjects();
    } catch (error) {
      console.error("Error creating project:", error);
    }
  };

  if (loading) return <div>Loading projects...</div>;

  return (
    <div className="container p-3">
      <h2>My Projects</h2>
      <button className="btn btn-success mb-3" onClick={() => setShowForm(true)}>
        + Create Project
      </button>

      {showForm && (
        <div className="mb-3 border rounded p-3 bg-light">
          <form onSubmit={handleCreateProject}>
            <div className="mb-2">
              <label className="form-label">Project Name</label>
              <input
                type="text"
                className="form-control"
                value={projectName}
                onChange={(e) => setProjectName(e.target.value)}
                required
              />
            </div>
            <button type="submit" className="btn btn-primary me-2">
              Create
            </button>
            <button type="button" className="btn btn-secondary" onClick={() => setShowForm(false)}>
              Cancel
            </button>
          </form>
        </div>
      )}

      {projects.length === 0 ? (
        <p>No projects found.</p>
      ) : (
        <ul className="list-group">
          {projects.map((proj) => (
            <li className="list-group-item d-flex justify-content-between align-items-center" key={proj.id}>
              <span>{proj.name}</span>
              <Link to={`/projects/${proj.id}`} className="btn btn-sm btn-outline-primary">
                Open
              </Link>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}
