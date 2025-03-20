<template>
  <NavBar />
  <el-container style="margin-top: 60px;" class="main-bg">

    <!-- 主要内容区域 -->
    <el-main style="padding: 0;">


      <!-- 新闻分类标签页 -->
      <el-row justify="center">
        <el-col :span="18">
          <!-- 轮播图 -->
          <el-carousel height="600px" :interval="5000" arrow="always">
            <el-carousel-item v-for="item in carouselItems" :key="item.id">
              <div class="carousel-container" @click="viewDetail(item.id)">
                <img :src="item.coverimage" :alt="item.title" class="carousel-image" />
                <div class="carousel-title">
                  <el-text class="title-text" line-clamp="1" size="large">{{ item.title }}</el-text>
                </div>
              </div>
            </el-carousel-item>
          </el-carousel>
        </el-col>
        <el-col :span="18">
          <el-tabs class="tabs-box" v-model="activeTab" type="card" @tab-click="handleTabClick">
            <el-tab-pane v-for="tab in tabs" :key="tab.id" :label="tab.tagname" :name="tab.id">

              <el-row :gutter="20">
                <el-col style="margin-bottom: 10px;" v-for="news in newsList" :key="news.id" :span="24"
                  class="news-item">
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

              <!-- 加载状态 -->
              <el-row justify="center" style="margin: 20px 0;">
                <el-col :span="24" style="text-align: center;">
                  <el-text v-if="loading" type="info">加载中...</el-text>
                  <el-text v-if="noMore" type="info">没有更多数据了</el-text>
                  <el-button v-if="!noMore && !loading" type="primary" @click="loadMore" style="margin-top: 20px;">
                    加载更多
                  </el-button>
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

<script setup lang="ts">
import { ref, onMounted, shallowRef, onBeforeUnmount, nextTick, computed } from 'vue'
import { getNewsListsUsingPost, getTop3NewsByViewCountUsingGet } from "@/api/newsController"
import { getTagListUsingGet } from "@/api/newsTagController"
import { useRouter } from 'vue-router'
import NavBar from '../components/NavBar.vue'
import FooterBar from '../components/FooterBar.vue'

// 使用Vue Router进行路由导航
const route = useRouter()
// 新闻列表数据，从sessionStorage中读取缓存，避免页面刷新时数据丢失
const newsList = ref(JSON.parse(sessionStorage.getItem('cachedNewsList') || '[]'))

// 当前页码，用于分页加载
const currentPage = ref<number>(1)
// 总页数，从接口返回
const totalPages = ref<number>(0)
// 加载状态，防止重复请求
const loading = ref<boolean>(false)
// 计算属性：判断是否还有更多数据可以加载
const noMore = computed(() => {
  return currentPage.value >= totalPages.value
})

// 处理图片加载完成事件
// 根据图片高度动态调整新闻摘要的显示行数
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

// 根据图片高度更新文本区域高度
// 确保新闻摘要的显示区域与图片高度一致
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

// 轮播图数据，包含图片URL和标题
// 初始数据会被接口返回的数据覆盖
interface CarouselItem {
  id: number
  coverimage: string
  title: string
}

interface News {
  id?: number
  coverimage?: string
  title?: string
  [key: string]: any
}

interface NewsTag {
  id: number
  tagname: string
  [key: string]: any
}

const carouselItems = ref<(CarouselItem | News)[]>([
  {
    id: 1,
    coverimage: 'src/assets/banner/1.jpg',
    title: '新闻1'
  },
  {
    id: 2,
    coverimage: 'src/assets/banner/2.jpg',
    title: '新闻2'
  },
  {
    id: 3,
    coverimage: 'src/assets/banner/3.jpg',
    title: '新闻3'
  }
])

// 新闻分类标签页数据
// 初始包含"全部"标签，其他标签从接口获取
interface TabItem {
  id: number
  tagname: string
  [key: string]: any
}

const tabs = ref<TabItem[]>([
  { id: 1, tagname: '全部' }
])

// 当前激活的标签页，默认显示第一个标签
const activeTab = ref<Number>(1)
// 查看新闻详情
// 跳转前保存当前滚动位置，以便返回时恢复
const viewDetail = (id: number) => {
  sessionStorage.setItem('homeScrollPosition', window.scrollY.toString())
  route.push('/news/' + id.toString())
}

