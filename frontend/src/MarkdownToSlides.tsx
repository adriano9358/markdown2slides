import { useState, useEffect, useRef } from "react";
import Reveal from "reveal.js";
import "reveal.js/dist/reveal.css";
import "reveal.js/dist/theme/white.css";
import Split from "react-split";
import { useParams } from "react-router-dom";


const MarkdownToSlides = () => {
    const deckRef = useRef<Reveal.Api | null>(null);
    const [markdown, setMarkdown] = useState("");
    const [autoConvert, setAutoConvert] = useState(false);
    const [slideContent, setSlideContent] = useState("");
    const [currentSlide, setCurrentSlide] = useState({ h: 0, v: 0 });
    const [loading, setLoading] = useState(true);
    const [theme, setTheme] = useState("white"); // default theme
    const { projectId } = useParams<{ projectId: string }>();

   
    const lastConvertedMarkdown = useRef("");
    const textareaRef = useRef<HTMLTextAreaElement | null>(null);

    //const contentId = "e7453f29-a228-4cc1-89c4-d25ad8eb95ed";
    const contentId = projectId!;

    if (!projectId) {
        return <div>Error: No project ID provided.</div>;
      }

    const fetchInitialContent = async () => {
        try {
            setLoading(true);
            const response = await fetch(`http://localhost:8080/projects/content/${contentId}`, {
                method: "GET",
                credentials: "include", 
            });
            if (!response.ok) throw new Error("Failed to fetch initial content");

            const text = await response.text();
            setMarkdown(text);
            await convertMarkdownToSlides(text);
        } catch (error) {
            console.error("Error fetching initial content:", error);
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
        } catch (error) {
            console.error("Error converting Markdown:", error);
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

    const insertMarkdownSyntax = (syntax: string, placeholder = "text") => {
        if (!textareaRef.current) return;
    
        const textarea = textareaRef.current;
        const start = textarea.selectionStart;
        const end = textarea.selectionEnd;
        const selectedText = textarea.value.substring(start, end) || placeholder;
    
        let formatted = selectedText;
        switch (syntax) {
            case "bold":
                formatted = `**${selectedText}**`;
                break;
            case "italic":
                formatted = `*${selectedText}*`;
                break;
            case "code":
                formatted = `\`${selectedText}\``;
                break;
            case "link":
                formatted = `[${selectedText}](https://)`;
                break;
            case "header":
                formatted = `# ${selectedText}`;
                break;
            case "quote":
                formatted = `> ${selectedText}`;
                break;
            case "ul":
                formatted = `- ${selectedText}`;
                break;
            case "ol":
                formatted = `1. ${selectedText}`;
                break;
            case "image":
                // Extract image name from the URL
                const urlParts = selectedText.split("/");
                const fileName = urlParts[urlParts.length - 1]; 
                const altText = fileName.replace(/\.[^/.]+$/, ""); // remove extension for alt text
                formatted = `![${altText}](${selectedText})\n`;
                break;
            default:
                break;
        }
    
        const newText =
            textarea.value.substring(0, start) +
            formatted +
            textarea.value.substring(end);
    
        textarea.value = newText;
        textarea.selectionStart = textarea.selectionEnd = start + formatted.length;
    
        setMarkdown(textarea.value);
    
        textarea.scrollTop = textarea.scrollTop;
    };
    

    const handleImageUpload = async (event: React.ChangeEvent<HTMLInputElement>) => {
        const file = event.target.files?.[0];
        if (!file) return;
    
        const extension = file.name.split(".").pop();
        const baseName = file.name.replace(/\.[^/.]+$/, "");
        const imageName = baseName.replace(/\s+/g, "_");
        
        try {
            const arrayBuffer = await file.arrayBuffer();
            const response = await fetch(
                `http://localhost:8080/projects/content/${contentId}/images/${imageName}.${extension}`,
                {
                    method: "POST",
                    credentials: "include",
                    headers: { "Content-Type": "application/octet-stream" },
                    body: arrayBuffer,
                }
            );
    
            if (!response.ok) throw new Error("Image upload failed");
    
            const imageUrl = `http://localhost:8080/projects/content/e7453f29-a228-4cc1-89c4-d25ad8eb95ed/images/${imageName}.${extension}`;
            insertMarkdownSyntax("image", imageUrl);
        } catch (error) {
            console.error("Error uploading image:", error);
        } finally {
            // Reset input
            event.target.value = "";
        }
    };
    

    useEffect(() => {
        fetchInitialContent();
    }, []);


    useEffect(() => {
        applyTheme(theme);
    }, [theme]);
    
    useEffect(() => {
        const style = document.createElement("style");
        /*style.innerHTML = `
            .reveal {
                font-size: 10pt !important;
                line-height: 1.3em !important;
            }
    
            .reveal pre code {
                font-size: 16pt !important;
                line-height: 1.2em !important;
            }

            .reveal section {
                position: relative; 
            }

            .reveal img[alt="isel_logo"] {
                width: 200px;
                position: absolute;
                top: 0px; 
                left: 0px;
            }
        `;*/
        style.innerHTML = `
        
        .slides {
            font-size: 0.75em;
        }
        .reveal ul {
            display: block;
        }
        .reveal ol {
            display: block;
        }
        
        img {
            max-height: 350px !important;
        }
        
        figcaption {
            font-size: 0.6em !important;
            font-style: italic !important;
        }
        
        .subtitle {
            font-style: italic !important;
        }
        
        .date {
            font-size: 0.75em !important;
        }
        `;
        document.head.appendChild(style);
    }, []);
    

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
            console.log("Rendered Slide Content", slideContent);
            try{ 
                deck.initialize({ embedded: true }).then(() => {
                    deckRef.current = deck;
                    deck.slide(currentSlide.h, currentSlide.v);
                    setTimeout(() => {
                        deck.layout();
                    }, 50);
                });
            } catch(e) {}
        }
    }, [slideContent]);

    

    return (
        <div className="container-fluid min-vh-100 w-100 d-flex flex-column p-3 bg-light">
            <div className="flex-grow-1 border rounded p-3 slide-editor-container d-flex flex-column">
                <div className="d-flex align-items-center flex-wrap gap-2 mb-3">
                    <button className="btn btn-sm btn-outline-primary" onClick={toggleFullscreen}>
                        Presentation Mode
                    </button>

                    {!autoConvert && (
                        <button className="btn btn-sm btn-outline-secondary" onClick={() => convertMarkdownToSlides()}>
                            Convert Markdown
                        </button>
                    )}

                    <div className="form-check form-switch m-0 d-flex align-items-center">
                        <input
                            className="form-check-input"
                            type="checkbox"
                            id="autoConvertSwitch"
                            checked={autoConvert}
                            onChange={() => setAutoConvert(!autoConvert)}
                            style={{ cursor: "pointer" }}
                        />
                        <label className="form-check-label ms-2" htmlFor="autoConvertSwitch">
                            Auto Convert
                        </label>
                    </div>

                    <select
                        className="form-select form-select-sm w-auto"
                        value={theme}
                        onChange={(e) => setTheme(e.target.value)}
                    >
                        <option value="black">Black</option>
                        <option value="white">White</option>
                        <option value="league">League</option>
                        <option value="beige">Beige</option>
                        <option value="night">Night</option>
                        <option value="serif">Serif</option>
                        <option value="simple">Simple</option>
                        <option value="solarized">Solarized</option>
                        <option value="moon">Moon</option>
                        <option value="dracula">Dracula</option>
                        <option value="sky">Sky</option>
                        <option value="blood">Blood</option>
                    </select>


                </div>

                <Split
                    className="d-flex flex-grow-1 w-100 gap-2 bg-light"
                    sizes={[50, 50]} // initial split percentage
                    minSize={300}
                    gutterSize={10}
                    direction="horizontal"
                >
                    {/* Markdown Editor */}
                    <div className="d-flex flex-column p-3 h-100">
                        {/* Toolbar */}
                        <div className="btn-toolbar mb-2" role="toolbar">
                            <button className="btn btn-sm btn-outline-dark me-2" onClick={() => insertMarkdownSyntax("bold")}>Bold</button>
                            <button className="btn btn-sm btn-outline-dark me-2" onClick={() => insertMarkdownSyntax("italic")}>Italic</button>
                            <button className="btn btn-sm btn-outline-dark me-2" onClick={() => insertMarkdownSyntax("code")}>Code</button>
                            <button className="btn btn-sm btn-outline-dark me-2" onClick={() => insertMarkdownSyntax("link")}>Link</button>
                            <button className="btn btn-sm btn-outline-dark me-2" onClick={() => insertMarkdownSyntax("header")}>H1</button>
                            <button className="btn btn-sm btn-outline-dark me-2" onClick={() => insertMarkdownSyntax("quote")}>Quote</button>
                            <button className="btn btn-sm btn-outline-dark me-2" onClick={() => insertMarkdownSyntax("ul")}>Bullet</button>
                            <button className="btn btn-sm btn-outline-dark" onClick={() => insertMarkdownSyntax("ol")}>Numbered</button>
                            <input
                                type="file"
                                accept="image/*"
                                style={{ display: "none" }}
                                id="imageUploadInput"
                                onChange={(e) => handleImageUpload(e)}
                            />
                            <label htmlFor="imageUploadInput" className="btn btn-sm btn-outline-dark me-2" style={{ cursor: "pointer" }}>
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

                    

                    {/* Slide Preview */}
                    <div className="d-flex flex-column p-3 h-100 position-relative">
                        <div className="reveal flex-grow-1 d-flex justify-content-center align-items-center overflow-hidden">
                            {slideContent && (
                                <div
                                    className="slides"
                                    dangerouslySetInnerHTML={{ __html: slideContent }}
                                />
                            )}
                        </div>
                        {loading && <LoadingOverlay />}
                    </div>
                </Split>
            </div>
        </div>
    );
};



const LoadingOverlay = () => (
    <div className="position-absolute top-0 start-0 w-100 h-100 d-flex flex-column justify-content-center align-items-center bg-white bg-opacity-75 rounded shadow-lg" style={{ zIndex: 10 }}>
        <div className="spinner-border text-primary" role="status" style={{ width: "3rem", height: "3rem" }}></div>
        <div className="mt-3 fs-5 text-primary">Rendering slides...</div>
    </div>
);


export default MarkdownToSlides;

