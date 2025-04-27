<template>
  <NavBar />
  <div class="content">
    <el-row class="el-row" justify="center" :gutter="20">
      <el-col :span="18">
        <el-card class="box-card" shadow="hover">
          <el-row>
            <!-- 左侧菜单 -->
            <el-col :span="5">
              <el-menu
                :default-active="activeMenu"
                class="profile-menu"
                @select="handleMenuSelect"
              >
                <el-menu-item index="info">
                  <el-icon><User /></el-icon>
                  <span>个人信息</span>
                </el-menu-item>
                <el-menu-item index="comments">
                  <el-icon><ChatDotRound /></el-icon>
                  <span>我的评论</span>
                </el-menu-item>
                <el-menu-item index="history">
                  <el-icon><Document /></el-icon>
                  <span>历史浏览</span>
                </el-menu-item>
                <el-menu-item index="tags">
                  <el-icon><CollectionTag /></el-icon>
                  <span>兴趣标签</span>
                </el-menu-item>
              </el-menu>
            </el-col>
            
            <!-- 右侧内容区 -->
            <el-col :span="19">
              <div class="content-wrapper">
                <!-- 个人信息 -->
                <div v-if="activeMenu === 'info'" class="content-panel">
                  <div class="user-header">
                    <el-avatar :size="100" :src="user.userAvatar" class="user-avatar" />
                    <div class="user-info">
                      <h2>{{ user.userName }}</h2>
                      <p>{{ user.userProfile || '暂无个人简介' }}</p>
                      <el-button @click="toEditInfo" type="primary" round>
                        <el-icon><Edit /></el-icon>
                        编辑个人资料
                      </el-button>
                    </div>
                  </div>
                  
                  <el-divider />
                  
                  <div class="info-section">
                    <h3>基本信息</h3>
                    <div class="info-item">
                      <span class="label">简介：</span>
                      <span>{{ user.userProfile || '暂无' }}</span>
                    </div>
                    <div class="info-item">
                      <span class="label">性别：</span>
                      <span>{{ getUserGender(user.gender) }}</span>
                    </div>
                    <div class="info-item">
                      <span class="label">邮箱：</span>
                      <span>{{ user.email || '暂无' }}</span>
                    </div>
                  </div>
                  
                  <el-divider />
                  
                  <div class="info-section">
                    <h3>其他信息</h3>
                    <div class="info-item">
                      <span class="label">用户角色：</span>
                      <span>{{ user.userRole || '普通用户' }}</span>
                    </div>
                    <div class="info-item">
                      <span class="label">注册时间：</span>
                      <span>{{ user.createTime || '未知' }}</span>
                    </div>
                  </div>
                </div>
                
                <!-- 我的评论 -->
                <div v-if="activeMenu === 'comments'" class="content-panel">
                  <div class="panel-header">
                    <h3>我的评论</h3>
                  </div>
                  <el-table 
                    :data="comments" 
                    style="width: 100%" 
                    v-loading="commentsLoading"
                    empty-text="暂无评论数据"
                    border
                    stripe
                    highlight-current-row
                  >
                    <el-table-column prop="content" label="评论内容" show-overflow-tooltip />
                    <el-table-column prop="newsTitle" label="新闻标题" show-overflow-tooltip />
                    <el-table-column prop="createTime" label="评论时间" width="180" />
                    <el-table-column label="操作" width="150" fixed="right">
                      <template #default="scope">
                        <el-button size="small" type="primary" @click="handleViewComment(scope.row)">
                          查看
                        </el-button>
                        <el-button type="danger" size="small" @click="handleDeleteComment(scope.row.id)">
                          删除
                        </el-button>
                      </template>
                    </el-table-column>
                  </el-table>
                </div>
                
                <!-- 历史浏览 -->
                <div v-if="activeMenu === 'history'" class="content-panel">
                  <div class="panel-header">
                    <h3>浏览记录</h3>
                    <div class="panel-actions">
                      <el-button 
                        type="danger" 
                        @click="handleClearAll" 
                        :disabled="!viewHistory.length"
                        round
                      >
                        <el-icon><Delete /></el-icon>
                        全部清空
                      </el-button>
                    </div>
                  </div>
                  
                  <el-table 
                    :data="viewHistory" 
                    style="width: 100%" 
                    v-loading="historyLoading"
                    empty-text="暂无浏览记录"
                    border
                    stripe
                    highlight-current-row
                  >
                    <el-table-column prop="newsTitle" label="标题" show-overflow-tooltip />
                    <el-table-column prop="viewTime" label="浏览时间" width="180" />
                    <el-table-column label="操作" width="150" fixed="right">
                      <template #default="scope">
                        <el-button size="small" type="primary" @click="handleViewHistory(scope.row)">
                          查看
                        </el-button>
                        <el-button type="danger" size="small" @click="handleDeleteHistory(scope.row)">
                          删除
                        </el-button>
                      </template>
                    </el-table-column>
                  </el-table>
                </div>
                
                <!-- 兴趣标签 -->
                <div v-if="activeMenu === 'tags'" class="content-panel">
                  <div class="panel-header">
                    <h3>兴趣标签</h3>
                  </div>
                  <p class="tags-desc">选择您感兴趣的新闻标签，我们将为您推荐相关内容</p>
                  
                  <div class="tags-container">
                    <el-tag
                      v-for="tag in availableTags"
                      :key="tag.id"
                      :type="userTags.includes(tag.id) ? 'primary' : 'info'"
                      class="tag-item"
                      :effect="userTags.includes(tag.id) ? 'dark' : 'plain'"
                      @click="toggleTag(tag.id)"
                    >
                      {{ tag.tagName }}
                    </el-tag>
                  </div>
                  
                  <div class="tags-action">
                    <el-button type="primary" @click="saveUserTags" round>
                      <el-icon><Check /></el-icon>
                      保存标签
                    </el-button>
                  </div>
                </div>
              </div>
            </el-col>
          </el-row>
          
          <!-- 编辑信息对话框 -->
          <el-dialog
            v-model="editFormVisible"
            title="编辑用户信息"
            width="580px"
            destroy-on-close
            draggable
          >
            <el-form :model="currentUser" label-position="top">
              <el-form-item label="用户名">
                <el-input v-model="currentUser.userName" placeholder="请输入用户名" />
              </el-form-item>
              
              <el-form-item label="头像">
                <upload-avatar
                  v-model:img-url="currentUser.userAvatar"
                  biz="user_avatar"
                />
              </el-form-item>
              
              <el-form-item label="简介">
                <el-input
                  v-model="currentUser.userProfile"
                  type="textarea"
                  rows="4"
                  placeholder="请输入个人简介"
                />
              </el-form-item>
              
              <el-form-item label="性别">
                <el-radio-group v-model="currentUser.gender">
                  <el-radio :label="0">女</el-radio>
                  <el-radio :label="1">男</el-radio>
                </el-radio-group>
              </el-form-item>
            </el-form>
            
            <template #footer>
              <span class="dialog-footer">
                <el-button @click="cancelEdit">取消</el-button>
                <el-button type="primary" @click="confirmEdit">确认</el-button>
              </span>
            </template>
          </el-dialog>
        </el-card>
      </el-col>
    </el-row>
  </div>
  <FooterBar />
