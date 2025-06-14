// File: components/MarkdownToSlides/index.tsx
import { useParams } from "react-router-dom";
import { useMarkdownToSlides } from "../../hooks/useMarkdownToSlides";
import { SlideEditorToolbar } from "./SlideEditorToolbar";
import { SlidePreview } from "./SlidePreview";
import { MarkdownEditor } from "./MarkdownEditor";
import Split from "react-split";

const ConvertWorkspace = () => {
  const { projectId } = useParams<{ projectId: string }>();
  const editor = useMarkdownToSlides(projectId);

  if (!projectId) return <div>Error: No project ID provided.</div>;

  return (
    <div className="w-100 h-100 d-flex flex-column bg-light overflow-hidden">
      <div className="flex-grow-1 border rounded p-3 slide-editor-container d-flex flex-column overflow-hidden">
        <SlideEditorToolbar {...editor} />

        <Split
          className="d-flex flex-grow-1 w-100 gap-2 bg-light"
          sizes={[50, 50]}
          minSize={300}
          gutterSize={10}
          direction="horizontal"
        >
          <MarkdownEditor
            markdown={editor.markdown}
            setMarkdown={editor.setMarkdown}
            projectId={projectId}
          />
          <SlidePreview slideContent={editor.slideContent} loading={editor.loading} />
        </Split>
      </div>
    </div>
  );
};

export default ConvertWorkspace;
