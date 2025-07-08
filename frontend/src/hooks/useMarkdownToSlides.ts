import { useState, useEffect, useRef } from "react";
import { getUserRole } from "../http/collaboratorsApi";
import { convertProject } from "../http/conversionApi";

export const useMarkdownToSlides = (ref: React.MutableRefObject<() => string>, projectId: string) => {
  const [markdown, setMarkdown] = useState("");
  const [slideContent, setSlideContent] = useState("");
  const [loading, setLoading] = useState(true); 
  const [role, setRole] = useState<string | null>(null);
  const [initialConversion, setInitialConversion] = useState(true);


  const convertMarkdownToSlides = async (md?: string) => {
    try {
      setLoading(true);
      const contentToConvert = ref.current()
      const html = await convertProject(false, "white", "Content:" + contentToConvert);
      setSlideContent(html);
    } catch (e) {
      console.error("Markdown conversion error:", e);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if(initialConversion && markdown.length > 2){
      setInitialConversion(false);
      convertMarkdownToSlides();
    }
  }, [markdown]);

  useEffect(() => {
    if (!projectId) return;

    getUserRole(projectId)
      .then((data) => setRole(data.role))
      .catch((err) => console.error("Failed to fetch role", err));
  }, [projectId]);

  return {
    markdown, 
    setMarkdown, 
    slideContent,
    loading,
    convertMarkdownToSlides, 
    role
  };
};
