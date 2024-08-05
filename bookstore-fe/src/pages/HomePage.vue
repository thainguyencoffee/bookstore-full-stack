<script>
import TheBanner from "../components/layouts/TheBanner.vue";
import TheCategory from "../components/layouts/TheCategory.vue";
import BooksList from "../components/BooksList.vue";

export default {
  components: {
    BooksList,
    TheCategory,
    TheBanner,
  },
  data() {
    return {
      categories: [],
      categoriesIsLoading: true
    }
  },
  computed: {
    categories() {
      return this.$store.getters['categories/categories'];
    },
    categoriesIsLoading() {
      return this.$store.getters['categories/isLoading'];
    },
  },
  mounted() {
    this.$store.dispatch('categories/fetchCategories');
  }
}
</script>

<template>
  <div class="container">
    <the-banner></the-banner>
    <the-category :categories="categories"></the-category>
    <div class="row mt-2">
      <div v-show="categoriesIsLoading">
        <div class="spinner-border text-primary" role="status">
          <span class="visually-hidden">Loading...</span>
        </div>
      </div>
      <books-list
          v-show="!categoriesIsLoading"
          v-for="category in categories"
          :key="category.id"
          :categoryId="category.id"
          :categoryName="category.name"
      ></books-list>
    </div>
  </div>
</template>

<style scoped>
</style>
