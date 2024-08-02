import {createStore} from "vuex";

const store = createStore({
    state() {
        return {
            books: [],
            bookDetail: {},
            isLoading: false
        }
    },
    mutations: {
        SET_BOOKS(state, books) {
            state.books = books;
        },
        SET_BOOK_DETAIL(state, book) {
            state.bookDetail = book;
        },
        SET_LOADING(state, loading) {
            state.isLoading = loading;
        }
    },
    actions: {
        fetchBooks({commit}) {
            commit('SET_LOADING', true);
            fetch('http://localhost:9001/api/books')
                .then(res => res.json())
                .then(bookPage => {
                    commit('SET_LOADING', false);
                    commit('SET_BOOKS', bookPage.content);
                })
                .catch(err => console.error(err));
        },
        fetchBookDetails({commit}, isbn) {
            commit('SET_LOADING', true);
            fetch(`http://localhost:9001/api/books/${isbn}`)
                .then(res => res.json())
                .then(book => {
                    commit('SET_LOADING', false);
                    commit('SET_BOOK_DETAIL', book);
                })
                .catch(err => console.error(err));
        }
    },
    getters: {
        isLoading(state) {
            return state.isLoading;
        },
        books(state) {
            return state.books;
        },
        bookDetail(state) {
            return state.bookDetail;
        }
    }
})

export default store;