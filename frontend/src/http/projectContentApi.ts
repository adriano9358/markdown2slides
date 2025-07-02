import { API_PREFIX, BACKEND_URL, request} from "./request";

export const getProjectContent = (projectId: string) => request(`/projects/content/${projectId}`, {credentials:"include"}, false, true);

export const uploadImage = (projectId: string, imageName: string, extension: string, arrayBuffer: ArrayBuffer) => fetch(
    `${BACKEND_URL}${API_PREFIX}/projects/content/${projectId}/images/${imageName}.${extension}`,
    {
        method: "POST",
        credentials: "include",
        headers: { "Content-Type": "application/octet-stream" },
        body: arrayBuffer,
    }
);