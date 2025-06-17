// File: components/MarkdownToSlides/SlideEditorToolbar.tsx
import React from "react";
import { useNavigate } from "react-router-dom";

interface ToolbarProps {
  toggleFullscreen: () => void;
  autoConvert: boolean;
  setAutoConvert: (val: boolean) => void;
  convertMarkdownToSlides: () => void;
  setTheme: (val: string) => void;
  theme: string;
  contentId: string;
}

export const SlideEditorToolbar = ({
  toggleFullscreen,
  autoConvert,
  setAutoConvert,
  convertMarkdownToSlides,
  setTheme,
  theme,
  contentId,
}: ToolbarProps) => {
  const navigate = useNavigate();

  return (
    <div className="slide-toolbar d-flex justify-content-between align-items-center mb-2 flex-wrap gap-2">
      {/* Left buttons group */}
      <div className="d-flex gap-2 flex-wrap align-items-center">
        <button
          className="btn btn-sm btn-outline-primary"
          onClick={toggleFullscreen}
        >
          Presentation Mode
        </button>

        <button
          className="btn btn-sm btn-outline-secondary"
          onClick={() =>
            window.open(`/projects/${contentId}/print?print-pdf`, "_blank")
          }
        >
          Export / Print
        </button>
      </div>

      {/* Centered Convert button */}
      {!autoConvert && (
        <div className="flex-grow-1 d-flex justify-content-center">
          <button
            className="btn btn-success px-4"
            onClick={() => convertMarkdownToSlides()}
          >
            Convert
          </button>
        </div>
      )}

      {/* Right controls */}
      <div className="d-flex gap-3 align-items-center flex-wrap">
        {/*<div className="form-check form-switch m-0 d-flex align-items-center">
          <input
            className="form-check-input"
            type="checkbox"
            id="autoConvertSwitch"
            checked={autoConvert}
            onChange={() => setAutoConvert(!autoConvert)}
            style={{ cursor: "pointer" }}
          />
          <label
            className="form-check-label ms-2"
            htmlFor="autoConvertSwitch"
          >
            Auto Convert
          </label>
      </div>*/}

        <select
          className="form-select form-select-sm w-auto"
          value={theme}
          onChange={(e) => setTheme(e.target.value)}
        >
          <option value="black">Black</option>
          <option value="white">White</option>
          <option value="league">League</option>
          <option value="beige">Beige</option>
          <option value="night">Night</option>
          <option value="serif">Serif</option>
          <option value="simple">Simple</option>
          <option value="solarized">Solarized</option>
          <option value="moon">Moon</option>
          <option value="dracula">Dracula</option>
          <option value="sky">Sky</option>
          <option value="blood">Blood</option>
        </select>

        {/* Collaborators button */}
        <button
          className="btn btn-sm btn-outline-dark"
          onClick={() => navigate(`/projects/${contentId}/collaborators`)}
        >
          Manage Collaborators
        </button>
      </div>
    </div>
  );
};
