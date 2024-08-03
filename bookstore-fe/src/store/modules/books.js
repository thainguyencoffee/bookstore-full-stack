const state = {
    booksByCategory: {},
    isLoading: false,
    bookDetail: {},
};

const mutations = {
    SET_BOOKS_BY_CATEGORY(state, {categoryId, books}) {
        state.booksByCategory = {...state.booksByCategory, [categoryId]: books};
    },
    SET_BOOK_DETAIL(state, book) {
        state.bookDetail = book;
    },
    SET_LOADING(state, loading) {
        state.isLoading = loading;
    }
};

const actions = {
    fetchBookDetails({commit}, isbn) {
        commit('SET_LOADING', true);
        fetch(`http://localhost:9001/api/books/${isbn}`)
            .then(res => res.json())
            .then(book => {
                commit('SET_LOADING', false);
                commit('SET_BOOK_DETAIL', book);
            })
            .catch(err => console.error(err));
    },
    fetchBooksByCategory({commit}, categoryId) {
        commit('SET_LOADING', true);
        fetch(`http://localhost:9001/api/categories/${categoryId}/books`)
            .then(res => res.json())
            .then(books => {
                commit('SET_LOADING', false);
                commit('SET_BOOKS_BY_CATEGORY', {categoryId: categoryId, books: books});
            })
            .catch(err => console.error(err));
    }
};

const getters = {
    isLoading(state) {
        return state.isLoading;
    },
    booksByCategory: (state) => (categoryId) => {
        return state.booksByCategory[categoryId] || [];
    },
    bookDetail(state) {
        return state.bookDetail;
    }
}

export default {
    namespaced: true,
    state,
    getters,
    actions,
    mutations,
}