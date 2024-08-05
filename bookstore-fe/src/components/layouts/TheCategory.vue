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
          <router-link to="/">
            <div class="category">
              <img :src="category.thumbnail" alt="Sách văn học"
                   class="category-img">
              <span class="fs-20">{{ category.name }}</span>
            </div>
          </router-link>
        </div>
      </div>
    </div>
  </section>
</template>

<style>
a {
  text-decoration: none;
  color: inherit;
}

.category {
  text-align: center;
}

.category-img {
  width: 100%;
  height: auto;
  object-fit: cover;
}
</style>
