<script>
import BookUnit from "./BookUnit.vue";
import {computed, onMounted} from "vue";
import {useStore} from "vuex";

export default {
  components: {
    BookUnit
  },
  props: ["categoryId", "categoryName"],
  setup(props) {
    const store = useStore();

    onMounted(() => {
      store.dispatch("books/fetchBooksByCategory", props.categoryId);
    });

    const booksByCategory = computed(() => store.getters["books/booksByCategory"](props.categoryId));
    const isLoading = computed(() => store.getters['books/isLoading'])

    return {
      booksByCategory,
      isLoading
    };
  }
}
</script>

<template>
  <div class="container" v-if="Array.from(booksByCategory).length > 0">
    <span class="fs-4 fw-bold text-secondary text-decoration-underline">{{categoryName}}</span>
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
