<script setup>
import {computed, toRefs} from 'vue';

const props = defineProps({
  id: {
    type: Number,
    required: true
  },
  title: {
    type: String,
    required: true
  },
  isbn: {
    type: String,
    required: true
  },
  author: {
    type: String,
    required: true
  },
  publisher: {
    type: String,
    required: true
  },
  supplier: {
    type: String,
    required: true
  },
  price: {
    type: Number,
    required: true
  },
  inventory: {
    type: Number,
    required: true
  },
  language: {
    type: String,
    required: true
  },
  coverType: {
    type: String,
    required: true
  },
  numberOfPages: {
    type: Number,
    required: true
  },
  measure: {
    type: Object,
    required: true,
    default: () => ({
      width: 0,
      height: 0,
      thickness: 0,
      weight: 0
    })
  },
  purchases: {
    type: Number,
    required: true
  },
  thumbnails: {
    type: Array,
    required: true,
    default: () => []
  },
  description: {
    type: String,
    required: false
  },
  createdAt: {
    type: String,
    required: true
  },
  createdBy: {
    type: String,
    required: true
  },
  lastModifiedAt: {
    type: String,
    required: true
  },
  lastModifiedBy: {
    type: String,
    required: true
  }
});

const {
  id,
  title,
  isbn,
  author,
  publisher,
  supplier,
  price,
  inventory,
  language,
  coverType,
  numberOfPages,
  measure,
  purchases,
  thumbnails,
  description,
  createdAt,
  createdBy,
  lastModifiedAt,
  lastModifiedBy
} = toRefs(props);

const linkTo = computed(() => '/books/' + id.value);
</script>

<template>
  <div class="col-12 col-md-4">
    <router-link :to="linkTo" class="card mb-3" style="max-width: 540px;">
      <div class="row g-0">
        <div class="col-md-4 thumbnail">
          <img :src="thumbnails[0]" class="img-fluid rounded-start h-100 p-1" alt="Thumbnail">
        </div>
        <div class="col-md-8">
          <div class="card-body p-2 p-md-1">
            <h5 class="card-title">{{ title }}</h5>
            <p class="card-subtitle">Tác giả: {{author}}</p>
            <p class="card-subtitle">Nhà cung cấp: {{supplier}}</p>
            <p class="card-subtitle badge text-bg-danger mb-2">Giá: {{price}}</p>
            <p class="card-subtitle">Ngôn ngữ: {{language}}</p>
            <p class="card-subtitle">Loại bìa: {{coverType}}</p>
            <p class="card-text"><small class="text-body-secondary">Đã bán {{purchases}} cuốn</small></p>
          </div>
          <div class="card-actions p-2 p-md-1 d-flex justify-content-between">
            <button class="btn btn-primary">Mua</button>
            <button class="btn btn-secondary mx-2">Add to Cart</button>
<!--            <div class="d-flex align-items-center">-->
<!--              <button class="quantity btn btn-light fw-bold">-</button>-->
<!--              <span class="mx-3 fw-bolder"> {{inventory}} </span>-->
<!--              <button class="quantity btn btn-light fw-bold">+</button>-->
<!--            </div>-->
          </div>
        </div>
      </div>
    </router-link>
  </div>
</template>

<style scoped>
a {
  text-decoration: none;
}
.thumbnail img {
  object-fit: cover; /* Đảm bảo ảnh được cắt để vừa với khung */
}
.quantity {
  width: 3rem;
}
</style>