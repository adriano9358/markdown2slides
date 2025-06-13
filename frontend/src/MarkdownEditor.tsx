import React, { useRef } from "react";

interface MarkdownEditorProps {
  markdown: string;
  setMarkdown: (value: string) => void;
  projectId: string;
}

export const MarkdownEditor: React.FC<MarkdownEditorProps> = ({
  markdown,
  setMarkdown,
  projectId,
}) => {
  const textareaRef = useRef<HTMLTextAreaElement | null>(null);

  const insertMarkdownSyntax = (syntax: string, selectedText?: string) => {
    if (!textareaRef.current) return;

    const textarea = textareaRef.current;
    const start = textarea.selectionStart;
    const end = textarea.selectionEnd;

    let replacement = "";

    if (syntax === "image" && selectedText) {
      const urlParts = selectedText.split("/");
      const fileName = urlParts[urlParts.length - 1];
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
      const selected = textarea.value.substring(start, end) || selectedText || "text";
      replacement = prefix + selected + suffix;
    }

    const before = markdown.slice(0, start);
    const after = markdown.slice(end);
    const newValue = before + replacement + after;

    setMarkdown(newValue);

    setTimeout(() => {
      textarea.focus();
      textarea.selectionStart = textarea.selectionEnd = start + replacement.length;
    }, 0);
  };

  const onInsertSyntax = (syntax: string) => {
    insertMarkdownSyntax(syntax);
  };

  const handleImageUpload = async (event: React.ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files?.[0];
    if (!file || !projectId) return;

    const extension = file.name.split(".").pop();
    const baseName = file.name.replace(/\.[^/.]+$/, "");
    const imageName = baseName.replace(/\s+/g, "_");

    try {
      const arrayBuffer = await file.arrayBuffer();
      const response = await fetch(
        `http://localhost:8080/projects/content/${projectId}/images/${imageName}.${extension}`,
        {
          method: "POST",
          credentials: "include",
          headers: { "Content-Type": "application/octet-stream" },
          body: arrayBuffer,
        }
      );

      if (!response.ok) throw new Error("Image upload failed");

      const imageUrl = `http://localhost:8080/projects/content/${projectId}/images/${imageName}.${extension}`;
      insertMarkdownSyntax("image", imageUrl);
    } catch (error) {
      console.error("Error uploading image:", error);
    } finally {
      event.target.value = "";
    }
  };

  return (
    <div className="d-flex flex-column p-3 h-100">
      <div className="btn-toolbar mb-2" role="toolbar">
        {["bold", "italic", "code", "link", "header", "quote", "ul", "ol"].map((type) => (
          <button
            key={type}
            className="btn btn-sm btn-outline-dark me-2"
            onClick={() => onInsertSyntax(type)}
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
        />
        <label
          htmlFor="imageUploadInput"
          className="btn btn-sm btn-outline-dark me-2"
          style={{ cursor: "pointer" }}
        >
          Upload Image
        </label>
      </div>

      <textarea
        ref={textareaRef}
        className="form-control flex-grow-1"
        value={markdown}
        onChange={(e) => setMarkdown(e.target.value)}
      />
    </div>
  );
};
