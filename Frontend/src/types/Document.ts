import { User } from "./User";

export interface Document {
    id: number,
    name: string,
    originalFileName: string,
    description?: string,
    processed: boolean,
    type: string,
    user: User
    dateUpload: string,
}