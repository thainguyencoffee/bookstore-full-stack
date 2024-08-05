<script>
import BookUnit from "./BookUnit.vue";

export default {
  components: {
    BookUnit
  },
  props: ["categoryId", "categoryName"],
  computed: {
    booksByCategory() {
      return this.$store.getters["books/booksByCategory"](this.categoryId);
    },
    isLoading() {
      return this.$store.getters['books/isLoading'];
    }
  },
  mounted() {
    this.$store.dispatch("books/fetchBooksByCategory", this.categoryId);
  }
}
</script>

<template>
  <div class="container" v-if="Array.from(booksByCategory).length > 0">
    <span class="fs-4 fw-bold text-secondary text-decoration-underline">{{ categoryName }}</span>
    <div class="row mt-2">
      <book-unit
          v-for="book in booksByCategory"
          :key="book.id"
          :book="book"
      ></book-unit>
    </div>
  </div>
</template>

<style scoped>
</style>
