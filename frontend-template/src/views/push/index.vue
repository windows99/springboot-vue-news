<template>
  <div class="news-push-container">
    <!-- 顶部操作栏 -->
    <el-card>
      <template #header>
        <div class="card-header">
          <span class="header-title">推送管理</span>
          <div class="operation-buttons">
            <el-button type="primary" @click="showSinglePushDialog">单条推送</el-button>
            <el-button type="success" @click="showBatchPushDialog">批量推送</el-button>
            <el-button type="warning" @click="showPersonalizedPushDialog">个性化推送</el-button>
          </div>
        </div>
      </template>

      <!-- 查询条件 -->
      <el-form :model="recordsSearchForm" class="query-form">
        <el-row :gutter="20">
          <el-col :xs="24" :sm="12" :md="6" :lg="4" :xl="4">
            <el-form-item label="新闻ID">
              <el-input v-model="recordsSearchForm.newsId" placeholder="请输入新闻ID" clearable />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="6" :lg="4" :xl="4">
            <el-form-item label="推送类型">
              <el-select v-model="recordsSearchForm.pushType" placeholder="选择推送类型" clearable>
                <el-option label="即时推送" :value="1" />
                <el-option label="个性化推送" :value="2" />
                <el-option label="批量推送" :value="3" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="24" :md="24" :lg="4" :xl="4">
            <div class="flex gap-2">
              <el-button type="primary" @click="handleRecordsSearch">查询</el-button>
              <el-button @click="resetRecordsSearch">重置</el-button>
            </div>
          </el-col>
        </el-row>
      </el-form>

      <!-- 推送记录表格 -->
      <el-table :data="recordsList" v-loading="recordsLoading" style="width: 100%">
        <el-table-column prop="newsPush.id" label="ID" width="80" />
        <el-table-column prop="newsId" label="新闻ID" width="180" />
        <el-table-column prop="newsTitle" label="新闻标题" />
        <el-table-column label="推送时间" width="180">
          <template #default="scope">
            {{ formatDate(scope.row.newsPush.pushTime) }}
          </template>
        </el-table-column>
        <el-table-column label="推送类型" width="120">
          <template #default="scope">
            <el-tag :type="getPushTypeTag(scope.row.newsPush.pushType)">
              {{ getPushTypeText(scope.row.newsPush.pushType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120">
          <template #default="scope">
            <el-button size="small" @click="viewRecordDetail(scope.row)" type="primary" text>
              查看
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination v-model:current-page="recordsSearchForm.current" v-model:page-size="recordsSearchForm.pageSize"
          :page-sizes="[10, 20, 50, 100]" :total="recordsTotal" layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleRecordsSizeChange" @current-change="handleRecordsPageChange" background />
      </div>
    </el-card>

    <!-- 单条推送弹窗 -->
    <el-dialog v-model="singlePushDialogVisible" title="单条推送" width="500px" destroy-on-close>
      <el-form :model="singlePushForm" label-width="80px">
        <el-form-item label="新闻ID">
          <el-input v-model="singlePushForm.newsId" placeholder="请输入要推送的新闻ID"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="singlePushDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSinglePush" :loading="singlePushLoading">
            确认推送
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 批量推送弹窗 -->
    <el-dialog v-model="batchPushDialogVisible" title="批量推送" width="80%" destroy-on-close>
      <div class="news-list-container">
        <!-- 查询表单 -->
        <el-form :model="newsSearchForm" class="query-form">
          <el-row :gutter="20">
            <el-col :xs="24" :sm="12" :md="6" :lg="4" :xl="4">
              <el-form-item label="新闻标签">
                <el-select v-model="newsSearchForm.category" placeholder="选择标签" clearable>
                  <el-option v-for="tag in newsTagList" :key="tag.id" :label="tag.tagname" :value="tag.id" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :xs="24" :sm="24" :md="24" :lg="4" :xl="4">
              <div class="flex gap-2">
                <el-button type="primary" @click="handleNewsSearch">查询</el-button>
                <el-button @click="resetNewsSearch">重置</el-button>
              </div>
            </el-col>
          </el-row>
        </el-form>

        <el-table :data="newsList" v-loading="newsLoading" @selection-change="handleNewsSelectionChange"
          @row-click="handleRowClick" highlight-current-row>
          <el-table-column type="selection" width="55" />
          <el-table-column prop="id" label="ID" width="180" />
          <el-table-column prop="title" label="标题" />
          <el-table-column prop="author" label="作者" width="120" />
          <el-table-column prop="category" label="分类" width="120" :formatter="formatCategory" />
          <el-table-column prop="createtime" label="发布日期" width="180" />
        </el-table>

        <div class="pagination-container">
          <el-pagination v-model:current-page="newsCurrentPage" v-model:page-size="newsPageSize"
            :page-sizes="[10, 20, 50, 100]" :total="newsTotal" layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleNewsSizeChange" @current-change="handleNewsPageChange" background />
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="batchPushDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleBatchPush" :loading="batchPushLoading">
            确认推送
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 个性化推送弹窗 -->
    <el-dialog v-model="personalizedPushDialogVisible" title="个性化推送" width="400px" destroy-on-close>
      <el-form :model="personalizedPushForm" label-width="80px">
        <el-form-item label="推送数量">
          <el-input-number v-model="personalizedPushForm.limit" :min="1" :max="10"></el-input-number>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="personalizedPushDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handlePersonalizedPush" :loading="personalizedPushLoading">
            确认推送
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 推送记录详情弹窗 -->
    <el-dialog v-model="detailDialogVisible" title="推送详情" width="600px" destroy-on-close>
      <div v-if="recordDetail" class="record-detail">
        <div class="detail-item">
          <span class="label">新闻ID：</span>
          <span>{{ recordDetail.newsId }}</span>
        </div>
        <div class="detail-item">
          <span class="label">新闻标题：</span>
          <span>{{ recordDetail.newsTitle }}</span>
        </div>
        <div class="detail-item">
          <span class="label">推送类型：</span>
          <el-tag :type="getPushTypeTag(recordDetail.newsPush.pushType)">
            {{ getPushTypeText(recordDetail.newsPush.pushType) }}
          </el-tag>
        </div>
        <div class="detail-item">
          <span class="label">推送时间：</span>
          <span>{{ formatDate(recordDetail.newsPush.pushTime) }}</span>
        </div>
        <div class="detail-item">
          <span class="label">创建时间：</span>
          <span>{{ formatDate(recordDetail.newsPush.createTime) }}</span>
        </div>
        <div class="detail-item" v-if="recordDetail.newsCoverImage">
          <span class="label">新闻封面：</span>
          <img :src="recordDetail.newsCoverImage" class="news-cover" />
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import {
  pushBatchUsingPost,
  pushPersonalizedUsingPost,
  listPushRecordsUsingGet,
  getUnreadPushesUsingGet
} from '@/api/newsPushController'
import { getNewsListsUsingPost } from '@/api/newsController'
import { useNewsStore } from "@/store/modules/news"

const newsStore = useNewsStore()
const newsTagList = newsStore.newsTagList

// 推送记录相关
const recordsSearchForm = reactive({
  current: 1,
  pageSize: 10,
  newsId: '',
  pushType: undefined
})
const recordsList = ref([])
const recordsTotal = ref(0)
const recordsLoading = ref(false)

// 单条推送相关
const singlePushDialogVisible = ref(false)
const singlePushForm = reactive({
  newsId: ''
})
const singlePushLoading = ref(false)

// 批量推送相关
const batchPushDialogVisible = ref(false)
const newsList = ref([])
const newsLoading = ref(false)
const newsCurrentPage = ref(1)
const newsPageSize = ref(10)
const newsTotal = ref(0)
const selectedNews = ref([])
const batchPushLoading = ref(false)

// 个性化推送相关
const personalizedPushDialogVisible = ref(false)
const personalizedPushForm = reactive({
  limit: 5
})
const personalizedPushLoading = ref(false)

// 推送记录详情相关
const detailDialogVisible = ref(false)
const recordDetail = ref(null)

// 新闻搜索表单
const newsSearchForm = reactive({
  category: undefined
})

// 显示单条推送弹窗
const showSinglePushDialog = () => {
  singlePushForm.newsId = ''
  singlePushDialogVisible.value = true
}

// 显示批量推送弹窗
const showBatchPushDialog = () => {
  batchPushDialogVisible.value = true
  fetchNewsList()
}

// 显示个性化推送弹窗
const showPersonalizedPushDialog = () => {
  personalizedPushForm.limit = 5
  personalizedPushDialogVisible.value = true
}

// 获取新闻列表
const fetchNewsList = async () => {
  try {
    newsLoading.value = true
    const res = await getNewsListsUsingPost({
      current: newsCurrentPage.value,
      pageSize: newsPageSize.value,
      category: newsSearchForm.category
    })
    if (res.code === 0) {
      newsList.value = res.data.records
      newsTotal.value = Number(res.data.total)
      console.log('新闻列表数据:', {
        current: newsCurrentPage.value,
        pageSize: newsPageSize.value,
        total: newsTotal.value,
        records: newsList.value
      })
    }
  } catch (error) {
    console.error('获取新闻列表失败', error)
    ElMessage.error('获取新闻列表失败')
  } finally {
    newsLoading.value = false
  }
}

// 处理新闻选择变化
const handleNewsSelectionChange = (selection) => {
  selectedNews.value = selection
}

// 处理新闻列表分页大小变化
const handleNewsSizeChange = (size) => {
  newsPageSize.value = size
  newsCurrentPage.value = 1
  fetchNewsList()
}

// 处理新闻列表页码变化
const handleNewsPageChange = (page) => {
  newsCurrentPage.value = page
  fetchNewsList()
}

// 单条推送
const handleSinglePush = async () => {
  if (!singlePushForm.newsId) {
    ElMessage.warning('请输入要推送的新闻ID')
    return
  }

  try {
    singlePushLoading.value = true
    const res = await pushBatchUsingPost({
      newsIds: [singlePushForm.newsId]
    })
    if (res.code === 0) {
      ElMessage.success('推送成功')
      singlePushDialogVisible.value = false
      loadPushRecords()
    }
  } catch (error) {
    console.error('推送错误', error)
    ElMessage.error('推送失败')
  } finally {
    singlePushLoading.value = false
  }
}

// 批量推送
const handleBatchPush = async () => {
  if (selectedNews.value.length === 0) {
    ElMessage.warning('请选择要推送的新闻')
    return
  }

  try {
    batchPushLoading.value = true
    const newsIds = selectedNews.value.map(news => news.id)
    const res = await pushBatchUsingPost({
      newsIds: newsIds
    })
    if (res.code === 0) {
      ElMessage.success(`成功推送${res.data}条新闻`)
      batchPushDialogVisible.value = false
      loadPushRecords()
    }
  } catch (error) {
    console.error('批量推送错误', error)
    ElMessage.error('批量推送失败')
  } finally {
    batchPushLoading.value = false
  }
}

// 个性化推送
const handlePersonalizedPush = async () => {
  try {
    personalizedPushLoading.value = true
    const res = await pushPersonalizedUsingPost({
      limit: personalizedPushForm.limit
    })
    if (res.code === 0) {
      if (res.data > 0) {
        ElMessage.success(`成功推送${res.data}条个性化新闻`)
      } else {
        ElMessage.info('没有找到符合您兴趣的新闻')
      }
      personalizedPushDialogVisible.value = false
      loadPushRecords()
    }
  } catch (error) {
    console.error('个性化推送错误', error)
    ElMessage.error('个性化推送失败')
  } finally {
    personalizedPushLoading.value = false
  }
}

// 查询推送记录
const loadPushRecords = async () => {
  try {
    recordsLoading.value = true
    const res = await listPushRecordsUsingGet({
      params: {
        current: recordsSearchForm.current,
        pageSize: recordsSearchForm.pageSize,
        newsId: recordsSearchForm.newsId,
        pushType: recordsSearchForm.pushType
      }
    })
    if (res.code === 0) {
      recordsList.value = res.data.records
      recordsTotal.value = Number(res.data.total)
      console.log('推送记录数据:', {
        current: recordsSearchForm.current,
        pageSize: recordsSearchForm.pageSize,
        total: recordsTotal.value,
        records: recordsList.value
      })
    }
  } catch (error) {
    console.error('加载推送记录错误', error)
    ElMessage.error('获取推送记录失败')
  } finally {
    recordsLoading.value = false
  }
}

// 处理推送记录查询
const handleRecordsSearch = () => {
  recordsSearchForm.current = 1
  loadPushRecords()
}

// 重置推送记录查询
const resetRecordsSearch = () => {
  recordsSearchForm.current = 1
  recordsSearchForm.newsId = ''
  recordsSearchForm.pushType = undefined
  loadPushRecords()
}

// 处理推送记录分页大小变化
const handleRecordsSizeChange = (size) => {
  recordsSearchForm.pageSize = size
  recordsSearchForm.current = 1
  loadPushRecords()
}

// 处理推送记录页码变化
const handleRecordsPageChange = (page) => {
  recordsSearchForm.current = page
  loadPushRecords()
}

// 查看推送记录详情
const viewRecordDetail = (record) => {
  recordDetail.value = record
  detailDialogVisible.value = true
}

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 获取推送类型文本
const getPushTypeText = (type) => {
  const types = {
    1: '即时推送',
    2: '个性化推送',
    3: '批量推送'
  }
  return types[type] || '未知类型'
}

// 获取推送类型对应的标签类型
const getPushTypeTag = (type) => {
  const tags = {
    1: 'primary',
    2: 'success',
    3: 'warning'
  }
  return tags[type] || 'info'
}

// 格式化分类
const formatCategory = (row) => {
  const tag = newsTagList.find(item => item.id === row.category)
  return tag ? tag.tagname : '-'
}

// 处理新闻搜索
const handleNewsSearch = () => {
  newsCurrentPage.value = 1
  fetchNewsList()
}

// 重置新闻搜索
const resetNewsSearch = () => {
  newsSearchForm.category = undefined
  newsCurrentPage.value = 1
  fetchNewsList()
}

// 修改行点击处理方法
const handleRowClick = (row) => {
  const index = selectedNews.value.findIndex(item => item.id === row.id)
  if (index === -1) {
    selectedNews.value.push(row)
  } else {
    selectedNews.value.splice(index, 1)
  }
  // 更新表格选中状态
  const tableRef = document.querySelector('.news-list-container .el-table')
  if (tableRef) {
    const checkbox = tableRef.querySelector(`tr[data-row-key="${row.id}"] .el-checkbox__input`)
    if (checkbox) {
      checkbox.click()
    }
  }
}

onMounted(() => {
  loadPushRecords()
})
</script>

<style scoped>
.news-push-container {
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

.operation-buttons {
  display: flex;
  gap: 10px;
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

.news-list-container {
  padding: 0;
}

.record-detail {
  padding: 20px;
}

.detail-item {
  margin-bottom: 15px;
  display: flex;
  align-items: flex-start;
}

.detail-item .label {
  font-weight: bold;
  width: 100px;
  flex-shrink: 0;
}

.news-cover {
  max-width: 200px;
  max-height: 150px;
  border-radius: 4px;
  margin-top: 10px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
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
</style>