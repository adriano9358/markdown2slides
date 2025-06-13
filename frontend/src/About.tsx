import React from "react";

export function About() {
  return (
    <div className="container mt-4">
      <div className="card shadow-sm">
        <div className="card-body">
          <h2 className="card-title">About This App</h2>
          <p className="card-text">
            This app allows you to create, edit, and present slides from Markdown content. It's built with React, Bootstrap, and Reveal.js to give you a powerful and easy-to-use presentation tool.
          </p>
          <p className="card-text">
            You can upload images, format text, and organize your slides with ease. The app is designed to help developers and presenters quickly convert Markdown into dynamic slides.
          </p>
        </div>
      </div>
    </div>
  );
}
