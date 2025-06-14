// PrintSlides.tsx
import { useEffect, useRef, useState } from "react";
import Reveal from "reveal.js";
import "reveal.js/dist/reveal.css";
import "reveal.js/dist/theme/white.css";
import { useParams } from "react-router-dom";

const PrintSlides = () => {
    const { projectId } = useParams<{ projectId: string }>();
    const [slideContent, setSlideContent] = useState("");
    const deckRef = useRef<Reveal.Api | null>(null);

    useEffect(() => {
        const fetchContent = async () => {
            try {
                const response = await fetch(`http://localhost:8080/projects/content/${projectId}`, {
                    credentials: "include",
                });
                const md = await response.text();
                const convertResp = await fetch("http://localhost:8080/convert?standalone=true", {
                    method: "POST",
                    credentials: "include",
                    headers: { "Content-Type": "text/plain" },
                    body: "Content:" + md,
                });
                const html = await convertResp.text();
                setSlideContent(html);
            } catch (err) {
                console.error("Failed to load slides", err);
            }
        };

        fetchContent();
    }, [projectId]);

    useEffect(() => {
        if (slideContent) {
            const deck = new Reveal();
            deck.initialize({
                embedded: false,
                hash: false,
                history: false,
                controls: false,
                slideNumber: false,
                progress: false,
                plugins: [],
                view: "scroll", // Reveal.js 4.6+ only
                transition: "none",
            }).then(() => {
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

export default PrintSlides;
