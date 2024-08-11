import {SET_CATEGORIES, SET_SUBCATEGORIES_MAP} from "./mutations.type.js";

const categoryModule = {
    namespaced: true,
    state() {
        return {
            categories: [],
            subCategoriesMap: {}
        }
    },
    mutations: {
        [SET_CATEGORIES](state, categories) {
            state.categories = categories;
        },
        [SET_SUBCATEGORIES_MAP](state, payload) {
            state.subCategoriesMap[payload.id] = payload.subCategoriesMap;
        }
    },
    actions: {
        fetchCategories({commit}) {
            fetch("/bff/api/categories")
                .then(response => response.json())
                .then(categories => {
                    commit(SET_CATEGORIES, categories);
                    for (const category of categories) {
                        fetch(`/bff/api/categories/${category.id}/children`)
                            .then(response => response.json())
                            .then(subCategories => {
                                commit(SET_SUBCATEGORIES_MAP, {id: category.id, subCategoriesMap: subCategories});
                            })
                    }
                })
        }
    },
    getters: {
        categories(state) {
            return state.categories;
        },
        subCategoriesMap(state) {
            return state.subCategoriesMap;
        }
    }
}
export default categoryModule;