<script>
export default {
  props: {
    categories: {
      type: Array,
      required: true
    }
  }
}
</script>

<template>
  <section class="mt-3 bg-white rounded-2 p-2">
    <div class="container">
      <div class="row">
        <!-- Category items -->
        <div class="col-2 col-md-1 p-1 p-md-3" v-for="category in categories">
          <div class="category">
            <img src="https://cdn0.fahasa.com/media/wysiwyg/Thang-05-2024/Icon_GiamGia_120x120.png" alt="Sách văn học"
                 class="category-img">
            <span class="fs-20">{{category.name}}</span>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<style>
.category {
  text-align: center;
}

.category-img {
  width: 100%;
  height: auto;
  object-fit: cover;
}
</style>
