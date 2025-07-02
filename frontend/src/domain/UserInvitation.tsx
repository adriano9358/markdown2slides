import { ProjectBasicInfo } from "./ProjectBasicInfo";

export interface UserInvitation {
  id: string;
  project: ProjectBasicInfo;
  email: string;
  role: string;
  status: string; // "PENDING", "ACCEPTED", etc.
  invited_by: string | null;
  invited_at: string;
}
