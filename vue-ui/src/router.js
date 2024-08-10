import {createRouter, createWebHistory} from 'vue-router';

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: '/', component: () => import('./views/HomeView.vue')
        },
        {
            path: '/profile', component: () => import('./views/ProfileView.vue')
        }
    ],
    scrollBehavior(_, _2, savePosition) {
        if (savePosition) {
            return savePosition;
        }
        return {top: 0, left: 0};
    }
})

export default router;