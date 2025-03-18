<template>
  <nav-bar />
  <el-container style="margin-top: 60px;" class="main-bg">
    <el-main style="padding: 0;">
      <el-row justify="center" class="news-container">
        <el-col :span="18" v-for="news in newsList" :key="news.id" class="news-item">
          <el-card shadow="hover" class="card-border">
            <el-row>
              <!-- 左侧图片 -->
              <el-col :span="4">
                <el-image :src="news.coverimage" fit="cover" :preview-src-list="[news.coverimage]" />
              </el-col>

              <!-- 右侧内容 -->
              <el-col :span="20" class="news-content">
                <el-text @click="viewDetail(news.id)" class="hover-title" line-clamp="1" size="large">{{ news.title
                }}</el-text>
                <br />
                <el-text line-clamp="3" size="small">{{ htmlToText(news.content) }}</el-text>
                <br />

              </el-col>
            </el-row>


          </el-card>
        </el-col>
      </el-row>
      <!-- 加载状态 -->
      <el-row justify="center" style="margin: 20px 0;">
        <el-col :span="24" style="text-align: center;">
          <el-text v-if="loading" style="color: aliceblue;">加载中...</el-text>
          <el-text v-if="noMore" style="color: aliceblue;">没有更多数据了</el-text>
          <el-button v-if="!noMore && !loading" type="primary" @click="loadMore" style="margin-top: 20px;">
            加载更多
          </el-button>
        </el-col>
      </el-row>

    </el-main>
  </el-container>
  <FooterBar />
</template>

<script setup>
import { onMounted, ref, computed, onBeforeUnmount } from 'vue'
import { getNewsListsUsingPost } from "@/api/newsController"
import { useRouter } from 'vue-router'
import NavBar from '../components/NavBar.vue'
import FooterBar from '../components/FooterBar.vue'

const route = useRouter()

const newsList = ref([])
const currentPage = ref(1)
const totalPages = ref(0)
const loading = ref(false)
const noMore = computed(() => {
  if (newsList.value.length === 0) return false
  return currentPage.value >= totalPages.value
})

// 获取新闻列表
const fetchData = async (category, isLoadMore = false) => {
  if (loading.value) return
  loading.value = true
  try {
    // 合并分页参数和查询条件
    let params = {
      current: currentPage.value,
      pageSize: 10,
      status: 1,
      category: category === 1 ? null : category
    }

    const res = await getNewsListsUsingPost(params)
    if (res.code == 0) {
      if (isLoadMore) {
        newsList.value.push(...res.data.records)
      } else {
        newsList.value = res.data.records
      }
      totalPages.value = res.data.pages
    }
  } finally {
    loading.value = false
  }
}
const viewDetail = (id) => {
  route.push('/news/' + id)
}

const htmlToText = (html) => {
  const parser = new DOMParser();
  const doc = parser.parseFromString(html, 'text/html');
  return doc.body.textContent || '';
}


// 加载更多新闻
const loadMore = () => {
  currentPage.value += 1
  fetchData(1, true)
}

// 处理页面滚动事件，实现无限滚动加载
const handleScroll = () => {
  const bottomOfWindow =
    document.documentElement.scrollTop + window.innerHeight >=
    document.documentElement.offsetHeight - 100

  if (bottomOfWindow && !loading.value && !noMore.value) {
    loadMore()
  }
}

onMounted(() => {
  window.addEventListener('scroll', handleScroll)
  fetchData(1)
})

onBeforeUnmount(() => {
  window.removeEventListener('scroll', handleScroll)
})
</script>

<style scoped>
.main-bg {
  background-image: url("../assets/bg01.jpg");
  background-attachment: fixed;
}

.news-container {
  margin-top: 10px;
}

.card-border {
  border-radius: 18px;
}

.news-item {
  margin-bottom: 10px;
}

.news-content {
  padding-left: 20px;
}

.news-title {
  font-size: 22px;
  font-weight: 500;
}

.hover-title:hover {
  color: #409eff;
  cursor: pointer;
}
</style>
