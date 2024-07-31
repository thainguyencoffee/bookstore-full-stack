<script setup>

</script>

<template>
<div class="row mt-2">
  <div class="col-md-8">
    <div id="carouselExampleAutoplaying" class="carousel slide" data-bs-ride="carousel">
      <div class="carousel-inner">
        <div class="carousel-item active">
          <img src="https://d3design.vn/uploads/Anh_bia_summer_sale_holiday_podium_display_on_yellow_background.jpg" class="d-block w-100" alt="...">
        </div>
        <div class="carousel-item">
          <img src="https://d3design.vn/uploads/Anh_bia_summer_sale_holiday_podium_display_on_yellow_background.jpg" class="d-block w-100" alt="...">
        </div>
        <div class="carousel-item">
          <img src="https://d3design.vn/uploads/Anh_bia_summer_sale_holiday_podium_display_on_yellow_background.jpg" class="d-block w-100" alt="...">
        </div>
      </div>
      <button class="carousel-control-prev" type="button" data-bs-target="#carouselExampleAutoplaying" data-bs-slide="prev">
        <span class="carousel-control-prev-icon" aria-hidden="true"></span>
        <span class="visually-hidden">Previous</span>
      </button>
      <button class="carousel-control-next" type="button" data-bs-target="#carouselExampleAutoplaying" data-bs-slide="next">
        <span class="carousel-control-next-icon" aria-hidden="true"></span>
        <span class="visually-hidden">Next</span>
      </button>
    </div>
  </div>
  <div class="d-none d-md-block col-md-4">
    <div class="row h-50 pb-1">
      <img src="https://d3design.vn/uploads/Anh_bia_summer_sale_holiday_podium_display_on_yellow_background.jpg" alt="banner-top" class="img-fluid">
    </div>
    <div class="row h-50 pt-1">
      <img src="https://d3design.vn/uploads/Anh_bia_summer_sale_holiday_podium_display_on_yellow_background.jpg" alt="banner-bottom" class="img-fluid">
    </div>
  </div>
</div>
</template>

<style scoped>
.carousel-item img {
  object-fit: cover;
}
</style>