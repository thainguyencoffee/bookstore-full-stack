<script setup lang="ts">
import AuthenticationButtons from "./components/AuthenticationButtons.vue";
</script>

<template>
  <header>
    <div class="wrapper">
      <div style="flex">
        <h1>Vue</h1>
        <authentication-buttons></authentication-buttons>
      </div>

      <nav>
        <router-link to="/">Home</router-link>
        <router-link to="/about">About</router-link>
      </nav>
    </div>
  </header>
  <router-view></router-view>
</template>

<style scoped>
nav {
  width: 100%;
  font-size: 12px;
  text-align: center;
  margin-top: 2rem;
}

nav a.router-link-exact-active {
  color: #242424;
}

nav a.router-link-exact-active:hover {
  background-color: transparent;
}

nav a {
  display: inline-block;
  padding: 0 1rem;
  border-left: 1px solid #242424;
}

nav a:first-of-type {
  border: 0;
}
</style>
