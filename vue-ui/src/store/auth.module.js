import {LOGIN, LOGOUT, CHECK_AUTH, FETCH_LOGIN_OPTIONS} from './actions.type.js'
import { SET_AUTH, PURGE_AUTH, SET_ERROR } from './mutations.type.js'

const state = {
    loginUri: '',
    loginExperiences: [],
    selectedLoginExperience: '',
    isLoginModalDisplayed: false,
    iframeSrc: '',
    errors: null,
    user: {},
    isAuthenticated: false // dont need to check token because we using bff
}

// nothing diff
const getters = {
    loginUri(state) {
        return state.loginUri;
    },
    loginExperiences(state) {
        return state.loginExperiences;
    },
    selectedLoginExperience(state) {
        return state.selectedLoginExperience;
    },
    isLoginModalDisplayed(state) {
        return state.isLoginModalDisplayed;
    },
    iframeSrc(state) {
        return state.iframeSrc;
    },
    currentUser(state) {
        return state.user;
    },
    isAuthenticated(state) {
        return state.isAuthenticated;
    }
}

const actions = {
    async [FETCH_LOGIN_OPTIONS] ({commit}) {
        try {
            const response = await axios
        }
    }
}