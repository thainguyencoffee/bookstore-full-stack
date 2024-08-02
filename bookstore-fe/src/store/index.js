import {createStore} from "vuex";
import BooksModule from "./modules/books.js";

const store = createStore({
    modules: {
        'books-module': BooksModule
    },
})

export default store;