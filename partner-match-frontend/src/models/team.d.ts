import {UserType} from "./user";

/**
 * 队伍类别
 */
export type TeamType = {
    id: number;
    teamName: string;
    teamDescription: string;
    expireTime?: Date;
    maxNum:number;
    teamPassword?: string;
    teamStatus: number;
    createTime: Date;
    updateTime: Date;
    userList: UserType[]
};