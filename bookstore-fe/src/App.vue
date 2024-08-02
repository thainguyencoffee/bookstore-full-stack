<script>
import TheHeader from "./components/layouts/TheHeader.vue";
export default {
  components: {TheHeader}
}
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
