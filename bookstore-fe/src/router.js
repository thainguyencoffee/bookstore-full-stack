import {createRouter, createWebHistory} from "vue-router";
import BooksList from "./pages/BooksList.vue";
import BookDetail from "./pages/BookDetail.vue";

const router = createRouter({
    history: createWebHistory(),
    routes: [
        {name: 'books-list', path: '/', component: BooksList},
        {name: 'book-detail', path: '/books/:isbn', component: BookDetail, props: true}
    ],
    scrollBehavior(to, from, savedPosition) {
        if (savedPosition) {
            return savedPosition;
        } else {
            return {left: 0, top: 0};
        }
    }
})

export default router;