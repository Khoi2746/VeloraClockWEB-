<script setup>
// XÓA DÒNG IMPORT INDEX CŨ ĐI
// Vì Router sẽ tự động nạp Index hoặc NewArrival vào khung này
</script>

<template>
  <router-view />
</template>

<style>
/* Ku em có thể để CSS chung cho toàn bộ dự án ở đây */
body {
  margin: 0;
  padding: 0;
  background-color: #4a423b; /* Màu nền đồng bộ với Velora */
}
</style>