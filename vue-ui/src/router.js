import {createRouter, createWebHistory} from 'vue-router';

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: '/', component: () => import('./views/HomeView.vue')
        },
        {
            name: 'bookDetail', path: '/books/:isbn', component: () => import('./views/BookDetail.vue')
        },
        {
            path: '/profile', component: () => import('./views/ProfileView.vue')
        },
        {
            name: 'emailPreferences', path: '/email-preferences', component: () => import('./views/EmailPreferences.vue')
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