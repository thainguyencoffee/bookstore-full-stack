<script setup>
import {useStore} from 'vuex';
import {useRoute} from 'vue-router';
import {useCookies} from "vue3-cookies";
import {computed, onMounted, ref} from "vue";

const store = useStore();
const route = useRoute();
const {cookies} = useCookies()

const loginUri = ref('');
const isLoggedIn = computed(() => store.getters.userIsLoggedIn);
const user = computed(() => store.getters.user);

onMounted(async () => {
  // Fetch user
  store.dispatch('refreshAuth');
  if (isLoggedIn.value) {
    return;
  }
  // Fetch login options from the BFF
  const response = await fetch("/bff/login-options");
  const opts = await response.json();
  if (opts.length) {
    loginUri.value = opts[0].loginUri;
  }
})

function login() {
  // error
  if (!loginUri.value) {
    return
  }
  const url = new URL(loginUri.value)

  url.searchParams.append(
      'post_login_success_uri',
      `${import.meta.env.VITE_REVERSE_PROXY}${import.meta.env.BASE_URL}${route.path}`
  )
  url.searchParams.append(
      'post_login_failure_uri',
      `${import.meta.env.VITE_REVERSE_PROXY}${import.meta.env.BASE_URL}/login-error`
  )
  window.location.href = url.toString();
}

async function logout(xsrfToken) {
  const response = await fetch("/bff/logout", {
    method: "POST",
    headers: {
      "X-XSRF-TOKEN": xsrfToken,
      "X-POST-LOGOUT-SUCCESS-URI": `${import.meta.env.VITE_REVERSE_PROXY}${import.meta.env.BASE_URL}/`
    }
  })
  const location = response.headers.get("Location");
  if (location) {
    window.location.href = location;
  }
}

</script>

<template>
  <nav class="navbar navbar-expand-lg navbar-light bg-light">
    <div class="container">
      <!-- Phần 1: Logo -->
      <router-link to="/" class="navbar-brand">
        <img src="https://svgsilh.com/svg/1973672.svg" alt="Logo" width="50" height="50"
             class="d-inline-block align-text-top">
      </router-link>

      <!-- Phần 3: User Dropdown -->
      <div class="order-lg-last ms-auto">
        <div class="dropdown">
          <button class="btn btn-primary dropdown-toggle" type="button" id="userDropdown" data-bs-toggle="dropdown"
                  aria-expanded="false">
            <span v-if="!isLoggedIn">Guest</span>
            <span v-else>{{ user.username }}</span>
            <font-awesome-icon :icon="['fas', 'user']"/>
          </button>
          <ul class="user-list__ul dropdown-menu dropdown-menu-end" aria-labelledby="userDropdown">
            <li v-if="isLoggedIn">
              <router-link class="dropdown-item" to="/profile">
                Profile
              </router-link>
            </li>
            <li v-if="isLoggedIn">
              <hr class="dropdown-divider">
            </li>
            <li v-if="isLoggedIn" class="dropdown-item" @click="logout(cookies.get('XSRF-TOKEN'))">
              Logout
            </li>
            <li v-if="!isLoggedIn" class="dropdown-item" @click="login()">
              Login
            </li>
          </ul>
        </div>
      </div>

      <!-- Phần 2: Collapse Navigation -->
      <button class="navbar-toggler mx-2" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNavDropdown"
              aria-controls="navbarNavDropdown" aria-expanded="false" aria-label="Toggle navigation">
        <span class="navbar-toggler-icon"></span>
      </button>
      <div class="collapse navbar-collapse" id="navbarNavDropdown">
        <ul class="navbar-nav ms-auto">
          <li class="nav-item">
            <a class="nav-link active" aria-current="page" href="#">Home</a>
          </li>
          <li class="nav-item">
            <a class="nav-link" href="#">Features</a>
          </li>
          <li class="nav-item">
            <a class="nav-link" href="#">Pricing</a>
          </li>
        </ul>
      </div>
    </div>
  </nav>
</template>

<style scoped>
.user-list__ul li {
  cursor: pointer;
}
</style>
