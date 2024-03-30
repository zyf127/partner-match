/**
 * 用户类别
 */
export type UserType = {
    id: number;
    username: string;
    userAccount: string;
    avatarUrl: string;
    gender:number;
    phone: string;
    email: string;
    profile: string;
    tagNames: string[];
    userStatus: number;
    userRole: number;
    createTime: Date;
};