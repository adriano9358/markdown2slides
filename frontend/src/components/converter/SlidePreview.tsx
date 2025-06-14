// File: components/MarkdownToSlides/SlidePreview.tsx
import React from "react";
import SlidesLoadingOverlay from "./SlidesLoadingOverlay";

interface SlidePreviewProps {
  slideContent: string;
  loading: boolean;
}


export const SlidePreview: React.FC<SlidePreviewProps> = ({ slideContent, loading }) => {
  return (
    <div className="d-flex flex-column p-3 h-100 position-relative">
      <div className="reveal flex-grow-1 d-flex justify-content-center align-items-center overflow-hidden">
        {slideContent && (
          <div
            className="slides"
            dangerouslySetInnerHTML={{ __html: slideContent }}
          />
        )}
      </div>
      {loading && <SlidesLoadingOverlay />}
    </div>
  );
};
