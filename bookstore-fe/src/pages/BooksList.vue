<script>
import BookUnit from "../components/BookUnit.vue";
import {computed, onMounted} from "vue";

export default {
  components: {
    BookUnit
  },
  setup() {
    onMounted(() => {
      this.$store.dispatch("books-module/fetchBooks");
    });

    const booksFetched = computed(() => this.$store.getters["books-module/books"]);

    return {
      books: booksFetched
    }
  }
}
</script>

<template>
  <div class="container">
    <the-banner></the-banner>
    <the-category></the-category>
    <div class="row mt-2">
      <book-unit v-for="book in books"
                 :id="book.id"
                 :isbn="book.isbn"
                 :title="book.title"
                 :author="book.author"
                 :publisher="book.publisher"
                 :supplier="book.supplier"
                 :price="book.price"
                 :inventory="book.inventory"
                 :purchases="book.purchases"
                 :language="book.language"
                 :measure="book.measure"
                 :cover-type="book.coverType"
                 :number-of-pages="book.numberOfPages"
                 :created-at="book.createdAt"
                 :created-by="book.createdBy"
                 :last-modified-at="book.lastModifiedAt"
                 :last-modified-by="book.lastModifiedBy"
                 :key="book.id"
                 :description="book.description"
                 :thumbnails="book.thumbnails"
      ></book-unit>
    </div>
  </div>
</template>

<style scoped>

</style>