<script>
import {computed, onMounted} from "vue";
import {useStore} from "vuex";

export default {
  props: ["isbn"],
  setup(props) {
    const store = useStore();

    onMounted(() => store.dispatch("books/fetchBookDetails", props.isbn))

    const bookDetail = computed(() => store.getters["books/bookDetail"])
    const isLoading = computed(() => store.getters["books/isLoading"])
    return {
      book: bookDetail,
      isLoading: isLoading
    }
  }
}
</script>

<template>
  <div v-show="isLoading">
    <div class="spinner-border text-primary" role="status">
      <span class="visually-hidden">Loading...</span>
    </div>
  </div>
  <div v-show="!isLoading">
    <div class="row mt-2">
      <div class="col-12 col-md-5 sticky-top" style="height: 480px;">
        <div class="p-1 p-md-2 bg-white rounded-2">
          {{book.isbn}}
          picture
        </div>
      </div>
      <div class="col-12 col-md-7">
        <div class="row px-2" style="height: 100px">
          <div class="p-1 p-md-2 bg-white rounded-2 mb-2">
            <span class="fs-4 fw-bold">{{book.title}}</span>
            title, purchase, vote
          </div>
        </div>
        <div class="row px-2" style="height: 100px">
          <div class="p-1 p-md-2 bg-white rounded-2 mb-2">
            discount
          </div>
        </div>
        <div class="row px-2" style="height: 100px">
          <div class="p-1 p-md-2 bg-white rounded-2 mb-2">
            commit
          </div>
        </div>
        <div class="row px-2" style="height: 420px">
          <div class="p-1 p-md-2 bg-white rounded-2 mb-2">
            description
          </div>
        </div>
      </div>
    </div>
    <!--Related books-->
    <div class="row px-2" style="height: 80vh">
      <div class="p-1 p-md-2 bg-white rounded-2 mt-2">
        <h1 class="text-center">Related books</h1>
      </div>
    </div>
  </div>
</template>

<style scoped>

</style>