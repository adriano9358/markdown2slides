// File: components/MarkdownToSlides/useMarkdownToSlides.ts
import { useState, useEffect, useRef } from "react";
import Reveal from "reveal.js";

export const useMarkdownToSlides = (projectId?: string) => {
  const deckRef = useRef<Reveal.Api | null>(null);
  const textareaRef = useRef<HTMLTextAreaElement | null>(null);

  const [markdown, setMarkdown] = useState("");
  const [autoConvert, setAutoConvert] = useState(false);
  const [slideContent, setSlideContent] = useState("");
  const [currentSlide, setCurrentSlide] = useState({ h: 0, v: 0 });
  const [loading, setLoading] = useState(true);
  const [theme, setTheme] = useState("white");

  const lastConvertedMarkdown = useRef("");

  const contentId = projectId!;

  const fetchInitialContent = async () => {
    try {
      setLoading(true);
      const res = await fetch(`http://localhost:8080/projects/content/${contentId}`, {
        method: "GET",
        credentials: "include",
      });
      if (!res.ok) throw new Error("Failed to fetch initial content");
      const text = await res.text();
      setMarkdown(text);
      await convertMarkdownToSlides(text);
    } catch (e) {
      console.error("Error fetching content:", e);
    } finally {
      setLoading(false);
    }
  };

  const applyTheme = (themeName: string) => {
    let existingLink = document.getElementById("reveal-theme") as HTMLLinkElement | null;
    if (existingLink) {
      existingLink.href = `https://cdn.jsdelivr.net/npm/reveal.js@4.5.0/dist/theme/${themeName}.css`;
    } else {
      const link = document.createElement("link");
      link.rel = "stylesheet";
      link.id = "reveal-theme";
      link.href = `https://cdn.jsdelivr.net/npm/reveal.js@4.5.0/dist/theme/${themeName}.css`;
      document.head.appendChild(link);
    }
  };

  const convertMarkdownToSlides = async (md?: string) => {
    try {
      setLoading(true);
      setSlideContent("");
      if (deckRef.current) {
        const indices = deckRef.current.getIndices();
        setCurrentSlide({ h: indices.h, v: indices.v });
      }

      const contentToConvert = md ?? markdown;

      console.log(contentToConvert)

      const response = await fetch("http://localhost:8080/convert", {
        method: "POST",
        credentials: "include",
        headers: { "Content-Type": "text/plain" },
        body: "Content:" + contentToConvert,
      });

      if (!response.ok) throw new Error("Failed to convert Markdown");
      const html = await response.text();
      setSlideContent(html);
      lastConvertedMarkdown.current = contentToConvert;
    } catch (e) {
      console.error("Markdown conversion error:", e);
    } finally {
      setLoading(false);
    }
  };

  const toggleFullscreen = () => {
    const element = document.querySelector(".reveal");
    if (!document.fullscreenElement) {
      element?.requestFullscreen();
    } else {
      document.exitFullscreen();
    }
  };

  useEffect(() => {
    fetchInitialContent();
  }, []);

  useEffect(() => {
    applyTheme(theme);
  }, [theme]);

  useEffect(() => {
    if (!autoConvert) return;
    const timeout = setTimeout(() => {
      if (markdown !== lastConvertedMarkdown.current) {
        convertMarkdownToSlides();
        lastConvertedMarkdown.current = markdown;
      }
    }, 3000);
    return () => clearTimeout(timeout);
  }, [markdown, autoConvert]);

  useEffect(() => {
    if (slideContent) {
      const deck = new Reveal();
      try {
        deck.initialize({ embedded: true }).then(() => {
          deckRef.current = deck;
          deck.slide(currentSlide.h, currentSlide.v);
          setTimeout(() => {
            deck.layout();
          }, 50);
        });
      } catch (e) {
        console.error("Reveal initialization error:", e);
      }
    }
  }, [slideContent]);

  return {
    markdown,
    setMarkdown,
    autoConvert,
    setAutoConvert,
    slideContent,
    loading,
    theme,
    setTheme,
    textareaRef,
    convertMarkdownToSlides,
    toggleFullscreen,
    contentId,
  };
};
