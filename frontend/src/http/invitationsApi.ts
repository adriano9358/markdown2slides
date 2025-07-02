import { Invitation } from "../domain/Invitation";
import { InvitationId } from "../domain/InvitationId";
import { SendInvitationData } from "../domain/SendInvitationData";
import { request } from "./request";

export const getProjectInvitations = (projectId: string) => request<Invitation[]>(`/projects/${projectId}/invitations`, {credentials:"include"});

export const getUserInvitations = () => request<any[]>(`/invitations`, {credentials:"include"});

export const sendNewInvitation = (projectId: string, body: SendInvitationData) => request<InvitationId>(`/projects/${projectId}/invitations`, 
    {method: "POST",
    headers: { "Content-Type": "application/json" },
    credentials: "include",
    body: JSON.stringify(body),
});

export const respondToInvitation = (invitationId: string, body: any) => request<any>(`/invitations/${invitationId}/respond`,
{   
    method: "POST",
    headers: { "Content-Type": "application/json" },
    credentials: "include",
    body: JSON.stringify(body),
});