<script setup>
import {useStore} from 'vuex';
import {computed} from "vue";

const store = useStore();

const user = computed(() => store.getters.user);

</script>

<template>
  <div class="container">
    <h1>Profile</h1>
    <div v-if="user">
      <p>Name: {{ user.username }}</p>
      <p>Email: {{ user.email }}</p>
      <p>Roles: {{ user.roles }}</p>
      <p>Exp: {{ user.exp }}</p>
    </div>
  </div>
</template>

<style scoped>
</style>