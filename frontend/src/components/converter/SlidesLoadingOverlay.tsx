export const SlidesLoadingOverlay = () => (
    <div className="position-absolute top-0 start-0 w-100 h-100 d-flex flex-column justify-content-center align-items-center bg-white bg-opacity-75 rounded shadow-lg" style={{ zIndex: 10 }}>
        <div className="spinner-border text-primary" role="status" style={{ width: "3rem", height: "3rem" }}></div>
        <div className="mt-3 fs-5 text-primary">Rendering slides...</div>
    </div>
);
