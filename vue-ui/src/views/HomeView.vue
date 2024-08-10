<script setup>

import BookstoreOnline from "../components/BookstoreOnline.vue";
</script>

<template>
  <bookstore-online></bookstore-online>
  <div class="row">
    <div class="col-4">
      <div class="row p-3 rounded-1 border home__mail-preferences">
        <div class="col-12">
          Mail Preferences
        </div>
        <div class="col-12 d-flex justify-content-evenly align-items-center">
          <input type="email" class="form-control" placeholder="email address">
          <button type="submit" class="btn btn-primary w-auto fw-bold">+</button>
        </div>
      </div>
      <div class="row p-1 mt-2 rounded-1 border ">
        Free books
      </div>
      <div class="row p-1 mt-2 rounded-1 border ">
        Community
      </div>
    </div>
    <div class="col-4 m-0 px-2">
      <div class="row p-1 rounded-1 border ">
        Slider
      </div>
      <div class="row p-1 mt-2 rounded-1 border ">Recent release</div>
      <div class="row p-1 mt-2 rounded-1 border ">Publisher news</div>
    </div>
    <div class="col-4 m-0 px-2">
      <div class="row p-1 rounded-1 border ">
        About your books
      </div>
      <div class="row p-1 mt-2 rounded-1 border ">Best sellers</div>
      <div class="row p-1 mt-2 rounded-1 border ">Want to selling</div>
    </div>
  </div>
  <div class="row m-0">
    Bookstore reviews
  </div>

</template>

<style scoped>
.home__mail-preferences {
  background-image: url(require('./assets/dotdBackground.png'));
}
</style>