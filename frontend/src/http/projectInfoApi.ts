import { request } from "./request";

export const getProjects = () => request<any>(`/projects`, {credentials:"include"});

export const createProject = (body: any) => request<any>(`/projects`, 
    {method: "POST",
    headers: { "Content-Type": "application/json" },
    credentials: "include",
    body: JSON.stringify(body),
})