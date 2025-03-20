<template>
  <div class="hot">
    <h1>时事热点</h1>
    <div class="news-list">
      <div v-for="news in newsList" :key="news.id" class="news-item">
        <h2>{{ news.title }}</h2>
        <p>{{ news.summary }}</p>
        <router-link :to="`/news/${news.id}`">阅读更多</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

// 示例热点新闻数据
const newsList = ref([])
</script>

<style scoped>
.hot {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.news-list {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 20px;
}

.news-item {
  border: 1px solid #eee;
  padding: 15px;
  border-radius: 5px;
  background-color: #fff8f0;
}

.news-item h2 {
  margin-top: 0;
  color: #e67e22;
}

.news-item p {
  color: #666;
}

.news-item a {
  color: #e67e22;
  text-decoration: none;
}

.news-item a:hover {
  text-decoration: underline;
}
</style>
