import React, { useContext } from "react";
import { Link } from "react-router-dom";
import { AuthContext } from "../../providers/AuthProvider";


export type ProjectMetadata = {
  id: string;
  name: string;
  createdAt: string;
  ownerId: string;
};

interface ProjectListProps {
  projects: ProjectMetadata[];
}

export const ProjectList: React.FC<ProjectListProps> = ({ projects }) => {
  const { user } = useContext(AuthContext);
  const userId = user?.id;

  if (!userId) return <p>User not authenticated.</p>;
  if (projects.length === 0) return <p>No projects found.</p>;

  const owned = projects.filter((p) => p.ownerId === userId);
  const collaborating = projects.filter((p) => p.ownerId !== userId);

  return (
    <>
      {owned.length > 0 && (
        <>
          <h5>Your Projects</h5>
          <ul className="list-group mb-4">
            {owned.map((proj) => (
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
        </>
      )}

      {collaborating.length > 0 && (
        <>
          <h5>Collaborations</h5>
          <ul className="list-group">
            {collaborating.map((proj) => (
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
        </>
      )}

      {owned.length === 0 && collaborating.length === 0 && <p>No projects found.</p>}
    </>
  );
};
