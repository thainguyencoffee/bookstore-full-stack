<script>
import BookUnit from "../components/BookUnit.vue";
import {computed, onMounted} from "vue";
import TheBanner from "../components/layouts/TheBanner.vue";
import TheCategory from "../components/layouts/TheCategory.vue";
import {useStore} from "vuex";

export default {
  components: {
    TheCategory,
    TheBanner,
    BookUnit
  },
  setup() {
    const store = useStore();

    onMounted(() => {
      store.dispatch("fetchBooks");
    });

    const books = computed(() => store.getters.books);

    return {
      books
    };
  }
}
</script>

<template>
  <div class="container">
    <the-banner></the-banner>
    <the-category></the-category>
    <div class="row mt-2">
      <book-unit
          v-for="book in books"
          :key="book.id"
          :book="book"
      ></book-unit>
    </div>
  </div>
</template>

<style scoped>
</style>
