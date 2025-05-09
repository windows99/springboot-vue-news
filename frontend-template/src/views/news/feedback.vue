<template>
  <div class="news-feedback-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span class="header-title">反馈新闻管理</span>
        </div>
      </template>

      <!-- 查询条件 -->
      <el-form :model="queryRequest" class="query-form">
        <el-row :gutter="20">
          <el-col :xs="24" :sm="12" :md="6" :lg="4" :xl="4">
            <el-form-item label="新闻ID">
              <el-input v-model="queryRequest.newsId" placeholder="请输入新闻ID" clearable />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="24" :md="24" :lg="4" :xl="4">
            <div class="flex gap-2">
              <el-button type="primary" @click="handleSearch">查询</el-button>
              <el-button @click="handleReset">重置</el-button>
            </div>
          </el-col>
        </el-row>
      </el-form>

      <!-- 反馈新闻列表 -->
      <el-table :data="tableData" v-loading="loading" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="newsId" label="新闻ID" width="180" />
        <el-table-column prop="newsTitle" label="新闻标题" />
        <el-table-column prop="username" label="反馈人" />
        <el-table-column prop="content" label="反馈内容" show-overflow-tooltip />
        <el-table-column prop="createTime" label="反馈时间" width="180" :formatter="formatDate" />
        <el-table-column label="操作" width="300">
          <template #default="{ row }">
            <el-button-group>
              <el-button type="primary" size="small" @click="handleEdit(row)">重新编辑</el-button>
              <el-button type="success" size="small" @click="handleRepublish(row)">重新发布</el-button>
              <el-button type="danger" size="small" @click="handleDelete(row)">删除新闻</el-button>
            </el-button-group>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize" :page-sizes="[10, 20, 50, 100]"
          :total="total" layout="total, sizes, prev, pager, next, jumper" @size-change="handleSizeChange"
          @current-change="handleCurrentChange" background />
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getPendingFeedbackListUsingGet,
  deleteFeedbackUsingPost,
  reviewFeedbackUsingPost
} from '@/api/newsFeedbackController'
import { deleteNewsUsingDelete, setStatusNewsUsingPut } from '@/api/newsController'

const router = useRouter()

// 表格数据
const tableData = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 查询条件
const queryRequest = reactive({
  newsId: '',
  current: 1,
  pageSize: 10
})

// 获取反馈新闻列表
const fetchData = async () => {
  try {
    loading.value = true
    const res = await getPendingFeedbackListUsingGet({
      current: currentPage.value,
      pageSize: pageSize.value,
      newsId: queryRequest.newsId
    })
    if (res.code === 0) {
      tableData.value = res.data.records
      total.value = res.data.total
    } else {
      ElMessage.error(res.message || '获取数据失败')
    }
  } catch (error) {
    console.error('获取反馈新闻列表失败', error)
    ElMessage.error('获取数据失败')
  } finally {
    loading.value = false
  }
}

// 处理查询
const handleSearch = () => {
  currentPage.value = 1
  fetchData()
}

// 处理重置
const handleReset = () => {
  queryRequest.newsId = ''
  currentPage.value = 1
  fetchData()
}

// 处理分页大小变化
const handleSizeChange = (size: number) => {
  pageSize.value = size
  currentPage.value = 1
  fetchData()
}

// 处理页码变化
const handleCurrentChange = (page: number) => {
  currentPage.value = page
  fetchData()
}

// 格式化日期
const formatDate = (row: any) => {
  if (!row.createTime) return '-'
  const date = new Date(row.createTime)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 重新编辑
const handleEdit = async (row: any) => {
  try {
    const res = await setStatusNewsUsingPut({ id: row.newsId, statusInt: 0 })
    if (res.code === 0) {
      // 删除反馈记录
      const deleteFeedbackRes = await deleteFeedbackUsingPost({ id: row.id })
      if (deleteFeedbackRes.code === 0) {
        ElMessage.success('已设置为草稿状态')
        fetchData()
      } else {
        ElMessage.error(deleteFeedbackRes.message || '删除反馈记录失败')
      }
    } else {
      ElMessage.error(res.message || '设置状态失败')
    }
  } catch (error) {
    console.error('设置状态失败', error)
    ElMessage.error('设置状态失败')
  }
}

// 重新发布
const handleRepublish = async (row: any) => {
  try {
    const res = await setStatusNewsUsingPut({ id: row.newsId, statusInt: 3 })
    if (res.code === 0) {
      // 删除反馈记录
      const deleteFeedbackRes = await deleteFeedbackUsingPost({ id: row.id })
      if (deleteFeedbackRes.code === 0) {
        ElMessage.success('已重新发布')
        fetchData()
      } else {
        ElMessage.error(deleteFeedbackRes.message || '删除反馈记录失败')
      }
    } else {
      ElMessage.error(res.message || '发布失败')
    }
  } catch (error) {
    console.error('发布失败', error)
    ElMessage.error('发布失败')
  }
}

// 删除
const handleDelete = (row: any) => {
  ElMessageBox.confirm('确认删除该新闻及其反馈记录？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      // 先删除新闻
      const deleteNewsRes = await deleteNewsUsingDelete({ id: row.newsId })
      if (deleteNewsRes.status === 200) {
        // 再删除反馈记录
        const deleteFeedbackRes = await deleteFeedbackUsingPost({ id: row.id })
        if (deleteFeedbackRes.code === 0) {
          ElMessage.success('删除成功')
          fetchData()
        } else {
          ElMessage.error(deleteFeedbackRes.message || '删除反馈记录失败')
        }
      } else {
        ElMessage.error(deleteNewsRes.message || '删除新闻失败')
      }
    } catch (error) {
      console.error('删除失败', error)
      ElMessage.error('删除失败')
    }
  })
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.news-feedback-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-title {
  font-size: 16px;
  font-weight: bold;
}

.query-form {
  margin-bottom: 20px;
}

.flex {
  display: flex;
}

.gap-2 {
  gap: 8px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
  padding: 10px 0;
}

:deep(.el-dialog__body) {
  padding: 20px;
}

:deep(.el-form-item__label) {
  font-weight: normal;
}

:deep(.el-table) {
  margin-top: 10px;
}

:deep(.el-card__header) {
  padding: 15px 20px;
  border-bottom: 1px solid #ebeef5;
}

:deep(.el-card__body) {
  padding: 20px;
}

:deep(.el-button-group) {
  display: flex;
  gap: 8px;
}
</style>