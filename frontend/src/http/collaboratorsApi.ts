import { Collaborator } from "../domain/Collaborator";
import { Role } from "../domain/Role";
import { request } from "./request";


export const getUserRole = (projectId: string) => request<Role>(`/projects/${projectId}/role`, {credentials:"include"});

export const getCollaborators = (projectId: string) => request<Collaborator[]>(`/projects/${projectId}/collaborators`, {credentials:"include"});