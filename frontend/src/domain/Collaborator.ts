import { User } from "./User";

export interface Collaborator {
  project_id: string;
  user: User;
  role: string;
}
