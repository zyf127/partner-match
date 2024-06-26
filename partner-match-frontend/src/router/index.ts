import Index from "../pages/Index.vue";
import TeamPage from "../pages/TeamPage.vue";
import UserPage from "../pages/UserPage.vue";
import SearchPage from "../pages/SearchPage.vue";
import UserEditPage from "../pages/UserEditPage.vue";
import SearchResultPage from "../pages/SearchResultPage.vue";
import UserLoginPage from "../pages/UserLoginPage.vue";
import * as VueRouter from "vue-router";
import TeamAddPage from "../pages/TeamAddPage.vue";
import TeamDetailPage from "../pages/TeamDetailPage.vue";
import TeamUpdatePage from "../pages/TeamUpdatePage.vue";
import UserRegisterPage from "../pages/UserRegisterPage.vue";
import PublicChatPage from "../pages/PublicChatPage.vue";
import UserDetailPage from "../pages/UserDetailPage.vue";
import FriendsPage from "../pages/FriendsPage.vue";
import TeamChatPage from "../pages/TeamChatPage.vue";
import PrivateChatPage from "../pages/PrivateChatPage.vue";

export const routes = [
    {path: '/', title:'伙伴匹配', component: Index},
    {path: '/team', title:'找队伍', component: TeamPage},
    {path: '/user', title:'个人信息', component: UserPage},
    {path: '/search', title:'搜索', component: SearchPage},
    {path: '/user/register', title: '注册', component: UserRegisterPage},
    {path: '/user/login', title:'登录', component: UserLoginPage},
    {path: '/user/list', title:'搜索结果', component: SearchResultPage},
    {path: '/user/edit', title:'编辑信息', component: UserEditPage},
    {path: '/user/detail', title: '查看详情', component: UserDetailPage},
    {path: '/team/add', title:'创建队伍', component: TeamAddPage},
    {path: '/team/detail', title:'队伍详情', component: TeamDetailPage},
    {path: '/team/update', title:'更新队伍', component: TeamUpdatePage},
    {path: '/chat/public', title: '世界聊天', component: PublicChatPage},
    {path: '/chat/team', title: '队伍聊天', component: TeamChatPage},
    {path: '/chat/private', title: '私聊', component: PrivateChatPage},
    {path: '/friends', title: '好友', component: FriendsPage},
]

const router = VueRouter.createRouter({
    history: VueRouter.createWebHashHistory(),
    routes,
})

export default router;