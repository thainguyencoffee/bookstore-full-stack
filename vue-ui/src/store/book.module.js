import {SET_BEST_SELLERS, SET_BOOKS} from "./mutations.type.js";

const bookModule = {
    namespaced: true,
    state() {
        return {
            books: [],
            bestSellers: []
        }
    },
    mutations: {
        [SET_BOOKS](state, books) {
          state.books = books;
        },
        [SET_BEST_SELLERS](state, books) {
            state.bestSellers = books;
        }
    },
    actions: {
        fetchBooks({commit}, payload) {
            const pageableStr = payload.pageableStr ? payload.pageableStr : '';
            fetch(`/bff/api/books?${pageableStr}`)
                .then(response => response.json())
                .then(bookPage => {
                    commit(SET_BOOKS, bookPage.content);
                })
        },
        fetchBestSellers({commit}, payload) {
            const from = payload.from;
            const pageableStr = payload.pageableStr ? payload.pageableStr : '';
            fetch(`/bff/api/books/best-sellers?from=${from}&${pageableStr}`)
                .then(response => response.json())
                .then(bookPage => {
                    commit(SET_BEST_SELLERS, bookPage.content);
                })
        }
    },
    getters: {
        bestSellers(state) {
            return state.bestSellers;
        }
    }
}

export default bookModule;