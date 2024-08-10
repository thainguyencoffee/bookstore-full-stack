<script setup>
import TheHeader from "./components/TheHeader.vue";
import TheBanner from "./components/BookstoreOnline.vue";
import TheFooter from "./components/TheFooter.vue";
</script>

<template>
  <the-header></the-header>
  <div class="container">
    <router-view></router-view>
  </div>
  <the-footer></the-footer>
</template>

<style scoped>

</style>
