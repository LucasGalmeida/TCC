import { User } from "./User";

export interface Document {
    id: number,
    name: string,
    description?: string,
    processed: boolean,
    type: string,
    user: User
    dateUpload: string,
}