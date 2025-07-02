import React, { useEffect, useState } from "react";
import { ProjectList } from "../components/projectManager/ProjectList";
import { ProjectMetadata } from "../domain/ProjectMetadata";
import { LoadingSpinner } from "../components/common/LoadingSpinner";
import { createProject, getProjects } from "../http/projectInfoApi";

export default function MyProjects() {
  const [projects, setProjects] = useState<ProjectMetadata[]>([]);
  const [loading, setLoading] = useState(true);
  const [showForm, setShowForm] = useState(false);
  const [projectName, setProjectName] = useState("");
  const [projectDescription, setProjectDescription] = useState("");
  const [projectVisibility, setProjectVisibility] = useState(true);

  useEffect(() => {
    fetchProjects();
  }, []);

  const fetchProjects = async () => {
    try {
      const data = await getProjects();
      const sorted = data.sort((a:any, b:any) => new Date(b.updatedAt).getTime() - new Date(a.updatedAt).getTime());
      setProjects(sorted);
    } catch (error) {
      console.error("Error fetching projects:", error);
    } finally {
      setLoading(false);
    }
  };

  const handleCreateProject = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      await createProject({
        name: projectName,
        description: projectDescription,
        visibility: projectVisibility,
      });
      setProjectName("");
      setProjectDescription("");
      setProjectVisibility(true);
      setShowForm(false);
      fetchProjects();
    } catch (error) {
      console.error("Error creating project:", error);
    }
  };

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

            <div className="mb-2">
              <label className="form-label">Description</label>
              <textarea
                className="form-control"
                value={projectDescription}
                onChange={(e) => setProjectDescription(e.target.value)}
              />
            </div>

            <div className="mb-3">
              <label className="form-label">Visibility</label>
              <select
                className="form-select"
                value={projectVisibility ? "public" : "private"}
                onChange={(e) => setProjectVisibility(e.target.value === "public")}
              >
                <option value="public">Public</option>
                <option value="private">Private</option>
              </select>
            </div>

            <button type="submit" className="btn btn-primary me-2">
              Create
            </button>
            <button
              type="button"
              className="btn btn-secondary"
              onClick={() => setShowForm(false)}
            >
              Cancel
            </button>
          </form>
        </div>
      )}

      {loading ? <LoadingSpinner /> : <ProjectList projects={projects} />}
    </div>
  );
}
