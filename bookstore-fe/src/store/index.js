import { createStore } from 'vuex';
import books from './modules/books';
import categories from './modules/categories';

const store = createStore({
    modules: {
        books,
        categories
    }
});

export default store;
