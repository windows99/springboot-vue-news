<template>
  <nav-bar />
  <el-container style="margin-top: 60px;" class="main-bg">

    <!-- <el-affix :offset="60">
      
    </el-affix> -->
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
                <el-text @click="viewDetail(news.id)" class="hover-title" line-clamp="1" size="large">
                  {{ news.title }}
                </el-text>
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
  <el-backtop :right="100" :bottom="100" />
  <FooterBar />
</template>

<script setup lang="ts">
import { onMounted, ref, computed, onBeforeUnmount, h } from 'vue'
// import { getNewsListsUsingPost } from "@/api/newsController"

import { getPersonalRecommendUsingGet, getRecommendForUserUsingGet, getHotNewsUsingGet } from "@/api/newsRecommendController"
import { useRouter } from 'vue-router'
import NavBar from '../components/NavBar.vue'
import FooterBar from '../components/FooterBar.vue'
import { useUserStoreHook } from '../stores/modules/user'
import { ElNotification, ElButton } from 'element-plus'

const useUserStore = useUserStoreHook()
const route = useRouter()


const newsList = ref<Object[]>([])
const currentPage = ref<Number>(1)
const totalPages = ref<Number>(0)
const loading = ref<Boolean>(false)


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
    }
    let res = null
    if (useUserStore.user.id) {
      //  用户登录，返回该用户个性推荐新闻
      res = await getRecommendForUserUsingGet({ ...params, userId: useUserStore.user.id })
    } else {
      // 未登录，返回热门新闻
      res = await getHotNewsUsingGet(params)
    }
    if (res.code == 0) {
      if (isLoadMore) {
        newsList.value.push(...res.data.records)
      } else {
        newsList.value = res.data.records
      }
      totalPages.value = res.data.size
    }
  } finally {
    loading.value = false
  }
}

/**
 *    文章详情
 * @param id 
 */
const viewDetail = (id) => {
  route.push('/news/' + id)
}


/**
 *   html转text
 * @param html 
 */
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

const websocket = ref<WebSocket | null>(null)

onMounted(() => {
  window.addEventListener('scroll', handleScroll)
  fetchData(1)

  // 建立WebSocket连接
  if (useUserStore.user.id) {
    const wsUrl = `ws://localhost:8101/api/websocket/news/${useUserStore.user.id}`
    websocket.value = new WebSocket(wsUrl)

    websocket.value.onmessage = (event) => {

      const newNews = JSON.parse(event.data)
      if (newNews.type === 2) {
        const checked = ref<boolean | string | number>(false)
        const notification = ElNotification({
          title: '您有新的推送，是否加载？',
          offset: 60,
          // Should pass a function if VNode contains dynamic props
          message: () =>
            [h(ElButton, {
              type: 'primary',
              onClick: (e) => {
                // 将新新闻添加到列表最前面
                newsList.value.unshift(...newNews.data)
                notification.close()
              }
            },
              '加载'
            ), h(ElButton, {
              type: 'danger',
              onClick: (e) => {
                // 将新新闻添加到列表最前面
                notification.close()
              }
            },
              '取消'
            )]

        })
      }
    }

    websocket.value.onerror = (error) => {
      console.error('WebSocket error:', error)
    }
  }
})

onBeforeUnmount(() => {
  window.removeEventListener('scroll', handleScroll)
  // 关闭WebSocket连接
  if (websocket.value) {
    websocket.value.close()
  }
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