</template>

<script lang="ts" setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  User, 
  ChatDotRound, 
  Document, 
  CollectionTag, 
  Edit, 
  Delete, 
  Check 
} from '@element-plus/icons-vue'
import NavBar from '@/components/NavBar.vue'
import FooterBar from '@/components/FooterBar.vue'
import UploadAvatar from '@/components/ImageUploader/UploaderAvatar.vue'
import { useUserStore } from '@/stores/modules/user'
import { updateMyUserUsingPost } from '@/api/userController'
import { getCommentListUsingGet, deleteCommentByIdUsingDelete } from '@/api/commentController'
import { 
  getViewHistoryUsingGet, 
  deleteViewByIdUsingPost, 
  deleteAllViewsByUserIdUsingPost 
} from '@/api/userNewsViewController'
import { message } from '@/utils/message'
import { setToken } from '@/utils/auth'
import { getTagListUsingGet } from '@/api/newsTagController'
import { getUserTagsUsingGet, updateUserTagsUsingPost } from '@/api/userTagController'

defineOptions({
  name: 'ProfileCenter'
})

const router = useRouter()
const userStore = useUserStore()

// 当前登录用户
const user = ref<API.User>({})

// 菜单相关
const activeMenu = ref('info')
const handleMenuSelect = (index: string) => {
  activeMenu.value = index
  if (index === 'comments') {
    fetchComments()
  } else if (index === 'history') {
    fetchViewHistory()
  } else if (index === 'tags') {
    fetchUserTags()
  }
}

