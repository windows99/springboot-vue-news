<template>
  <div class="comment-manage-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span class="header-title">评论管理</span>
        </div>
      </template>

      <!-- 查询条件 -->
      <el-form :model="queryParams" class="query-form">
        <el-row :gutter="20">
          <el-col :xs="24" :sm="12" :md="6" :lg="8" :xl="8">
            <el-form-item label="新闻标题">
              <el-input v-model="queryParams.newsTitle" placeholder="输入新闻标题" clearable />
            </el-form-item>
          </el-col>

          <el-col :xs="24" :sm="12" :md="6" :lg="8" :xl="8">
            <el-form-item label="评论内容">
              <el-input v-model="queryParams.content" placeholder="输入评论内容关键词" clearable />
            </el-form-item>
          </el-col>

          <el-col :xs="24" :sm="24" :md="12" :lg="8" :xl="8">
            <div class="flex gap-2">
              <el-button type="primary" @click="handleSearch">查询</el-button>
              <el-button @click="handleReset">重置</el-button>
            </div>
          </el-col>
        </el-row>
      </el-form>

      <!-- 评论列表 -->
      <el-table :data="commentList" v-loading="loading" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="newsTitle" label="新闻标题" width="200" />
        <el-table-column prop="content" label="评论内容" min-width="250" show-overflow-tooltip />
        <el-table-column prop="username" label="评论用户" width="120" />
        <el-table-column prop="createTime" label="评论时间" width="160" />
        <el-table-column prop="likeNum" label="点赞数" width="80" />
        <el-table-column label="操作" width="180">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleView(row)">查看</el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @current-change="handlePageChange"
          @size-change="handleSizeChange"
        />
      </div>
    </el-card>

    <!-- 评论详情弹窗 -->
    <el-dialog v-model="dialogVisible" title="评论详情" width="50%">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="ID">{{ currentComment.id }}</el-descriptions-item>
        <el-descriptions-item label="新闻标题">{{ currentComment.newsTitle }}</el-descriptions-item>
        <el-descriptions-item label="评论用户">{{ currentComment.username }}</el-descriptions-item>
        <el-descriptions-item label="用户头像">
          <el-avatar :src="currentComment.userAvatar" :size="50"></el-avatar>
        </el-descriptions-item>
        <el-descriptions-item label="评论时间">{{ currentComment.createTime }}</el-descriptions-item>
        <el-descriptions-item label="点赞数">{{ currentComment.likeNum }}</el-descriptions-item>
        <el-descriptions-item label="评论内容">{{ currentComment.content }}</el-descriptions-item>
        <el-descriptions-item v-if="currentComment.parentId" label="父评论ID">{{ currentComment.parentId }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  getCommentListUsingGet, 
  deleteCommentByIdUsingDelete,
  getCommentByIdUsingGet
} from '@/api/commentController'

// 分页相关
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const loading = ref(false)

// 查询参数
const queryParams = reactive({
  newsTitle: '',
  content: '',
  page: currentPage.value,
  pageSize: pageSize.value
})

// 评论列表
const commentList = ref<any[]>([])

// 当前查看的评论
const currentComment = ref<any>({})
const dialogVisible = ref(false)

// 获取评论列表
const fetchCommentList = async () => {
  loading.value = true
  try {
    const params = {
      ...queryParams,
      page: currentPage.value,
      pageSize: pageSize.value
    }

    const res = await getCommentListUsingGet(params)
    if (res && res.code === 0 && res.data) {
      // 由于接口可能直接返回数组，这里做一下兼容处理
      if (Array.isArray(res.data)) {
        commentList.value = res.data
        total.value = res.data.length
      } else {
        // 如果后端返回分页对象，使用类型断言确保TypeScript不会报错
        const data = res.data as any
        if (data.total !== undefined) {
          total.value = data.total
          commentList.value = data.records || data
        } else {
          commentList.value = res.data as any[]
          total.value = (res.data as any[]).length
        }
      }
    } else {
      commentList.value = []
      total.value = 0
      ElMessage.error(res.message || '获取评论列表失败')
    }
  } catch (error) {
    console.error('获取评论列表失败', error)
    ElMessage.error('获取评论列表失败')
  } finally {
    loading.value = false
  }
}

// 查询
const handleSearch = () => {
  currentPage.value = 1
  fetchCommentList()
}

// 重置
const handleReset = () => {
  // 重置查询表单
  queryParams.newsTitle = ''
  queryParams.content = ''
  currentPage.value = 1
  fetchCommentList()
}

// 查看评论详情
const handleView = async (row: any) => {
  try {
    loading.value = true
    const res = await getCommentByIdUsingGet({ id: row.id })
    if (res && res.code === 0 && res.data) {
      currentComment.value = res.data
      dialogVisible.value = true
    } else {
      ElMessage.error(res.message || '获取评论详情失败')
    }
  } catch (error) {
    console.error('获取评论详情失败', error)
    ElMessage.error('获取评论详情失败')
  } finally {
    loading.value = false
  }
}

// 删除评论
const handleDelete = async (row: any) => {
  try {
    await ElMessageBox.confirm(
      '确定要删除该评论吗？删除后无法恢复。',
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const res = await deleteCommentByIdUsingDelete({ id: row.id })
    if (res && res.code === 0) {
      ElMessage.success('删除成功')
      fetchCommentList()
    } else {
      ElMessage.error(res.message || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除评论失败', error)
      ElMessage.error('删除评论失败')
    }
  }
}

// 分页变化
const handlePageChange = (page: number) => {
  currentPage.value = page
  fetchCommentList()
}

// 每页条数变化
const handleSizeChange = (size: number) => {
  pageSize.value = size
  currentPage.value = 1
  fetchCommentList()
}

onMounted(() => {
  fetchCommentList()
})
</script>

<style scoped>
.comment-manage-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-title {
  font-size: 18px;
  font-weight: bold;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.query-form {
  margin-bottom: 20px;
}
</style> 