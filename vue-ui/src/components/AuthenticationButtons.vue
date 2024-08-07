<script setup lang="ts">
import {inject} from "vue";
import {UserService} from "../user.service.ts";
import LoginForm from "../views/LoginForm.vue";
import LogoutForm from "../views/LogoutForm.vue";

const user = inject('UserService') as UserService;
</script>

<template>
<span>
  <login-form v-if="!user.current.value.isAuthenticated"></login-form>
  <logout-form v-else></logout-form>
</span>
</template>

<style scoped>

</style>