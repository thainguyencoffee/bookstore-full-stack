import {SET_BEST_SELLERS} from "./mutations.type.js";

const bookModule = {
    namespaced: true,
    state() {
        return {
            bestSellers: []
        }
    },
    mutations: {
        [SET_BEST_SELLERS](state, bookSalesView) {
            state.bestSellers = bookSalesView;
        }
    },
    actions: {
        fetchBestSellers({commit}, payload) {
            const top = payload.top;
            fetch(`/bff/api/books/best-sellers?top=${top}`)
                .then(response => response.json())
                .then(bookSalesView => {
                    commit(SET_BEST_SELLERS, bookSalesView);
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