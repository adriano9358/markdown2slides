import React from "react";
import { EditorView } from "@codemirror/view";
import { API_PREFIX, BACKEND_URL } from "../../http/request";
import { uploadImage } from "../../http/projectContentApi";

interface MarkdownShortcutMenuProps {
  editorRef: React.MutableRefObject<EditorView | null>;
  readOnly?: boolean;
  projectId: string;
}

export const MarkdownShortcutMenu: React.FC<MarkdownShortcutMenuProps> = ({
  editorRef,
  readOnly = false,
  projectId,
}) => {
  const insertMarkdownSyntax = (syntax: string, selectedText?: string) => {
    const view = editorRef.current;
    if (!view) return;

    const { from, to } = view.state.selection.main;
    let replacement = "";

    if (syntax === "image" && selectedText) {
      const fileName = selectedText.split("/").pop() || "image";
      const altText = fileName.replace(/\.[^/.]+$/, "");
      replacement = `![${altText}](${selectedText})\n`;
    } else {
      const patterns: Record<string, [string, string]> = {
        bold: ["**", "**"],
        italic: ["*", "*"],
        code: ["`", "`"],
        link: ["[", "](url)"],
        header: ["# ", ""],
        quote: ["> ", ""],
        ul: ["- ", ""],
        ol: ["1. ", ""],
      };
      const [prefix, suffix] = patterns[syntax] || ["", ""];
      const selected = view.state.sliceDoc(from, to) || selectedText || "text";
      replacement = prefix + selected + suffix;
    }

    view.dispatch({
      changes: { from, to, insert: replacement },
      selection: { anchor: from + replacement.length },
    });

    view.focus();
  };

  const handleImageUpload = async (event: React.ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files?.[0];
    if (!file || !projectId) return;

    const extension = file.name.split(".").pop();
    const baseName = file.name.replace(/\.[^/.]+$/, "");
    const imageName = baseName.replace(/\s+/g, "_");

    try {
      const arrayBuffer = await file.arrayBuffer();
      const response = await uploadImage(projectId, imageName, extension, arrayBuffer)
      if (!response.ok) throw new Error("Image upload failed");
      const imageUrl = `${BACKEND_URL}${API_PREFIX}/projects/content/${projectId}/images/${imageName}.${extension}`;
      insertMarkdownSyntax("image", imageUrl);
    } catch (error) {
      console.error("Error uploading image:", error);
    } finally {
      event.target.value = "";
    }
  };

  return (
    <div className="btn-toolbar mb-2 justify-content-center" role="toolbar">
      {["bold", "italic", "code", "link", "header", "quote", "ul", "ol"].map((type) => (
        <button
          key={type}
          className="btn btn-sm btn-outline-dark me-2"
          onClick={() => insertMarkdownSyntax(type)}
          disabled={readOnly}
        >
          {type.charAt(0).toUpperCase() + type.slice(1)}
        </button>
      ))}

      <input
        type="file"
        accept="image/*"
        style={{ display: "none" }}
        id="imageUploadInput"
        onChange={handleImageUpload}
        disabled={readOnly}
      />
      <label
        htmlFor="imageUploadInput"
        className={`btn btn-sm btn-outline-dark me-2 ${readOnly ? "disabled" : ""}`}
        style={{
          cursor: readOnly ? "not-allowed" : "pointer",
          pointerEvents: readOnly ? "none" : "auto"
        }}
      >
        Upload Image
      </label>
    </div>
  );
};

