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

const routes = [
    { path: '/', component: Index },
    { path: '/team', component: TeamPage },
    {path: '/user', component: UserPage},
    {path: '/search', component: SearchPage},
    {path: '/user/login', component: UserLoginPage},
    {path: '/user/list', component: SearchResultPage},
    {path: '/user/edit', component: UserEditPage},
    {path: '/team/add', component: TeamAddPage},
    {path: '/team/detail', component: TeamDetailPage}
]

const router = VueRouter.createRouter({
    history: VueRouter.createWebHashHistory(),
    routes,
})

export default router;