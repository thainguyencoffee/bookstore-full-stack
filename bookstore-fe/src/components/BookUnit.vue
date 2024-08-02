<script>
import {computed} from 'vue';

export default {
  props: {
    book: {
      type: Object,
      required: true
    }
  },
  setup(props) {
    const linkTo = computed(() => {
      return {
        name: 'book-detail',
        params: {
          isbn: props.book.isbn
        }
      }
    });

    return {
      linkTo
    }
  }
}
</script>

<template>
  <div class="col-12 col-md-4">
    <router-link :to="linkTo" class="card mb-3" style="max-width: 540px;">
      <div class="row g-0">
        <div class="col-md-4 thumbnail">
          <img :src="book.thumbnails[0]" class="img-fluid rounded-start h-100 p-1" alt="Thumbnail">
        </div>
        <div class="col-md-8">
          <div class="card-body p-2 p-md-1">
            <h5 class="card-title">{{ book.title }}</h5>
            <p class="card-subtitle">Tác giả: {{ book.author }}</p>
            <p class="card-subtitle">Nhà cung cấp: {{ book.supplier }}</p>
            <p class="card-subtitle badge text-bg-danger mb-2">Giá: {{ book.price }}</p>
            <p class="card-subtitle">Ngôn ngữ: {{ book.language }}</p>
            <p class="card-subtitle">Loại bìa: {{ book.coverType }}</p>
            <p class="card-text"><small class="text-body-secondary">Đã bán {{ book.purchases }} cuốn</small></p>
          </div>
          <div class="card-actions p-2 p-md-1 d-flex justify-content-between">
            <button class="btn btn-primary">Mua</button>
            <button class="btn btn-secondary mx-2">Add to Cart</button>
            <!--            <div class="d-flex align-items-center">-->
            <!--              <button class="quantity btn btn-light fw-bold">-</button>-->
            <!--              <span class="mx-3 fw-bolder"> {{book.inventory}} </span>-->
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
  object-fit: cover;
}

.quantity {
  width: 3rem;
}
</style>