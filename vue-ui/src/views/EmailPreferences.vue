<script setup>
import {computed, onMounted, ref} from "vue";
import {useStore} from "vuex";
import {useCookies} from "vue3-cookies";
import {useRoute} from "vue-router";

const store = useStore();
const route = useRoute();
const {cookies} = useCookies();

// Fetch categories from Vuex store
const categories = computed(() => store.getters["categories/categories"]);
const subCategoriesMap = computed(() => store.getters["categories/subCategoriesMap"]);

// State for user input fields
const firstName = ref('');
const lastName = ref('');
const email = ref('');

// State for selected categories
const selectedCategories = ref([]);
// State for selected email options
const selectedEmailTopicOptions = ref([]);

// Validate
const errors = ref({
  firstName: '',
  lastName: '',
  email: ''
});

function validateForm() {
  errors.value.firstName = firstName.value.trim() === '' ? 'First name is required' : '';
  errors.value.lastName = lastName.value.trim() === '' ? 'Last name is required' : '';
  errors.value.email = email.value.trim() === '' ? 'Email is required' : '';
  if (errors.value.email === '') {
    errors.value.email = /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email.value) ? '' : 'Invalid email address';
  }
  // errors.value.firstName !== '' || errors.value.lastName !== '' || errors.value.email !== ''
  return errors.value.firstName === '' && errors.value.lastName === '' && errors.value.email === '';
}

// Handle submit form
const isSubmitSuccess = ref(false);

function handleSubmit(xsrfToken) {
  if (validateForm()) {
    fetch("/bff/api/email-preferences", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        "X-XSRF-TOKEN": xsrfToken,
      },
      body: JSON.stringify({
        email: email.value,
        firstName: firstName.value,
        lastName: lastName.value,
        categoryIds: selectedCategories.value,
        emailTopicOptions: selectedEmailTopicOptions.value
      })
    }).then(response => {
      isSubmitSuccess.value = response.status === 201;
    });
  }
}

// Fetch categories when component mounts
onMounted(() => {
  // check route params
  if (route.query.email) {
    email.value = route.query.email;
  }

  store.dispatch("categories/fetchCategories");
});

// Format subcategories into a comma-separated string
const formattedSubCategories = (categoryId) => {
  const subCategories = subCategoriesMap.value[categoryId];
  if (subCategories && subCategories.length > 0) {
    return subCategories.map(subCategory => subCategory.name).join(', ');
  }
  return '';
};

function handleReset() {
  isSubmitSuccess.value = false;
}
</script>

<template>
  <p class="text-center fs-1 fw-bold" v-if="!isSubmitSuccess">Tell us about yourself</p>
  <p class="text-center fs-1 fw-bold" v-if="isSubmitSuccess">Thank You</p>
  <hr>
  <div class="container">
    <p v-if="isSubmitSuccess" class="fs-5 text-center">
      We saved your email preferences.
    </p>
    <p v-if="isSubmitSuccess" class="fs-5 text-center">
      To go back to your preferences,
      <router-link @click="handleReset" to="/email-preferences">click here</router-link>.
    </p>
    <div class="row" v-if="!isSubmitSuccess">
      <div class="col-6">
        <div class="row mb-3">
          <div class="col-6">
            <label for="first-name" class="form-label">First Name</label>
            <input v-model="firstName" type="text" class="form-control" id="first-name" placeholder="John">
            <span class="text-danger">{{ errors.firstName }}</span>
          </div>
          <div class="col-6">
            <label for="last-name" class="form-label">Last Name</label>
            <input v-model="lastName" type="text" class="form-control" id="last-name" placeholder="Doe">
            <span class="text-danger">{{ errors.lastName }}</span>
          </div>
        </div>
        <div class="mb-3">
          <label for="email" class="form-label">Email</label>
          <input v-model="email" type="email" class="form-control" id="email" placeholder="john.doe@example.com">
          <span class="text-danger">{{ errors.email }}</span>
        </div>

        <span class="fs-3 fw-bold">Please email me about your…</span>
        <hr class="m-auto">
        <ul class="list-group mt-3">
          <li class="list-group-item d-flex justify-content-start">
            <input class="form-check-input me-1" type="checkbox" value="WEEKLY_NEWSLETTER"
                   v-model="selectedEmailTopicOptions"
                   aria-label="...">
            <span class="fw-bold">Weekly Newsletter</span>
          </li>
          <li class="list-group-item d-flex justify-content-start">
            <input class="form-check-input me-1" type="checkbox" value="DEAL_OF_THE_DAY"
                   v-model="selectedEmailTopicOptions"
                   aria-label="...">
            <span class="fw-bold">Deal of the Day</span>
          </li>
          <li class="list-group-item d-flex justify-content-start">
            <input class="form-check-input me-1" type="checkbox" value="NEW_RELEASES"
                   v-model="selectedEmailTopicOptions"
                   aria-label="...">
            <span class="fw-bold">New Releases</span>
          </li>
        </ul>
      </div>

      <div class="col-6">
        <span class="fs-3 fw-bold">I'm interested In...</span>
        <hr class="m-auto">
        <ul class="list-group mt-3">
          <li class="list-group-item d-flex justify-content-start" v-for="category in categories">
            <input class="form-check-input me-1" type="checkbox" :value="category.id" v-model="selectedCategories"
                   aria-label="...">
            <div>
              <span class="d-block fw-bold">{{ category.name }}</span>
              <span class="d-block">{{ formattedSubCategories(category.id) }}</span>
            </div>
          </li>
        </ul>
      </div>
    </div>
    <div class="row d-flex justify-content-center mt-3" v-if="!isSubmitSuccess">
      <button @click="handleSubmit(cookies.get('XSRF-TOKEN'))" class="btn btn-primary w-25">Submit</button>
    </div>
  </div>
</template>

<style scoped>
/* Add your styles here */
</style>
