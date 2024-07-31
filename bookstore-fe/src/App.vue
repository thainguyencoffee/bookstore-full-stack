<script setup>

import TheHeader from "./components/layouts/TheHeader.vue";
import TheBanner from "./components/layouts/TheBanner.vue";
import TheCategory from "./components/layouts/TheCategory.vue";
</script>

<template>
  <div class="app container-fluid">
    <the-header></the-header>
    <main class="container">
      <router-view></router-view>
    </main>
  </div>
</template>

<style scoped>
.app {
  padding: 0;
  margin: 0;
  background-color: #f1f1f1;
}
</style>
