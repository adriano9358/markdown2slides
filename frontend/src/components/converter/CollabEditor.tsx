import React, { useEffect, useRef } from "react";
import { EditorView, basicSetup } from "codemirror";
import { EditorState, Text } from "@codemirror/state";
import { markdown } from "@codemirror/lang-markdown";
import { peerExtension } from "../../collab/peerExtension";
import { getDocument } from "../../collab/collabFunctions";
import { remoteCursorsField } from "../../collab/cursorField";
import { renderCursorsPlugin } from "../../collab/renderCursors";
import { MarkdownShortcutMenu } from "./MarkdownShortcutMenu";

interface CollabEditorProps {
  projectId: string;
  userId: string;
  edRef: React.MutableRefObject<() => string>;
  setMarkdown: (value: string) => void;
  isReadOnly: boolean;
}

export const CollabEditor: React.FC<CollabEditorProps> = ({ projectId, userId, edRef, setMarkdown, isReadOnly}) => {
  const editorContainerRef = useRef<HTMLDivElement | null>(null);
  const editorRef = useRef<EditorView | null>(null);

  useEffect(() => {
    if (!editorContainerRef.current) return;

    let editorView: EditorView;

    getDocument(projectId).then(({ version, doc }) => {
      setMarkdown(doc.toString());
      const state = EditorState.create({
        doc,
        extensions: [
          basicSetup,
          markdown(),
          EditorView.lineWrapping,
          EditorView.theme({
            "&": { maxHeight: "70vh" },
            ".cm-scroller": { overflow: "auto" },
          }),
          ...(isReadOnly ? [EditorState.readOnly.of(true)] : []),
          ...peerExtension(projectId, version, userId),
          remoteCursorsField,
          renderCursorsPlugin,
        ],
      });

      editorView = new EditorView({
        state,
        parent: editorContainerRef.current!,
      });

      editorRef.current = editorView;
      edRef.current = () => editorRef.current?.state.doc.toString() ?? ""
    });

    return () => {
      editorRef.current?.destroy();
    };
  }, [projectId]);

  return (
    <div>
      <MarkdownShortcutMenu
        editorRef={editorRef}
        projectId={projectId}
      />
      <div ref={editorContainerRef} className="codemirror-container" />
    </div>
  );
};
