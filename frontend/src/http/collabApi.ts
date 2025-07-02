import { CursorInfo } from "../domain/CursorInfo";
import { request } from "./request";
import { InitMessage } from "../domain/InitMessage";


export const getUpdates = (projectId: string, version: number) => request<any[]>(`/collab/${projectId}/updates/${version}`, {credentials:"include"});

export const sendUpdates = (projectId: string, version: number, body: any) => request<boolean>(`/collab/${projectId}/updates/${version}`, 
    {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        credentials:"include",
        body: JSON.stringify(body),
    }
);

export const getInitialDoc = (projectId: string) => request<InitMessage>(`/collab/${projectId}`, {credentials:"include"});

export const getCursors = (projectId: string, userId: string, body: CursorInfo) => request<OtherCursors[]>(`/collab/${projectId}/cursor/${userId}`,
    {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        credentials:"include",
        body: JSON.stringify(body),
    }
);

export interface OtherCursors{
    userId: string,
    cursor: CursorInfo
}