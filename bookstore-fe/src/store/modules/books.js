const booksModule =  {
    namespaced: true,
    state() {
        return {
            books: []
        }
    },
    mutations: {
        setBooks(state, payload) {
            state.books = payload.books;
        }
    },
    actions: {
        fetchBooks({commit}) {
            fetch('http://localhost:9001/api/books')
                .then(res => res.json())
                .then(bookPage => {
                    commit('setBooks', {books: bookPage.content});
                })
                .catch(err => console.error(err));
        }
    },
    getters: {
        books(state) {
            return state.books;
        }
    }
}

export default booksModule;