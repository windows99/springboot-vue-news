<template>
  <NavBar />
  <el-container style="margin-top: 60px;">

    <!-- 主要内容区域 -->
    <el-main style="padding: 0;">
      <!-- 轮播图 -->
      <el-carousel height="400px" :interval="5000" arrow="always">
        <el-carousel-item v-for="item in carouselItems" :key="item.id">
          <img :src="item.image" :alt="item.title" class="carousel-image" />
        </el-carousel-item>
      </el-carousel>

      <!-- 新闻分类标签页 -->
      <el-row justify="center">
        <el-col :span="18">
          <el-tabs class="tabs-box" v-model="activeTab" type="card" @tab-click="handleTabClick">
            <el-tab-pane v-for="tab in tabs" :key="tab.id" :label="tab.tagname" :name="tab.id">

              <el-row :gutter="20">
                <el-col v-for="news in newsList" :key="news.id" :span="24" class="news-item">
                  <el-card shadow="hover" class="card-border">
                    <el-row>
                      <!-- 左侧图片 -->
                      <el-col :span="4">
                        <el-image :src="news.coverimage" fit="cover" :preview-src-list="[news.coverimage]"
                          @load="(e) => handleImageLoad(e, news)" ref="newsImage" />
                      </el-col>

                      <!-- 右侧内容 -->
                      <el-col :span="20" class="news-content">
                        <el-text @click="viewDetail(news.id)" class="hover-title" line-clamp="1" size="large">{{
                          news.title }}</el-text>
                        <br />
                        <el-text :line-clamp="news.lineClamp || 3" size="small" :style="{ height: news.textHeight }">
                          {{ htmlToText(news.content) }}
                        </el-text>
                        <br />
                      </el-col>
                    </el-row>
                  </el-card>
                </el-col>
              </el-row>

            </el-tab-pane>
          </el-tabs>
        </el-col>
      </el-row>

    </el-main>

  </el-container>

  <footer-bar />
</template>

<script setup>
import { ref, onMounted, shallowRef, onBeforeUnmount, nextTick } from 'vue'
import { getNewsListsUsingPost } from "@/api/newsController"
import { getTagListUsingGet } from "@/api/newsTagController"
import { useRouter } from 'vue-router'
import NavBar from '../components/NavBar.vue'
import FooterBar from '../components/FooterBar.vue'

const route = useRouter()
const newsList = ref(JSON.parse(sessionStorage.getItem('cachedNewsList') || '[]'))

// 处理图片加载
const handleImageLoad = (e, news) => {
  setTimeout(() => {
    const img = e.target?.querySelector('.el-image__inner') || e.target?.querySelector('img') || e.target
 
    if (!img) return

    // 确保图片完全加载
    if (!img.complete || img.naturalHeight === 0) {
      img.onload = () => {
        const imgHeight = img.clientHeight
        updateTextHeight(news, imgHeight)
      }
      return
    }

    const imgHeight = img.clientHeight
    updateTextHeight(news, imgHeight)
  }, 100)
}

const updateTextHeight = (news, imgHeight) => {
  const lineHeight = 20 // 每行文字高度
  const padding = 16 // 文字区域padding
  const availableHeight = imgHeight - padding

  // 计算最大行数，限制在1-5行之间
  const maxLines = Math.floor(availableHeight / lineHeight)
  news.lineClamp = Math.max(1, Math.min(maxLines, 5))

  // 设置文字区域高度与图片高度一致
  news.textHeight = `${imgHeight}px`
}

// 轮播图数据
const carouselItems = ref([
  {
    id: 1,
    image: 'src/assets/banner/1.jpg',
    title: '新闻1'
  },
  {
    id: 2,
    image: 'src/assets/banner/2.jpg',
    title: '新闻2'
  },
  {
    id: 3,
    image: 'src/assets/banner/3.jpg',
    title: '新闻3'
  }
])

const tabs = ref([
  { id: 1, name: 'all', tagname: '全部' },
])

// 标签页
const activeTab = ref(1)
const viewDetail = (id) => {
  sessionStorage.setItem('homeScrollPosition', window.scrollY)
  route.push('/news/' + id)
}

const htmlToText = (html) => {
  const parser = new DOMParser();
  const doc = parser.parseFromString(html, 'text/html');
  return doc.body.textContent || '';
}

const handleTabClick = (tab, event) => {
  fetchData(tab.props.name)
}

// 获取新闻列表
const fetchData = async (category) => {
  try {
    let params = {
      current: 1,
      pageSize: 10,
      status: 0,
      category: category === 1 ? null : category
    }

    const res = await getNewsListsUsingPost(params)
    if (res.code == 0) {
      newsList.value = res.data.records
    }
  } finally {
    console.log("获取新闻列表成功")
  }
}

// 获取新闻标签列表
const fetchTabsData = async () => {
  try {
    let params = {
      page: 1,
      pageSize: 100
    }
    const res = await getTagListUsingGet(params)
    if (res.code == 0) {
      tabs.value.push(...res.data)
    }
  } finally {
    console.log("err")
  }
}

onMounted(() => {
  fetchTabsData()
  if (newsList.value.length === 0) {
    fetchData(1)
  }

  const savedPosition = sessionStorage.getItem('homeScrollPosition')
  if (savedPosition) {
    window.scrollTo(0, Number(savedPosition))
    sessionStorage.removeItem('homeScrollPosition')
  }
})

onBeforeUnmount(() => {
  sessionStorage.setItem('cachedNewsList', JSON.stringify(newsList.value))
})
</script>

<style scoped>
.tabs-box .el-tabs__item {
  font-size: 20px;
}

.carousel-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.card-border {
  border-radius: 18px;
}

.news-content {
  padding-left: 20px;
}

.news-description {
  color: #666;
  margin-bottom: 10px;
}

.news-time {
  color: #999;
  font-size: 12px;
}

.footer-content {
  text-align: center;
  padding: 20px;
}

.user-avatar {
  float: right;
}

.hover-title:hover {
  color: #409eff;
  cursor: pointer;
}
</style>
