// File: components/ProjectList.tsx
import React from "react";
import { Link } from "react-router-dom";

export type ProjectMetadata = {
  id: string;
  name: string;
  createdAt: string;
};

interface ProjectListProps {
  projects: ProjectMetadata[];
}

export const ProjectList: React.FC<ProjectListProps> = ({ projects }) => {
  if (projects.length === 0) {
    return <p>No projects found.</p>;
  }

  return (
    <ul className="list-group">
      {projects.map((proj) => (
        <li
          className="list-group-item d-flex justify-content-between align-items-center"
          key={proj.id}
        >
          <div>
            <strong>{proj.name}</strong>
            <br />
            <small className="text-muted">
              Created: {new Date(proj.createdAt).toLocaleDateString()}
            </small>
          </div>
          <Link to={`/projects/${proj.id}`} className="btn btn-sm btn-outline-primary">
            Open
          </Link>
        </li>
      ))}
    </ul>
  );
};
