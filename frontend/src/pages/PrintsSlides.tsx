import { useEffect, useRef, useState } from "react";
import Reveal from "reveal.js";
import "reveal.js/dist/reveal.css";
import { useParams } from "react-router-dom";
import { getProjectContent } from "../http/projectContentApi";
import { convertProject } from "../http/conversionApi";

export const PrintSlides = () => {
    const { projectId, theme } = useParams<{ projectId: string, theme: string }>();
    const [slideContent, setSlideContent] = useState("");
    const deckRef = useRef<Reveal.Api | null>(null);
    
    const fetchContent = async () => {
        try {
            const md = await getProjectContent(projectId);
            const html = await convertProject(true, theme, "Content:" + md);
            setSlideContent(html);
        } catch (err) {
            console.error("Failed to load slides", err);
        }
    };

    const onThemeLoad = () => {
        const deck = new Reveal();
        deck.initialize(
            { embedded: false, hash: false, history: false, controls: false, slideNumber: false, progress: false, plugins: [], view: "scroll", transition: "none"}
        ).then(() => {
            //alert("To export as PDF, open the in-browser print menu with (CTRL+P)");
            deckRef.current = deck;
            setTimeout(() => {
                window.print();
            }, 300);
        });
    };

    useEffect(() => {
        fetchContent();
    }, [projectId]);

    useEffect(() => {
        if (slideContent) {
            const themeLink = document.querySelector('link#theme') as HTMLLinkElement;

            if (themeLink && themeLink.sheet) {
                onThemeLoad();
            } else if (themeLink) {
                themeLink.addEventListener("load", onThemeLoad);
            } else {
                console.warn("No theme link found, using default theme.");
                onThemeLoad();
            }

            return () => {
                if (themeLink) {
                    themeLink.removeEventListener("load", onThemeLoad);
                }
            };
        }
    }, [slideContent]);

    return (
        <div className="reveal" style={{ height: "100vh", overflow: "auto" }}>
            <div
                className="slides"
                dangerouslySetInnerHTML={{ __html: slideContent }}
            />
        </div>
    );
};