// 将HTML内容转换为纯文本
// 用于显示新闻摘要时去除HTML标签
const htmlToText = (html) => {
  const parser = new DOMParser();
  const doc = parser.parseFromString(html, 'text/html');
  return doc.body.textContent || '';
}

// 处理标签页点击事件
// 切换标签时重置页码并重新获取数据
const handleTabClick = (tab, event) => {
  currentPage.value = 1
  fetchData(tab.props.name)
}


// 获取轮播图数据
const fetchBanner = async () => {
  try {
    const res = await getTop3NewsByViewCountUsingGet()
    if (res.code == 0) {
      carouselItems.value = res.data
    }
  } catch (error) {
    console.error('获取轮播图数据失败：', error)
  }
}


// 获取新闻列表数据
// category: 新闻分类
// isLoadMore: 是否为加载更多操作
const fetchData = async (category, isLoadMore = false) => {
  if (loading.value) return
  loading.value = true

  try {
    let params = {
      pageSize: 10,
      status: 1,
      category: category === 1 ? null : category,
      current: currentPage.value
    }

    const res = await getNewsListsUsingPost(params)
    if (res.code == 0) {
      if (isLoadMore) {
        newsList.value.push(...res.data.records)
      } else {
        newsList.value = res.data.records
      }
      totalPages.value = res.data.pages
      currentPage.value = +res.data.current
    }
  } finally {
    loading.value = false
  }
}

// 加载更多新闻
// 增加页码并触发数据获取
const loadMore = () => {
  currentPage.value += 1
  fetchData(activeTab.value, true)
}

// 处理页面滚动事件，实现无限滚动加载
// 当滚动到底部时自动加载更多数据
const handleScroll = () => {
  const bottomOfWindow =
    document.documentElement.scrollTop + window.innerHeight >=
    document.documentElement.offsetHeight - 100

  if (bottomOfWindow && !loading.value && !noMore.value) {
    loadMore()
  }
}

// 组件挂载时初始化
// 获取轮播图数据、标签数据和新闻列表
onMounted(() => {
  window.addEventListener('scroll', handleScroll)
})

// 组件卸载前保存数据
// 将当前新闻列表缓存到sessionStorage中
onBeforeUnmount(() => {
  window.removeEventListener('scroll', handleScroll)
})

// 获取新闻分类标签数据
// 用于初始化标签页
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
  fetchBanner()
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
.main-bg {
  /* background-image: url("../assets/bg01.jpg");
  background-attachment: fixed; */
  /* background-color: silver;
  background-image:
    radial-gradient(circle at 100% 150%, silver 24%, white 24%, white 28%, silver 28%, silver 36%, white 36%, white 40%, transparent 40%, transparent),
    radial-gradient(circle at 0 150%, silver 24%, white 24%, white 28%, silver 28%, silver 36%, white 36%, white 40%, transparent 40%, transparent),
    radial-gradient(circle at 50% 100%, white 10%, silver 10%, silver 23%, white 23%, white 30%, silver 30%, silver 43%, white 43%, white 50%, silver 50%, silver 63%, white 63%, white 71%, transparent 71%, transparent),
    radial-gradient(circle at 100% 50%, white 5%, silver 5%, silver 15%, white 15%, white 20%, silver 20%, silver 29%, white 29%, white 34%, silver 34%, silver 44%, white 44%, white 49%, transparent 49%, transparent),
    radial-gradient(circle at 0 50%, white 5%, silver 5%, silver 15%, white 15%, white 20%, silver 20%, silver 29%, white 29%, white 34%, silver 34%, silver 44%, white 44%, white 49%, transparent 49%, transparent);
  background-size: 100px 50px; */
}

.tabs-box .el-tabs__item {
  font-size: 20px;
}

.carousel-container {
  position: relative;
  width: 100%;
  height: 100%;
  cursor: pointer;
}

.carousel-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.carousel-title {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: rgba(0, 0, 0, 0.5);
  padding: 16px;
}

.title-text {
  color: #fff;
  font-weight: bold;
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
