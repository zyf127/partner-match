import Index from "../pages/Index.vue";
import TeamPage from "../pages/TeamPage.vue";
import UserPage from "../pages/UserPage.vue";
import SearchPage from "../pages/SearchPage.vue";
import UserEditPage from "../pages/UserEditPage.vue";
import SearchResultPage from "../pages/SearchResultPage.vue";


const routes = [
    { path: '/', component: Index },
    { path: '/team', component: TeamPage },
    {path: '/user', component: UserPage},
    {path: '/search', component: SearchPage},
    {path: '/user/list', component: SearchResultPage},
    {path: '/user/edit', component: UserEditPage},
]

export default routes;