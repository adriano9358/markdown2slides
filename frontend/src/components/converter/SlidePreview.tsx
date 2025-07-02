import React, { useEffect, useRef } from "react";
import Reveal from "reveal.js";
import { SlidesLoadingOverlay } from "./SlidesLoadingOverlay";

interface SlidePreviewProps {
  slideContent: string;
  loading: boolean;
}


export const SlidePreview: React.FC<SlidePreviewProps> = ({ slideContent, loading }) => {
  const deckRef = useRef<Reveal.Api | null>(null);
  const deckDivRef = useRef<HTMLDivElement>(null);
  const slidesRef = useRef<HTMLDivElement>(null);
  const lastSlideRef = useRef<{ h: number; v: number }>({ h: 0, v: 0 });

  useEffect(() => {
    if (!slideContent || !deckDivRef.current || !slidesRef.current) return;
    slidesRef.current.innerHTML = slideContent;
    if (!deckRef.current) {
      const deck = new Reveal(deckDivRef.current, {
        embedded: true,
      });

      deck.initialize().then(() => {
        deckRef.current = deck;
        deck.slide(lastSlideRef.current.h, lastSlideRef.current.v);
        setTimeout(() => deck.layout(), 30);
      }).catch(err => console.error("Reveal init failed:", err));
      
    } else {
      const { h, v } = deckRef.current.getIndices();
      lastSlideRef.current = { h, v };
      deckRef.current.slide(h, v);
      setTimeout(() => { deckRef.current?.layout(); }, 30);
    }
  }, [slideContent]);

  useEffect(() => {
    return () => {
      try {
        deckRef.current?.destroy();
        deckRef.current = null;
      } catch (e) {
        console.warn("Reveal.js destroy failed:", e);
      }
    };
  }, []);

  return (
    <div className="d-flex flex-column p-3 h-100 position-relative">
      <div className="reveal flex-grow-1 d-flex justify-content-center align-items-center overflow-hidden" ref={deckDivRef}>
        <div className="slides" ref={slidesRef}></div>
      </div>
      {loading && <SlidesLoadingOverlay />}
    </div>
  );
};
