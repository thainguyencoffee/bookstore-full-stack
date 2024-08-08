import {createRouter, createWebHistory} from "vue-router";
import Login from "../views/LoginForm.vue";
import Logout from "../views/LogoutForm.vue";
import LoginError from "../views/LoginError.vue";
import AboutView from "../views/AboutView.vue";
import HomeView from "../views/HomeView.vue";

const routes = [
    {path: '/', component: HomeView},
    {path: '/about', component: AboutView},
    {path: '/login', component: Login},
    {path: '/logout', component: Logout},
    {path: '/login-error', component: LoginError}
]

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: routes
})

export default router;