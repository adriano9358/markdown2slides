import { BACKEND_URL, request } from "./request";

export const getUserInfo = () => request<any>(`/user`, {credentials:"include"});

export const logOut = () => fetch(`${BACKEND_URL}/logout`, { credentials: "include"});