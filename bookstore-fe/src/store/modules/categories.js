const state = {
    categories: [],
    isLoading: false
};

const mutations = {
    SET_CATEGORIES(state, categories) {
        state.categories = categories;
    },
    SET_LOADING(state, loading) {
        state.isLoading = loading;
    }
};

const actions = {
    fetchCategories({ commit }) {
        commit('SET_LOADING', true);
        fetch('http://localhost:9001/api/categories')
            .then(res => res.json())
            .then(categories => commit('SET_CATEGORIES', categories))
            .catch(err => console.error(err))
            .finally(() => commit('SET_LOADING', false));
    }
};

const getters = {
    isLoading(state) {
        return state.isLoading;
    },
    categories(state) {
        return state.categories;
    },
};

export default {
    namespaced: true,
    state,
    mutations,
    actions,
    getters
};
