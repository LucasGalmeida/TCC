import { ChatHistory } from "./ChatHistory";
import { User } from "./User";

export interface Chat {
    id: number,
    title: string,
    user: User,
    chatHistory: ChatHistory[]
}