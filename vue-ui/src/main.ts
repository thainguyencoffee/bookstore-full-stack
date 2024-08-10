import { createApp } from 'vue'
import './style.css'
import App from './App.vue'
import router from "./router";
import {UserService} from "./user.service.ts";

const app = createApp(App);
app.provide("UserService", new UserService());
app.use(router);
app.mount('#app');