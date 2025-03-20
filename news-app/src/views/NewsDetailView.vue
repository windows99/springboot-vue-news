<template>
  <NavBar />
  <el-container style="margin-top: 60px;">
    <el-main>
      <el-row justify="center">
        <el-col :span="18">
          <el-space direction="vertical" :size="24" fill>
            <!-- 标题区 -->
            <el-page-header @back="goBack">
              <template #content>
                <h2 style="margin: 0;">{{ news.title }}</h2>
              </template>
            </el-page-header>

            <!-- 元数据 -->
            <el-space>
              <el-text type="info">作者：{{ news.author }}</el-text>
              <el-text type="info">发布时间：{{ news.createtime }}</el-text>
              <el-icon>
                <View />
              </el-icon>
              <el-text type="info">浏览量：{{ news.viewcount }}</el-text>
            </el-space>

            <!-- 新闻内容 -->
            <el-card shadow="never">
              <div class="content" v-html="news.content"></div>
            </el-card>

            <!-- 互动操作区 -->
            <el-space :size="24" class="interaction-buttons">
              <el-button type="primary" :icon="Star" @click="handleLike" round>
                点赞 {{ news.likecount || 0 }}
              </el-button>
              <el-button type="success" :icon="Share" @click="handleShare" round>
                分享
              </el-button>
              <el-button type="warning" :icon="WarningFilled" @click="handleWarningFilled" round>
                反馈
              </el-button>
            </el-space>

            <!-- 评论输入 -->
            <el-card shadow="never">
              <template #header>
                <el-text size="large">评论（{{ comments.length }}）</el-text>
              </template>

              <el-input v-model="newComment" type="textarea" placeholder="写下你的评论..." :rows="3" resize="none" />
              <div style="margin-top: 10px;">
                <el-button type="primary" @click="submitComment">提交评论</el-button>
              </div>
            </el-card>

            <!-- 评论列表 -->
            <el-space direction="vertical" :size="16" fill>
              <el-card v-for="comment in comments" :key="comment.id" shadow="never" class="!border-1">
                <el-space alignment="flex-start" :size="12" fill>
                  <div>
                    <el-space :size="8">
                      <el-avatar :src="comment.userAvatar" />
                      <el-text strong>{{ comment.username }}</el-text>
                      <el-text type="info" size="small">{{ comment.createTime }}</el-text>
                    </el-space>
                    <div class="mt-2">{{ comment.content }}</div>
                  </div>
                </el-space>
              </el-card>
            </el-space>
          </el-space>
        </el-col>
      </el-row>
    </el-main>
  </el-container>
  <FooterBar />
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import NavBar from '../components/NavBar.vue'
import FooterBar from '../components/FooterBar.vue'
import { useRoute } from 'vue-router'
import { View, Star, Share, WarningFilled } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { getNewsByIdUsingGet, updateNewsUsingPut } from "@/api/newsController"
import { useUserStoreHook } from '../stores/modules/user'
import { getCommentListUsingGet, addCommentUsingPost } from "@/api/commentController"
import type { News, Comment } from '@/api/typings.d'

// 使用用户store
const useUserStore = useUserStoreHook()
const route = useRoute()

// 新闻详情数据
const news = ref<News>({
  id: 0,
  title: '',
  content: '',
  author: '',
  createtime: '',
  viewcount: 0
})
// 新评论内容
const newComment = ref<string>('')
// 评论列表
const comments = ref<Comment[]>([])



// 组件挂载时初始化
onMounted(async () => {
  await fetchNews()
  fetchComments()
  // 页面滚动到顶部
  window.scrollTo({
    top: 0,
    left: 0,
    behavior: "auto"
  });
})



/**
 * 获取新闻详情
 */
const fetchNews = async (): Promise<void> => {
  const newsId = route.params.id
  const obj = { 
    id: newsId,
    userId: useUserStore.user.id ? useUserStore.user.id : null 
  }
  const data = await getNewsByIdUsingGet(obj)
  if (data.code === 0) {
    news.value = data.data
  }
}

/**
 * 获取评论列表
 */
const fetchComments = async (): Promise<void> => {
  const obj = { newsId: news.value.id }
  const res = await getCommentListUsingGet(obj)
  if (res.code === 0) {
    comments.value = res.data
  }
}

/**
 * 处理点赞操作
 */
const handleLike = async (): Promise<void> => {
  if (useUserStore.user.id) {
    news.value.likecount = (news.value.likecount || 0) + 1
    const res = await updateNewsUsingPut({ id: news.value.id }, news.value)
    if (res.code === 0) {
      ElMessage.success('点赞成功')
    }
  } else {
    ElMessage.error('请先登录')
  }
}

/**
 * 返回上一页
 */
const goBack = (): void => {
  // 保存当前滚动位置
  sessionStorage.setItem('detailScrollPosition', window.scrollY.toString())
  window.history.back()
}

/**
 * 处理分享操作
 */
const handleShare = (): void => {
  ElMessage.success('分享功能开发中')
}

/**
 * 处理反馈操作
 */
const handleWarningFilled = (): void => {
  ElMessage.success('反馈功能开发中')
}

/**
 * 提交评论
 */
const submitComment = async (): Promise<void> => {
  if (!newComment.value.trim()) {
    ElMessage.error('评论内容不能为空')
    return
  }

  if (useUserStore.user.id) {
    await addCommentUsingPost({
      content: newComment.value,
      userId: useUserStore.user.id,
      newsId: news.value.id
    })
    newComment.value = ''
    fetchComments()
  } else {
    ElMessage.error('请先登录')
  }
}
</script>

<style scoped>
.content {
  line-height: 1.8;
}

:deep(.el-main) {
  --el-main-padding: 24px;
}

:deep(.el-card__header) {
  padding: 16px 20px;
}

:deep(.el-card__body) {
  padding: 20px;
}

.interaction-buttons {
  margin: 24px 0;
  justify-content: center;
  width: 100%;
}

.interaction-buttons .el-button {
  margin: 0 12px;
  padding: 12px 24px;
}
</style>
