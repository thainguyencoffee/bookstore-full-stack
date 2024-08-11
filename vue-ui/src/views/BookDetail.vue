<script setup>

import {useRoute} from "vue-router";
import {computed, onMounted, reactive, ref} from "vue";

const route = useRoute();

const bookDetail = ref({})
const isbn = computed(() => route.params.isbn);
const bookTypes = reactive([
  "eBook", "print", "online + audio", "subscription"
])
const bookTypeSelected = ref(bookTypes[0])
const bookIsFavorite = ref(false);

const quotes = reactive([
  {
    actor: 'Walter Reade, Staff Developer',
    position: 'engineering: Google',
    content: 'This is a fantastic resource for getting up to speed on LLMs fast.'
  },
  {
    actor: 'Walter Reade, Staff Developer',
    position: 'engineering: Google',
    content: 'This is a fantastic resource for getting up to speed on LLMs fast.'
  },
  {
    actor: 'Walter Reade, Staff Developer',
    position: 'engineering: Google',
    content: 'This is a fantastic resource for getting up to speed on LLMs fast.'
  },
])

onMounted(() => {
  fetch(`/bff/api/books/${isbn.value}`)
      .then(res => {
        if (res.status === 200) return res.json()
        throw new Error(`Book with isbn ${isbn.value} not found!`)
      })
      .then(book => bookDetail.value = book);
})

function formatDate(instant) {
  const date = new Date(instant);
  return date.toLocaleString();
}

function formatCurrency(value) {
  return new Intl.NumberFormat('vi-VN', {
    style: 'currency',
    currency: 'VND'
  }).format(value);
}

function centerButtonAction() {

}

function toggleFavorite() {
  bookIsFavorite.value = !bookIsFavorite.value
}

function onBookTypeSelected(bookType) {
  bookTypes.forEach(b => {
    if (b === bookType) {
      bookTypeSelected.value = bookType;
    }
  })
}
</script>

