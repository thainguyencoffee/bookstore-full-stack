<script setup>

import BookstoreOnline from "../components/BookstoreOnline.vue";
import {computed, onMounted, ref} from "vue";
import {useStore} from "vuex";

const store = useStore();

const emailPreferences = ref('');
const bestSellers = computed(() => store.getters["books/bestSellers"])

onMounted(() => {
  // fetch best sellers
  store.dispatch("books/fetchBestSellers", {top: 10});
})

function bookDetailLink(isbn) {
  return {
    name: 'bookDetail',
    params: {isbn}
  }
}
</script>

<template>
  <bookstore-online></bookstore-online>
  <div class="container">
    <div class="row g-3">
      <div class="col-12 col-md-4">
        <div class="card mb-3 p-2 rounded home_category text-white">
          <h4 class="text-start">sign up for</h4>
          <h5 class="text-center">Deal of the Day,</h5>
          <h5 class="text-end">Weekly Newsletters</h5>
          <h4 class="text-center">and more</h4>
          <div class="d-flex justify-content-around align-items-center">
            <input type="email" class="form-control" placeholder="email address" v-model="emailPreferences">
            <router-link :to="{ name: 'emailPreferences', query: {email: emailPreferences} }"
                         class="btn btn-primary w-25">Go
            </router-link>
          </div>
        </div>
        <div class="mb-3 p-2 bg-white rounded home_category">
          <h3>Bookstore online</h3>
          <p>Bookstore online is a place where you can find all the books you need. We have a wide range of books from
            different genres. You can find books for children, teenagers, and adults. We also have books in different
            languages. You can buy books online and have them delivered to your doorstep. We have a team of experts who
            can help you find the perfect book for you. We also have a blog where you can read about the latest books
            and authors. So, if you are looking for a book, visit Bookstore online today!</p>
        </div>
        <div class="p-2 bg-white rounded home_category">
          <h3>Bookstore online</h3>
          <p>Bookstore online is a place where you can find all the books you need. We have a wide range of books from
            different genres. You can find books for children, teenagers, and adults. We also have books in different
            languages. You can buy books online and have them delivered to your doorstep. We have a team of experts who
            can help you find the perfect book for you. We also have a blog where you can read about the latest books
            and authors. So, if you are looking for a book, visit Bookstore online today!</p>
        </div>
      </div>
      <div class="col-12 col-md-4">
        <div class="mb-3 p-2 bg-white rounded home_category">
          <h3>Bookstore online</h3>
          <p>Bookstore online is a place where you can find all the books you need. We have a wide range of books from
            different genres. You can find books for children, teenagers, and adults. We also have books in different
            languages. You can buy books online and have them delivered to your doorstep. We have a team of experts who
            can help you find the perfect book for you. We also have a blog where you can read about the latest books
            and authors. So, if you are looking for a book, visit Bookstore online today!</p>
        </div>
        <div class="mb-3 p-2 rounded bg-dark bg-opacity-10">
          <span class="fs-3 fw-normal">Bestsellers</span> &af;
          <div>
            <ul style="list-style: decimal">
              <li v-for="book in bestSellers" :key="book.id">
                <router-link class="best-sellers-item" :to="bookDetailLink(book.isbn)">{{ book.title }} - purchases :
                  {{ book.totalPurchases }}
                </router-link>
              </li>
            </ul>
          </div>
        </div>
        <div class="p-2 bg-white rounded home_category">
          <h3>Bookstore online</h3>
          <p>Bookstore online is a place where you can find all the books you need. We have a wide range of books from
            different genres. You can find books for children, teenagers, and adults. We also have books in different
            languages. You can buy books online and have them delivered to your doorstep. We have a team of experts who
            can help you find the perfect book for you. We also have a blog where you can read about the latest books
            and authors. So, if you are looking for a book, visit Bookstore online today!</p>
        </div>
      </div>
      <div class="col-12 col-md-4">
        <div class="mb-3 p-2 bg-white rounded home_category">
          <h3>Bookstore online</h3>
          <p>Bookstore online is a place where you can find all the books you need. We have a wide range of books from
            different genres. You can find books for children, teenagers, and adults. We also have books in different
            languages. You can buy books online and have them delivered to your doorstep. We have a team of experts who
            can help you find the perfect book for you. We also have a blog where you can read about the latest books
            and authors. So, if you are looking for a book, visit Bookstore online today!</p>
        </div>
        <div class="mb-3 p-2 bg-white rounded home_category">
          <h3>Bookstore online</h3>
          <p>Bookstore online is a place where you can find all the books you need. We have a wide range of books from
            different genres. You can find books for children, teenagers, and adults. We also have books in different
            languages. You can buy books online and have them delivered to your doorstep. We have a team of experts who
            can help you find the perfect book for you. We also have a blog where you can read about the latest books
            and authors. So, if you are looking for a book, visit Bookstore online today!</p>
        </div>
        <div class="p-2 bg-white rounded home_category">
          <h3>Bookstore online</h3>
          <p>Bookstore online is a place where you can find all the books you need. We have a wide range of books from
            different genres. You can find books for children, teenagers, and adults. We also have books in different
            languages. You can buy books online and have them delivered to your doorstep. We have a team of experts who
            can help you find the perfect book for you. We also have a blog where you can read about the latest books
            and authors. So, if you are looking for a book, visit Bookstore online today!</p>
        </div>
      </div>
    </div>
  </div>

  <div class="row">
    Bookstore reviews
  </div>

</template>

<style scoped>
.home_category {
  background-image: url("https://www.manning.com/assets/dotdBackground_small-aea7dbee847cb26aab6e506586cd8a2b.png");
  background-size: cover;
}

.best-sellers-item {
  color: rebeccapurple;
  text-decoration: none;
}

.best-sellers-item:hover {
  text-decoration: underline;
}
</style>