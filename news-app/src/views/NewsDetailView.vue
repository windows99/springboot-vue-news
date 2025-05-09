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
              <el-button type="success" :icon="Share" @click="showShareDialog" round>
                分享
              </el-button>
              <el-button type="warning" :icon="WarningFilled" @click="showFeedbackDialog" round>
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
                      <el-button 
                        v-if="useUserStore.user.id === comment.userId"
                        type="text" 
                        size="small" 
                        @click="deleteComment(comment.id)"
                      >
                        删除
                      </el-button>
                    </el-space>
                    <div class="comment-text">{{ comment.content }}</div>
                  </div>
                </el-space>
              </el-card>
            </el-space>
          </el-space>
        </el-col>
      </el-row>
    </el-main>
  </el-container>

  <!-- 分享对话框 -->
  <el-dialog
    v-model="shareDialogVisible"
    title="分享文章"
    width="300px"
    center
  >
    <div class="share-dialog-content">
      <div class="qrcode-container">
        <div ref="qrcodeRef"></div>
      </div>
      <div class="share-tips">
        <p>扫描二维码分享到：</p>
        <div class="share-platforms">
          <el-button type="primary" plain @click="copyLink">
            <el-icon><Link /></el-icon>
            复制链接
          </el-button>
          <el-button type="success" plain @click="shareToWeChat">
            <el-icon><ChatDotRound /></el-icon>
            微信
          </el-button>
          <el-button type="info" plain @click="shareToQQ">
            <el-icon><ChatLineRound /></el-icon>
            QQ
          </el-button>
        </div>
      </div>
    </div>
  </el-dialog>

  <!-- 反馈对话框 -->
  <el-dialog
    v-model="feedbackDialogVisible"
    title="问题反馈"
    width="400px"
    center
  >
    <el-form :model="feedbackForm" label-width="80px">
      <el-form-item label="反馈内容">
        <el-input
          v-model="feedbackForm.content"
          type="textarea"
          :rows="4"
          placeholder="请详细描述您遇到的问题..."
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="feedbackDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitFeedback">
          提交反馈
        </el-button>
      </span>
    </template>
  </el-dialog>

  <el-backtop :right="100" :bottom="100" />
  <FooterBar />
</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount, h } from 'vue'
import NavBar from '../components/NavBar.vue'
import FooterBar from '../components/FooterBar.vue'
import { useRoute } from 'vue-router'
import { View, Star, Share, WarningFilled, Link, ChatDotRound, ChatLineRound } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { getNewsByIdUsingGet, updateNewsUsingPut } from "@/api/newsController"
import { useUserStoreHook } from '../stores/modules/user'
import { getCommentListUsingGet, addCommentUsingPost, deleteCommentByIdUsingDelete } from "@/api/commentController"
import {submitFeedbackUsingPost} from "@/api/newsFeedbackController"
import type { API } from '@/api/typings.d'
import QRCode from 'qrcodejs2-fix'

// 使用用户store
const useUserStore = useUserStoreHook()
const route = useRoute()

// 新闻详情数据
const news = ref<API.News>({
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
const comments = ref<API.Comment[]>([])

// 分享相关
const shareDialogVisible = ref(false)
const qrcodeRef = ref<HTMLElement | null>(null)
const currentUrl = window.location.href

// 反馈相关
const feedbackDialogVisible = ref(false)
const feedbackForm = ref({
  content: '',
  newsId: 0
})

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
  const newsId = BigInt(route.params.id).toString();
  const obj: API.getNewsByIdUsingGETParams = { 
    id: newsId,
    userId: useUserStore.user.id ? useUserStore.user.id : undefined
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
 * 显示分享对话框
 */
const showShareDialog = () => {
  shareDialogVisible.value = true
  // 在对话框显示后生成二维码
  setTimeout(() => {
    generateQRCode()
  }, 100)
}

/**
 * 生成二维码
 */
const generateQRCode = () => {
  if (qrcodeRef.value) {
    qrcodeRef.value.innerHTML = ''
    new QRCode(qrcodeRef.value, {
      text: currentUrl,
      width: 200,
      height: 200,
      colorDark: '#000000',
      colorLight: '#ffffff',
      correctLevel: QRCode.CorrectLevel.H
    })
  }
}

/**
 * 复制链接
 */
const copyLink = () => {
  navigator.clipboard.writeText(currentUrl).then(() => {
    ElMessage.success('链接已复制到剪贴板')
  }).catch(() => {
    ElMessage.error('复制失败，请手动复制')
  })
}

/**
 * 分享到微信
 */
const shareToWeChat = () => {
  // 微信分享需要配置微信JSSDK，这里仅作示例
  ElMessage.info('请使用微信扫描二维码分享')
}

/**
 * 分享到QQ
 */
const shareToQQ = () => {
  window.open(`http://connect.qq.com/widget/shareqq/index.html?url=${encodeURIComponent(currentUrl)}&title=${encodeURIComponent(news.value.title)}`)
}

/**
 * 显示反馈对话框
 */
const showFeedbackDialog = (): void => {
  if (!useUserStore.user.id) {
    ElMessage.error('请先登录')
    return
  }
  feedbackForm.value = {
    content: '',
    newsId: news.value.id
  }
  feedbackDialogVisible.value = true
}

/**
 * 提交反馈
 */
const submitFeedback = async (): Promise<void> => {
  if (!feedbackForm.value.content.trim()) {
    ElMessage.error('请输入反馈内容')
    return
  }

  try {
    const res = await submitFeedbackUsingPost(feedbackForm.value)
    if (res.code === 0) {
      ElMessage.success('反馈提交成功')
      feedbackDialogVisible.value = false
    } else {
      ElMessage.error(res.message || '反馈提交失败')
    }
  } catch (error) {
    ElMessage.error('反馈提交失败')
  }
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
      newsId: news.value.id,
      newsTitle: news.value.title
    })
    newComment.value = ''
    fetchComments()
  } else {
    ElMessage.error('请先登录')
  }
}

/**
 * 删除评论
 */
const deleteComment = async (commentId: number): Promise<void> => {
  try {
    const res = await deleteCommentByIdUsingDelete({ id: commentId })
    if (res.code === 0) {
      ElMessage.success('删除成功')
      fetchComments()
    } else {
      ElMessage.error(res.message || '删除失败')
    }
  } catch (error) {
    ElMessage.error('删除失败')
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

.comment-text{
  font-size: 14px;
  padding: 8px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.share-buttons {
  display: flex;
  gap: 10px;
}

.share-dialog-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20px;
}

.qrcode-container {
  padding: 10px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,0.1);
}

.share-tips {
  text-align: center;
}

.share-platforms {
  display: flex;
  gap: 10px;
  margin-top: 10px;
  justify-content: center;
}

.share-platforms .el-button {
  display: flex;
  align-items: center;
  gap: 5px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>