<template>
  <div class="container mt-3">
    <div class="row g-3">
      <div class="col-12 col-md-3">
        <div class="mb-3 p-2 bg-white rounded border carousel-container">
          <div id="carouselExample" class="carousel slide">
            <div class="carousel-inner">
              <div class="carousel-item active" v-for="thumbnail in bookDetail.thumbnails">
                <img :src="thumbnail" class="d-block w-100" style="height: 400px; object-fit: cover;" alt="...">
              </div>
            </div>

            <!-- Overlay -->
            <div class="carousel-overlay"></div>

            <!-- Center Button -->
            <button class="center-button btn btn-primary" @click="centerButtonAction">
              Click to look inside
            </button>
          </div>
        </div>
      </div>
      <div class="col-12 col-md-6">

        <!-- book information -->
        <section>
          <div class="mb-1 p-2 ">
            <span class="fs-3">{{ bookDetail.title }}</span>
          </div>
          <div class="mb-1 p-2 ">
            rate
          </div>

          <div class="mb-1 p-2 ">
            <span class="fs-6 fw-bold d-block">{{ bookDetail.author }}</span>
            <span class="fs-6 fw-lighter d-block">Release in: {{ formatDate(bookDetail.createdAt) }} &centerdot; Publication in: {{
                formatDate(bookDetail.createdAt)
              }}</span>
            <!--Hasn't implemented `release in` yet-->
            <span class="fs-6 fw-lighter d-block">ISBN: {{ bookDetail.isbn }} &centerdot; {{ bookDetail.numberOfPages }} pages <i>(estimate)</i></span>
            <!--Hasn't implemented `release in` yet-->
          </div>

          <!--Type of books-->
          <div class="mb-2 m-auto p-1 p-md-2">
            <div class="list-group d-flex flex-column flex-md-row">
              <button v-for="t in bookTypes" @click="onBookTypeSelected(t)"
                      class="list-group-item list-group-item-action list-group-item-secondary rounded-0 border-0">
                {{ t }}
              </button>
            </div>
          </div>

          <!--Favorite on handset-->
          <section class="d-block d-md-none w-100 text-center rounded border mb-2">
            <!--Favorite state icon-->
            <div @click="toggleFavorite">
              <span class="fs-6 d-block">Add to favorite </span>
              <font-awesome-icon v-if="bookIsFavorite" class="fs-3 favorite-button" :icon="['fas', 'heart']"/>
              <!--Un favorite state icon-->
              <font-awesome-icon v-if="!bookIsFavorite" class="fs-3 favorite-button" :icon="['far', 'heart']"/>
            </div>
          </section>

          <!--Price on handset-->
          <section class="d-block d-md-none mb-5 p-2 bg-white rounded border sticky-md-top">
            <!--Price inside-->
            <div class="text-end">
              <span class="d-block">{{ bookTypeSelected }}</span>
              <span class="d-block fs-4 fw-light text-primary">{{ formatCurrency(bookDetail.price) }}</span>
              <span class="d-block">you save $15.00 (30%)</span>
            </div>
            <!--Add to card button-->
            <div>
              <button class="btn add-to-cart-btn w-100 mt-2">Add to cart</button>
            </div>
            <!--Free with subscription button-->
            <div>
              <button class="btn free-with-subscription-btn w-100 mt-2">Free with subscription</button>
            </div>
          </section>

        </section>

        <!-- book summary -->
        <section class="mb-1 p-2">
          <span class="fs-5 fw-bold d-block">Main content focus something!</span>
          <span class="fw-lighter fs-6 d-block">
            In Build a Large Language Model (from Scratch), you’ll discover how LLMs work from the inside out. In this insightful book, bestselling author Sebastian Raschka guides you step by step through creating your own LLM, explaining each stage with clear text, diagrams, and examples. You’ll go from the initial design and creation to pretraining on a general corpus, all the way to finetuning for specific tasks.
            <ul>
              <li>Build a Large Language Model (from Scratch) teaches you how to:            </li>
              <li>Plan and code all the parts of an LLM            </li>
              <li>Prepare a dataset suitable for LLM training            </li>
              <li>Finetune LLMs for text classification and with your own data            </li>
              <li>Apply instruction tuning techniques to ensure your LLM follows instructions            </li>
              <li>Load pretrained weights into an LLM            </li>
              <li>The large language models (LLMs) that power cutting-edge AI tools like ChatGPT, Bard, and Copilot seem like a miracle, but they’re not magic. This book demystifies LLMs by helping you build your own from scratch. You’ll get a unique and valuable insight into how LLMs work, learn how to evaluate their quality, and pick up concrete techniques to finetune and improve them.
              </li>
              <li>The process you use to train and develop your own small-but-functional model in this book follows the same steps used to deliver huge-scale foundation models like GPT-4. Your small-scale LLM can be developed on an ordinary laptop, and you’ll be able to use it as your own personal assistant.
              </li>
            </ul>
          </span>
        </section>

        <!--About the author-->
        <section class="mb-1 p-2">
          <span class="fs-3 fw-bold d-block">about the author</span>
          <span class="fs-6 d-block">
            <b>Sebastian Raschka</b>
            <span class="fw-lighter fs-6">
              has been working on machine learning and AI for more than a decade. Sebastian joined Lightning AI in 2022, where he now focuses on AI and LLM research, developing open-source software, and creating educational material. Prior to that, Sebastian worked at the University of Wisconsin-Madison as an assistant professor in the Department of Statistics, focusing on deep learning and machine learning research. He has a strong passion for education and is best known for his bestselling books on machine learning using open-source software.
            </span>
          </span>
        </section>
      </div>
      <div class="col-12 col-md-3">

        <!--Favorite-->
        <section class="d-none d-md-block" style="height: 26vh">
          <!--Favorite state icon-->
          <div @click="toggleFavorite">
            <font-awesome-icon v-if="bookIsFavorite" class="fs-3 favorite-button" :icon="['fas', 'heart']"/>
            <!--Un favorite state icon-->
            <font-awesome-icon v-if="!bookIsFavorite" class="fs-3 favorite-button" :icon="['far', 'heart']"/>
          </div>
        </section>

        <!--Price on desktop-->
        <section class="d-none d-md-block mb-5 p-2 bg-white rounded border sticky-md-top">
          <!--Price inside-->
          <div class="text-end">
            <span class="d-block">{{ bookTypeSelected }}</span>
            <span class="d-block fs-4 fw-light text-primary">{{ formatCurrency(bookDetail.price) }}</span>
            <span class="d-block">you save $15.00 (30%)</span>
          </div>
          <!--Add to card button-->
          <div>
            <button class="btn add-to-cart-btn w-100 mt-2">Add to cart</button>
          </div>
          <!--Free with subscription button-->
          <div>
            <button class="btn free-with-subscription-btn w-100 mt-2">Free with subscription</button>
          </div>
        </section>

        <!--Quote-->
        <section>
          <div class="mb-5 p-2 bg-light rounded border quote-item" v-for="quote in quotes">
            <span class="d-block text-center fs-6 fw-medium">{{ quote.content }}</span>
            <span
                class="d-block text-end fst-italic fw-lighter">{{ quote.actor }} &centerdot; {{ quote.position }}</span>
            <span class="quote-icon">
              <font-awesome-icon :icon="['fas', 'quote-left']"/>
            </span>
          </div>
        </section>
      </div>
    </div>
  </div>
</template>

<style scoped>
.carousel-container {
  position: relative;
}

.center-button {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  opacity: 0;
  transition: opacity 0.3s ease;
  z-index: 20;
}

.carousel-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  z-index: 10;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.carousel-container:hover .center-button {
  opacity: 1; /* Fade in the button */
}

.carousel-container:hover .carousel-overlay {
  opacity: 1; /* Fade in the overlay */
}

.favorite-button {
  cursor: pointer;
}

.add-to-cart-btn {
  background: #4b4bff;
  color: white;
}

.add-to-cart-btn:hover {
  background: #3232ff;
  color: white;
}

.free-with-subscription-btn {
  background: #d7d7ff;
  color: #4b4bff;
  font-weight: bold;
  border: 1px solid #3232ff;
}

.free-with-subscription-btn:hover {
  background: #4b4bff;
  color: #ffffff;
}

.quote-item {
  position: relative;
}

.quote-icon {
  position: absolute;
  top: -30%;
  left: 0;
  font-size: 2.5rem;
  color: black;
  opacity: 50%;
}
</style>