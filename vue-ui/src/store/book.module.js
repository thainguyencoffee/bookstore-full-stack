import {SET_BEST_SELLERS} from "./mutations.type.js";

const bookModule = {
    namespaced: true,
    state() {
        return {
            bestSellers: []
        }
    },
    mutations: {
        [SET_BEST_SELLERS](state, books) {
            state.bestSellers = books;
        }
    },
    actions: {
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