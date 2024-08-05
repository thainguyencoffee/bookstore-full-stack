<script>

export default {
  computed: {
    categories() {
      return this.$store.getters['categories/categories'];
    }
  },
}
</script>

<template>
  <h1>Breadcrumb work!</h1>
  <ul>
    <li v-for="category in categories">
      {{category.id}}
    </li>
  </ul>
</template>