// 获取当前登录用户信息
const getCurrentUser = () => {
  const userLogin = userStore.$state.user
  // 未登录,禁止访问
  if (userLogin == null) {
    ElMessage.error('请先登录')
    return router.push('/login')
  }
  // 已登录
  user.value = userLogin
}

// 用户性别
const getUserGender = computed(() => (gender: number): string => {
  if (gender === 0) {
    return '女'
  } else if (gender === 1) {
    return '男'
  } else if (gender === 2) {
    return '未知'
  } else {
    return '未填写'
  }
})

// 编辑用户信息
const editFormVisible = ref(false)
// 当前修改用户
const currentUser = ref<API.LoginUserVO>({})
// 修改用户对话框
const toEditInfo = () => {
  editFormVisible.value = true
  // 浅拷贝修改
  currentUser.value = Object.assign({}, user.value)
}
// 取消修改
const cancelEdit = () => {
  editFormVisible.value = false
  currentUser.value = {}
}
// 确认修改
const confirmEdit = async () => {
  try {
    const res = await updateMyUserUsingPost(currentUser.value)
    if (res.code === 0) {
      message('修改成功', { type: 'success' })
      setToken({
        user: currentUser.value,
        roles: [currentUser.value.userRole]
      } as any)
      // 拷贝回去
      user.value = Object.assign({}, currentUser.value)
      cancelEdit()
    } else {
      message(res.message, { type: 'error' })
    }
  } catch (error) {
    console.error('更新用户信息失败', error)
    message('更新失败', { type: 'error' })
  }
}

// 评论相关功能
const comments = ref([])
const commentsLoading = ref(false)
const fetchComments = async () => {
  commentsLoading.value = true
  try {
    // 获取全部评论并筛选当前用户的评论
    const res = await getCommentListUsingGet({ 
      page: 1,
      pageSize: 50
    })
    comments.value = res.data?.filter(comment => comment.userId === user.value.id) || []
  } catch (error) {
    console.error('获取评论失败', error)
    ElMessage.error('获取评论失败')
  } finally {
    commentsLoading.value = false
  }
}

const handleViewComment = (row: any) => {
  router.push(`/news/${row.newsId}`)
}

const handleDeleteComment = async (id: number) => {
  try {
    await ElMessageBox.confirm('确定要删除这条评论吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteCommentByIdUsingDelete(id)
    ElMessage.success('删除成功')
    fetchComments()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除评论失败', error)
      ElMessage.error('删除失败')
    }
  }
}

// 历史浏览相关功能
const viewHistory = ref([])
const historyLoading = ref(false)
const fetchViewHistory = async () => {
  historyLoading.value = true
  try {
    const userId = user.value.id as number
    // 按正确的参数格式传递
    const res = await getViewHistoryUsingGet({
      userId,
      params: {}
    })
    viewHistory.value = res.data || []
  } catch (error) {
    console.error('获取浏览记录失败', error)
    ElMessage.error('获取浏览记录失败')
  } finally {
    historyLoading.value = false
  }
}

const handleViewHistory = (row: any) => {
  router.push(`/news/${row.newsId}`)
}

const handleDeleteHistory = async (row: any) => {
  try {
    await ElMessageBox.confirm('确定要删除这条浏览记录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteViewByIdUsingPost({ viewId: row.id })
    ElMessage.success('删除成功')
    fetchViewHistory()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除浏览记录失败', error)
      ElMessage.error('删除失败')
    }
  }
}

