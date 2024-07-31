import {createRouter, createWebHistory} from "vue-router";
import BooksList from "./pages/BooksList.vue";

const router = createRouter({
    history: createWebHistory(),
    routes: [
        { name: 'books-list', path: '/', component: BooksList }
    ]
})

export default router;