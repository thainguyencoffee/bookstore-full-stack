<script>
import BookUnit from "../components/BookUnit.vue";
import {computed, onMounted} from "vue";
import TheBanner from "../components/layouts/TheBanner.vue";
import TheCategory from "../components/layouts/TheCategory.vue";
import {useStore} from "vuex";
import BooksList from "../components/BooksList.vue";

export default {
  components: {
    BooksList,
    TheCategory,
    TheBanner,
  },
  setup() {
    const store = useStore();

    onMounted(() => {
      store.dispatch("categories/fetchCategories");
    });

    const categories = computed(() => store.getters['categories/categories'])
    const categoriesIsLoading = computed(() => store.getters["categories/isLoading"])

    return {
      categories,
      categoriesIsLoading
    };
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
