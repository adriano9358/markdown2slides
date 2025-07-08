import { request } from "./request";

export const convertProject = (standalone:boolean, theme:string, body: any) => request(`/convert` + (standalone ? "?standalone=true" : "" ) + (standalone ? "&theme=" + theme : "" ), 
    {
        method: "POST",
        credentials: "include",
        body: body,
    }, 
    false, true)