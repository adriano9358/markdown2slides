import { useParams } from "react-router-dom";
import { useMarkdownToSlides } from "../../hooks/useMarkdownToSlides";
import { SlideEditorToolbar } from "./SlideEditorToolbar";
import { SlidePreview } from "./SlidePreview";
import Split from "react-split";
import { useContext, useRef } from "react";
import { AuthContext } from "../../providers/AuthProvider";
import { CollabEditor } from "./CollabEditor";

export const ConvertWorkspace = () => {
  const { projectId } = useParams<{ projectId: string }>();
  const editorRef = useRef<() => string>(() => "")
  const {markdown, setMarkdown, slideContent, loading, convertMarkdownToSlides, role} = useMarkdownToSlides(editorRef, projectId);
  const { user } = useContext(AuthContext);
  

  const isReadOnly = role === "VIEWER";
  

  if (!projectId) return <div>Error: No project ID provided.</div>;
  if (!role) return <div>Loading role...</div>;

  
  return (
    <div className="w-100 h-100 d-flex flex-column bg-light overflow-hidden">
      <div className="flex-grow-1 border rounded p-3 slide-editor-container d-flex flex-column overflow-hidden">
        <SlideEditorToolbar
          convertMarkdownToSlides={convertMarkdownToSlides}
          projectId={projectId}
        />

        <Split
          className="editor-wrapper d-flex flex-grow-1 w-100 gap-2 bg-light"
          sizes={[50, 50]}
          minSize={300}
          gutterSize={10}
          direction="horizontal"
        >
          <div className="d-flex flex-column flex-grow-1 h-100" style={{ minHeight: 0 }}> 
            <div className="d-flex flex-column flex-grow-1" style={{ minHeight: 0 }}>
              <CollabEditor projectId={projectId} userId={user.id} edRef={editorRef} setMarkdown={setMarkdown}/>
            </div>
          </div>

          <div className="d-flex flex-column flex-grow-1 h-100">
            <SlidePreview 
              slideContent={slideContent} 
              loading={loading} 
            />
          </div>
        </Split>
      </div>
    </div>
  );
};

export default ConvertWorkspace;
