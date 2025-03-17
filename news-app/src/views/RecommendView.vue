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
                <el-text line-clamp="1">{{ news.title }}</el-text>
                <br />
                <el-text line-clamp="3" size="small">{{ htmlToText(news.content) }}</el-text>
                <br />
                <el-button type="primary" size="small" @click="viewDetail(news.id)">
                  查看更多
                </el-button>
              </el-col>
            </el-row>
          </el-card>
        </el-col>
      </el-row>
    </el-main>
  </el-container>
  <FooterBar />
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { getNewsListsUsingPost } from "@/api/newsController"
import { useRouter } from 'vue-router'
import NavBar from '../components/NavBar.vue'
import FooterBar from '../components/FooterBar.vue'

const route = useRouter()

const newsList = ref([])

// 获取新闻列表
const fetchData = async (category) => {
  try {
    // 合并分页参数和查询条件
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
const viewDetail = (id) => {
  route.push('/news/' + id)
}

const htmlToText = (html) => {
  const parser = new DOMParser();
  const doc = parser.parseFromString(html, 'text/html');
  return doc.body.textContent || '';
}


onMounted(() => {
  fetchData(1)
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
</style>
