import { createApp } from 'vue'
import 'bootstrap/dist/css/bootstrap.css';
import 'bootstrap/dist/js/bootstrap.bundle.min.js';
// import all icons from the free-solid-svg-icons
import { fas } from '@fortawesome/free-solid-svg-icons';
import { far } from '@fortawesome/free-regular-svg-icons';

import { library } from '@fortawesome/fontawesome-svg-core';
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome';


import App from './App.vue'
import router from "./router.js";
import store from "./store/index.js";

const app = createApp(App);

// add all icons to the library
library.add(fas);
library.add(far);
app.component('font-awesome-icon', FontAwesomeIcon);

app
    .use(store)
    .use(router)
    .mount('#app')
