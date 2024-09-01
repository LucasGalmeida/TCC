import { ChatHistoryEnum } from "./ChatHistoryEnum";

export interface ChatHistory {
    id?: number,
    type: ChatHistoryEnum,
    date: string,
    message: string,
}