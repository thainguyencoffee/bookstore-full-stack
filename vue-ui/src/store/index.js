import { createStore } from 'vuex';
import { SET_AUTH, SET_REFRESH_INTERVAL_ID, SET_USER } from "./mutations.type.js";

const store = createStore({
    state() {
        return {
            user: {},
            isLoggedIn: false,
            refreshIntervalId: null
        };
    },
    mutations: {
        [SET_AUTH](state, payload) {
            state.isLoggedIn = payload.isAuth;
        },
        [SET_USER](state, payload) {
            state.user = payload.user;
        },
        [SET_REFRESH_INTERVAL_ID](state, payload) {
            state.refreshIntervalId = payload.refreshIntervalId;
        }
    },
    actions: {
        async refreshAuth(context) {
            if (context.state.refreshIntervalId) {
                clearInterval(context.state.refreshIntervalId);
            }
            try {
                const response = await fetch("/bff/api/me");
                const user = await response.json();
                if (user.username) {
                    context.commit(SET_USER, { user });
                    context.commit(SET_AUTH, { isAuth: true });
                }
                if (user.exp) {
                    const now = new Date().getTime();
                    const delay = ((user.exp * 1000) - now) * 0.8;
                    if (delay > 2000) {
                        const refreshIntervalId = setInterval(() => {
                            context.dispatch("refreshAuth");
                        }, delay);
                        context.commit(SET_REFRESH_INTERVAL_ID, { refreshIntervalId });
                    }
                }
            } catch (error) {
                console.error("Failed to refresh auth:", error);
            }
        }
    },
    getters: {
        userIsLoggedIn(state) {
            return state.isLoggedIn;
        },
        user(state) {
            return state.user;
        }
    }
});

export default store;
