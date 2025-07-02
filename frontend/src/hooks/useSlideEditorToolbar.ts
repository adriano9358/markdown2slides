import { useEffect, useState } from "react";

export const useSlideEditorToolbar = () => {
    const [theme, setTheme] = useState("white");

    const toggleFullScreen = () => {
        const element = document.querySelector(".reveal");
        if (!document.fullscreenElement) {
          element?.requestFullscreen();
        } else {
          document.exitFullscreen();
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

    useEffect(() => {
        applyTheme(theme);
    }, [theme]);

    return {
        theme,
        setTheme,
        toggleFullScreen
    };
}