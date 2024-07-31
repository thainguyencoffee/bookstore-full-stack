import {createStore} from "vuex";
import BooksModule from "./modules/books.js";

const store = createStore({
    modules: {
        'books': BooksModule
    },
})

export default store;