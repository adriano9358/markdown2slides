import { useEffect, useRef, useState } from "react";
import Reveal from "reveal.js";
import "reveal.js/dist/reveal.css";
import "reveal.js/dist/theme/white.css";
import { useParams } from "react-router-dom";
import { getProjectContent } from "../http/projectContentApi";
import { convertProject } from "../http/conversionApi";

export const PrintSlides = () => {
    const { projectId } = useParams<{ projectId: string }>();
    const [slideContent, setSlideContent] = useState("");
    const deckRef = useRef<Reveal.Api | null>(null);
    

    const fetchContent = async () => {
        try {
            const md = await getProjectContent(projectId);
            const html = await convertProject(true, "Content:" + md);
            setSlideContent(html);
        } catch (err) {
            console.error("Failed to load slides", err);
        }
    };

    useEffect(() => {
        fetchContent();
    }, [projectId]);

    useEffect(() => {
        if (slideContent) {
            const deck = new Reveal();
            deck.initialize(
                { embedded: false, hash: false, history: false, controls: false, slideNumber: false, progress: false, plugins: [], view: "scroll", transition: "none"}
            ).then(() => {
                deckRef.current = deck;
                setTimeout(() => {
                    window.print();
                }, 300);
            });

            const link = document.createElement("link");
            link.rel = "stylesheet";
            link.href = "https://cdn.jsdelivr.net/npm/reveal.js@4.5.0/dist/print/pdf.css";
            link.id = "pdf-print-style";
            document.head.appendChild(link);
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

