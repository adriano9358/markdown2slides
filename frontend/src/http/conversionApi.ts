import { request } from "./request";

export const convertProject = (standalone:boolean, body: any) => request(`/convert` + (standalone ? "?standalone=true" : ""), 
    {
        method: "POST",
        credentials: "include",
        body: body,
    }, 
    false, true)