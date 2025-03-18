<template>
  <router-view v-slot="{ Component }">
    <keep-alive include="news-detail,news-list">
      <component :is="Component" />
    </keep-alive>
  </router-view>
</template>

<script setup>

</script>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: #2c3e50;
}

.main-content {
  min-height: calc(100vh - 60px);
}
</style>
