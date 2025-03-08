<template>
  <div class="p-6 bg-white rounded-lg shadow">
    <el-breadcrumb separator="/" class="mb-6">
      <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item :to="{ path: '/news' }">新闻</el-breadcrumb-item>
      <el-breadcrumb-item>详情</el-breadcrumb-item>
    </el-breadcrumb>

    <div v-if="loading" class="flex justify-center py-10">
      <el-skeleton :rows="6" animated />
    </div>

    <div v-else>
      <h1 class="text-3xl font-bold mb-4">{{ news.title }}</h1>

      <div class="flex items-center text-sm text-gray-500 mb-6">
        <span class="mr-4">发布时间：{{ news.createtime }}</span>
        <span>作者：{{ news.author }}</span>
      </div>

      <div class="mb-6">
        <img :src="news.coverimage" alt="新闻封面" class="w-full h-96 object-coverimage rounded-lg">
      </div>

      <div class="prose max-w-none" v-html="news.content"></div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getNewsByIdUsingGet } from '@/api/newsController'

const route = useRoute()
const loading = ref(true)
console.log(route.params.id)

const news = ref({
  id: 0,
  title: '',
  content: '',
  coverimage: '',
  author: '',
  createtime: ''
})
const getNewsId = ref<API.getNewsByIdUsingGETParams>({})

onMounted(
  async () => {
    try {
      getNewsId.value.id = BigInt(route.params.id).toString()
      const res = await getNewsByIdUsingGet(getNewsId.value)
      news.value = res
    } catch (error) {
      console.error(error)
      ElMessage.error('获取新闻详情失败')
    } finally {
      loading.value = false
    }
  }
)
</script>

<style scoped>
.prose {
  line-height: 1.75;
}

.prose :deep(img) {
  max-width: 100%;
  height: auto;
  margin: 1rem 0;
  border-radius: 0.5rem;
}

.prose :deep(p) {
  margin: 1rem 0;
  color: #4a5568;
}

.prose :deep(h2) {
  font-size: 1.5rem;
  font-weight: 600;
  margin: 1.5rem 0 1rem;
  color: #2d3748;
}

.prose :deep(h3) {
  font-size: 1.25rem;
  font-weight: 600;
  margin: 1.25rem 0 0.75rem;
  color: #2d3748;
}
</style>
