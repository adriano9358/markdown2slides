import * as React from 'react';
import { Link } from 'react-router-dom';

export function Home() {
  return (
    <div className="container py-5 text-center">
      <h1 className="display-4 mb-4">Welcome to markdown2slides</h1>
      <p className="lead mb-4">
        Turn your Markdown notes into professional presentation slides right in your browser.
      </p>

      <div className="d-flex justify-content-center gap-3">
        <Link to="/projects" className="btn btn-primary btn-lg">
          My Projects
        </Link>
        <Link to="/about" className="btn btn-outline-secondary btn-lg">
          Learn More
        </Link>
      </div>

    </div>
  );
}