const handleClearAll = async () => {
  try {
    await ElMessageBox.confirm('确定要清空所有浏览记录吗？此操作不可恢复！', '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteAllViewsByUserIdUsingPost({ userId: user.value.id as number })
    ElMessage.success('清空成功')
    fetchViewHistory()
  } catch (error) {
    // 用户取消操作
  }
}

// 兴趣标签相关功能
const availableTags = ref([])
const userTags = ref<number[]>([])

// 获取所有标签及用户标签
const fetchUserTags = async () => {
  try {
    // 获取所有标签
    const tagsRes = await getTagListUsingGet({})
    if (tagsRes.data) {
      availableTags.value = tagsRes.data
      
      // 获取用户标签
      const userTagsRes = await getUserTagsUsingGet({ userId: user.value.id as number })
      if (userTagsRes.data) {
        userTags.value = userTagsRes.data
      } else {
        userTags.value = []
      }
    }
  } catch (error) {
    console.error('获取标签失败', error)
    ElMessage.error('获取标签失败')
    
    // 如果API不可用，使用模拟数据
    availableTags.value = [
      { id: 1, tagName: '科技' },
      { id: 2, tagName: '财经' },
      { id: 3, tagName: '体育' },
      { id: 4, tagName: '娱乐' },
      { id: 5, tagName: '政治' },
      { id: 6, tagName: '教育' },
      { id: 7, tagName: '健康' },
      { id: 8, tagName: '旅游' }
    ]
    userTags.value = [1, 3, 6]
  }
}

const toggleTag = (tagId: number) => {
  const index = userTags.value.indexOf(tagId)
  if (index > -1) {
    userTags.value.splice(index, 1)
  } else {
    userTags.value.push(tagId)
  }
}

const saveUserTags = async () => {
  try {
    // 调用更新用户标签接口
    const res = await updateUserTagsUsingPost({
      userId: user.value.id as number,
      tagIds: userTags.value
    })
    
    if (res.code === 0) {
      ElMessage.success('标签保存成功')
    } else {
      ElMessage.error(res.message || '保存失败')
    }
  } catch (error) {
    console.error('保存标签失败', error)
    ElMessage.error('保存标签失败')
  }
}

onMounted(() => {
  getCurrentUser()
})
</script>

<style scoped>
/* 解决横向滚动问题 */
.content {
  margin-top: 60px;
  padding: 10px 0;
  width: 100%;
  overflow-x: hidden;
}

.el-row {
  margin-left: 0 !important;
  margin-right: 0 !important;
}

.box-card {
  min-height: 600px;
  overflow: hidden;
  margin-bottom: 20px;
}

.profile-menu {
  height: 100%;
  border-right: 1px solid #e6e6e6;
}

.content-wrapper {
  padding: 0 10px;
  overflow-x: hidden;
}

.content-panel {
  padding: 10px;
}

/* 美化用户信息区域 */
.user-header {
  display: flex;
  align-items: center;
  margin-bottom: 30px;
  padding: 20px 10px;
  background-color: #f8f9fa;
  border-radius: 8px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.05);
}

.user-avatar {
  border: 4px solid #fff;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.user-info {
  margin-left: 25px;
  flex: 1;
}

.user-info h2 {
  margin-top: 0;
  margin-bottom: 10px;
  font-size: 24px;
  font-weight: 600;
  color: #303133;
}

.user-info p {
  margin-top: 0;
  margin-bottom: 20px;
  font-size: 15px;
  color: #606266;
  max-width: 400px;
}

/* 美化信息显示区域 */
.info-section {
  padding: 10px 15px;
  margin-bottom: 20px;
}

.info-section h3 {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 15px;
  color: #303133;
}

.info-item {
  margin-bottom: 12px;
  font-size: 15px;
  line-height: 1.6;
  display: flex;
}

.label {
  color: #606266;
  margin-right: 8px;
  min-width: 80px;
  font-weight: 500;
}

/* 美化面板标题 */
.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  border-bottom: 1px solid #ebeef5;
  padding-bottom: 10px;
}

.panel-header h3 {
  font-size: 18px;
  font-weight: 600;
  margin: 0;
  color: #303133;
}

.panel-actions {
  display: flex;
  gap: 10px;
}

/* 美化标签区域 */
.tags-desc {
  margin-bottom: 20px;
  color: #606266;
  font-size: 14px;
}

.tags-container {
  margin: 20px 0;
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.tag-item {
  padding: 8px 15px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s;
}

.tag-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.tags-action {
  margin-top: 30px;
}

/* 媒体查询 - 小屏幕适配 */
@media (max-width: 768px) {
  .user-info {
    margin-left: 15px;
  }
  
  .info-item {
    flex-direction: column;
  }
  
  .label {
    margin-bottom: 4px;
  }
}

/* 修复表格内容溢出问题 */
:deep(.el-table) {
  width: 100% !important;
  max-width: 100%;
  overflow-x: hidden;
}

:deep(.el-table__body),
:deep(.el-table__header) {
  width: 100% !important;
}

/* 其他元素的样式调整 */
:deep(.el-divider) {
  margin: 20px 0;
}

:deep(.el-card__body) {
  padding: 15px !important;
  overflow: hidden;
}

:deep(.el-menu) {
  border-right: none;
}

:deep(.el-menu-item) {
  height: 50px;
  line-height: 50px;
}

:deep(.el-button) {
  font-weight: 500;
}
</style> 