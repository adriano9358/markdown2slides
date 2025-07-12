import { ProjectBasicInfo } from "./ProjectBasicInfo";

export interface UserInvitation {
  id: string;
  project: ProjectBasicInfo;
  email: string;
  role: string;
  status: string; 
  invited_by: string | null;
  invited_at: string;
}
