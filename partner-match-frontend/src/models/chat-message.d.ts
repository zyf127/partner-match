import {UserType} from "./user";

/**
 * 聊天消息类别
 */
export type ChatMessageType = {
    id: number;
    fromUser: UserType;
    toId: number;
    messageContent: string;
    messageType:number;
    teamId: number;
    createTime: Date;
};