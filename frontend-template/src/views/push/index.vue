<template>
  <div class="news-push-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span class="header-title">新闻推送管理</span>
          <div class="button-group">
            <el-button type="primary" @click="createTimedPush">创建定时推送</el-button>
            <el-button type="success" @click="createInstantPush">一键推送</el-button>
            <el-button type="warning" @click="createTargetedPush">定向推送</el-button>
          </div>
        </div>
      </template>

      <!-- 推送任务列表 -->
      <el-tabs v-model="activeTab" @tab-click="handleTabClick">
        <el-tab-pane label="所有任务" name="all"></el-tab-pane>
        <el-tab-pane label="待执行" name="pending"></el-tab-pane>
        <el-tab-pane label="已完成" name="completed"></el-tab-pane>
        <el-tab-pane label="失败任务" name="failed"></el-tab-pane>
      </el-tabs>

      <el-table :data="taskList" v-loading="loading" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="推送标题" width="180" />
        <el-table-column prop="pushType" label="推送类型" width="120">
          <template #default="{ row }">
            <el-tag :type="getPushTypeTag(row.pushType) as '' | 'success' | 'warning' | 'info' | 'danger'">{{ getPushTypeLabel(row.pushType) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="getStatusTag(row.status) as 'success' | 'warning' | 'info' | 'danger'">{{ getStatusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="scheduledTime" label="计划时间" width="180" />
        <el-table-column prop="createdTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button 
              v-if="row.status === 'pending'" 
              type="success" 
              size="small" 
              @click="executePush(row)"
            >
              执行
            </el-button>
            <el-button 
              v-if="row.status === 'pending'" 
              type="danger" 
              size="small" 
              @click="cancelPush(row)"
            >
              取消
            </el-button>
            <el-button 
              v-if="row.status === 'completed'" 
              type="primary" 
              size="small" 
              @click="viewStatistics(row)"
            >
              统计
            </el-button>
            <el-button 
              type="info" 
              size="small" 
              @click="viewDetail(row)"
            >
              详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>

    <!-- 定时推送对话框 -->
    <el-dialog v-model="timedPushDialogVisible" title="创建定时推送" width="60%">
      <el-form :model="timedPushForm" label-width="100px">
        <el-form-item label="推送标题" required>
          <el-input v-model="timedPushForm.title" placeholder="请输入推送标题" />
        </el-form-item>
        <el-form-item label="选择新闻" required>
          <el-select
            v-model="timedPushForm.newsIds"
            multiple
            filterable
            placeholder="请选择要推送的新闻"
            style="width: 100%"
          >
            <el-option
              v-for="item in newsList"
              :key="item.id"
              :label="item.title"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="推送时间" required>
          <el-date-picker
            v-model="timedPushForm.scheduledTime"
            type="datetime"
            placeholder="选择推送时间"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="推送内容">
          <el-input
            v-model="timedPushForm.content"
            type="textarea"
            rows="4"
            placeholder="请输入推送内容描述"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="timedPushDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleTimedPushSubmit">确认</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 一键推送对话框 -->
    <el-dialog v-model="instantPushDialogVisible" title="一键推送" width="60%">
      <el-form :model="instantPushForm" label-width="100px">
        <el-form-item label="推送标题" required>
          <el-input v-model="instantPushForm.title" placeholder="请输入推送标题" />
        </el-form-item>
        <el-form-item label="选择新闻" required>
          <el-select
            v-model="instantPushForm.newsIds"
            multiple
            filterable
            placeholder="请选择要推送的新闻"
            style="width: 100%"
          >
            <el-option
              v-for="item in newsList"
              :key="item.id"
              :label="item.title"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="推送内容">
          <el-input
            v-model="instantPushForm.content"
            type="textarea"
            rows="4"
            placeholder="请输入推送内容描述"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="instantPushDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleInstantPushSubmit">立即推送</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 定向推送对话框 -->
    <el-dialog v-model="targetedPushDialogVisible" title="定向推送" width="60%">
      <el-form :model="targetedPushForm" label-width="100px">
        <el-form-item label="推送标题" required>
          <el-input v-model="targetedPushForm.title" placeholder="请输入推送标题" />
        </el-form-item>
        <el-form-item label="选择新闻" required>
          <el-select
            v-model="targetedPushForm.newsIds"
            multiple
            filterable
            placeholder="请选择要推送的新闻"
            style="width: 100%"
          >
            <el-option
              v-for="item in newsList"
              :key="item.id"
              :label="item.title"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="用户角色">
          <el-select
            v-model="targetedPushForm.targetUsers.roles"
            multiple
            placeholder="按用户角色筛选"
            style="width: 100%"
          >
            <el-option label="管理员" value="admin" />
            <el-option label="编辑" value="editor" />
            <el-option label="普通用户" value="user" />
          </el-select>
        </el-form-item>
        <el-form-item label="用户标签">
          <el-select
            v-model="targetedPushForm.targetUsers.tags"
            multiple
            filterable
            placeholder="按用户标签筛选"
            style="width: 100%"
          >
            <el-option
              v-for="tag in userTagList"
              :key="tag.id"
              :label="tag.name"
              :value="tag.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="推送时间" required>
          <el-date-picker
            v-model="targetedPushForm.scheduledTime"
            type="datetime"
            placeholder="选择推送时间"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="推送内容">
          <el-input
            v-model="targetedPushForm.content"
            type="textarea"
            rows="4"
            placeholder="请输入推送内容描述"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="targetedPushDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleTargetedPushSubmit">确认</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 统计详情对话框 -->
    <el-dialog v-model="statisticsDialogVisible" title="推送统计" width="50%">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="推送标题">{{ currentTask?.title }}</el-descriptions-item>
        <el-descriptions-item label="推送类型">{{ getPushTypeLabel(currentTask?.pushType) }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ currentTask?.createdTime }}</el-descriptions-item>
        <el-descriptions-item label="执行时间">{{ currentTask?.executedTime }}</el-descriptions-item>
        <el-descriptions-item label="总推送数">{{ currentTask?.statistics?.total || 0 }}</el-descriptions-item>
        <el-descriptions-item label="送达数">{{ currentTask?.statistics?.delivered || 0 }}</el-descriptions-item>
        <el-descriptions-item label="已读数">{{ currentTask?.statistics?.read || 0 }}</el-descriptions-item>
        <el-descriptions-item label="反馈数">{{ currentTask?.statistics?.feedback || 0 }}</el-descriptions-item>
      </el-descriptions>
      <div class="statistics-chart" style="height: 300px; margin-top: 20px;">
        <!-- 这里可以添加图表组件，如ECharts -->
        <div>图表区域（后续可集成ECharts）</div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, reactive, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getNewsListsUsingPost } from '@/api/newsController'

// 模拟推送类型API
const getPushTypeTag = (type: string) => {
  const map: Record<string, '' | 'success' | 'warning' | 'info' | 'danger'> = {
    'instant': 'success',
    'timed': '',  // 使用默认样式替代primary
    'targeted': 'warning'
  }
  return map[type] || 'info'
}

const getPushTypeLabel = (type: string) => {
  const map: Record<string, string> = {
    'instant': '一键推送',
    'timed': '定时推送',
    'targeted': '定向推送'
  }
  return map[type] || '未知类型'
}

const getStatusTag = (status: string) => {
  const map: Record<string, 'success' | 'warning' | 'info' | 'danger'> = {
    'pending': 'warning',
    'processing': 'info',
    'completed': 'success',
    'failed': 'danger'
  }
  return map[status] || 'info'
}

const getStatusLabel = (status: string) => {
  const map: Record<string, string> = {
    'pending': '待执行',
    'processing': '执行中',
    'completed': '已完成',
    'failed': '失败'
  }
  return map[status] || '未知状态'
}

// 分页相关
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const loading = ref(false)
const activeTab = ref('all')

// 推送任务列表
const taskList = ref([])

// 新闻列表（用于选择）
const newsList = ref([])

// 用户标签列表（模拟数据）
const userTagList = ref([
  { id: 1, name: '科技爱好者' },
  { id: 2, name: '财经关注' },
  { id: 3, name: '体育迷' },
  { id: 4, name: '娱乐粉丝' },
  { id: 5, name: '教育工作者' }
])

// 定时推送表单
const timedPushDialogVisible = ref(false)
const timedPushForm = reactive({
  title: '',
  content: '',
  newsIds: [] as number[],
  scheduledTime: ''
})

// 一键推送表单
const instantPushDialogVisible = ref(false)
const instantPushForm = reactive({
  title: '',
  content: '',
  newsIds: [] as number[]
})

// 定向推送表单
const targetedPushDialogVisible = ref(false)
const targetedPushForm = reactive({
  title: '',
  content: '',
  newsIds: [] as number[],
  scheduledTime: '',
  targetUsers: {
    roles: [] as string[],
    tags: [] as number[]
  }
})

// 统计对话框
const statisticsDialogVisible = ref(false)
const currentTask = ref(null)

// 获取新闻列表
const fetchNewsList = async () => {
  try {
    const res = await getNewsListsUsingPost({
      current: 1,
      pageSize: 100,
      status: 3 // 已发布状态
    })
    if (res.code === 0 && res.data && res.data.records) {
      newsList.value = res.data.records
    }
  } catch (error) {
    console.error('获取新闻列表失败', error)
    ElMessage.error('获取新闻列表失败')
  }
}

// 获取推送任务列表（模拟数据）
const fetchTaskList = async () => {
  loading.value = true
  try {
    // 这里应该调用后端API
    // 模拟数据
    const mockData = [
      {
        id: 1,
        title: '周末活动通知',
        pushType: 'instant',
        status: 'completed',
        scheduledTime: null,
        createdTime: '2023-05-20 10:00:00',
        executedTime: '2023-05-20 10:00:05',
        statistics: {
          total: 100,
          delivered: 95,
          read: 80,
          feedback: 20
        }
      },
      {
        id: 2,
        title: '每周热点推送',
        pushType: 'timed',
        status: 'pending',
        scheduledTime: '2023-05-25 08:00:00',
        createdTime: '2023-05-20 14:30:00',
        executedTime: null
      },
      {
        id: 3,
        title: 'VIP用户专享资讯',
        pushType: 'targeted',
        status: 'failed',
        scheduledTime: '2023-05-21 09:00:00',
        createdTime: '2023-05-20 16:45:00',
        executedTime: '2023-05-21 09:00:03',
        statistics: {
          total: 50,
          delivered: 30,
          read: 0,
          feedback: 0
        }
      }
    ]
    
    // 根据当前标签筛选数据
    if (activeTab.value === 'all') {
      taskList.value = mockData
    } else {
      taskList.value = mockData.filter(task => task.status === activeTab.value)
    }
    
    total.value = taskList.value.length
  } catch (error) {
    console.error('获取推送任务列表失败', error)
    ElMessage.error('获取推送任务列表失败')
  } finally {
    loading.value = false
  }
}

// 创建定时推送
const createTimedPush = () => {
  timedPushForm.title = ''
  timedPushForm.content = ''
  timedPushForm.newsIds = []
  timedPushForm.scheduledTime = ''
  timedPushDialogVisible.value = true
}

// 创建一键推送
const createInstantPush = () => {
  instantPushForm.title = ''
  instantPushForm.content = ''
  instantPushForm.newsIds = []
  instantPushDialogVisible.value = true
}

// 创建定向推送
const createTargetedPush = () => {
  targetedPushForm.title = ''
  targetedPushForm.content = ''
  targetedPushForm.newsIds = []
  targetedPushForm.scheduledTime = ''
  targetedPushForm.targetUsers.roles = []
  targetedPushForm.targetUsers.tags = []
  targetedPushDialogVisible.value = true
}

// 提交定时推送
const handleTimedPushSubmit = async () => {
  // 表单验证
  if (!timedPushForm.title || timedPushForm.newsIds.length === 0 || !timedPushForm.scheduledTime) {
    ElMessage.warning('请填写必填项')
    return
  }
  
  try {
    // 这里应该调用后端API
    console.log('提交定时推送', timedPushForm)
    ElMessage.success('创建定时推送成功')
    timedPushDialogVisible.value = false
    fetchTaskList()
  } catch (error) {
    console.error('创建定时推送失败', error)
    ElMessage.error('创建定时推送失败')
  }
}

// 提交一键推送
const handleInstantPushSubmit = async () => {
  // 表单验证
  if (!instantPushForm.title || instantPushForm.newsIds.length === 0) {
    ElMessage.warning('请填写必填项')
    return
  }
  
  try {
    // 这里应该调用后端API
    console.log('提交一键推送', instantPushForm)
    ElMessage.success('一键推送成功')
    instantPushDialogVisible.value = false
    fetchTaskList()
  } catch (error) {
    console.error('一键推送失败', error)
    ElMessage.error('一键推送失败')
  }
}

// 提交定向推送
const handleTargetedPushSubmit = async () => {
  // 表单验证
  if (!targetedPushForm.title || targetedPushForm.newsIds.length === 0 || !targetedPushForm.scheduledTime) {
    ElMessage.warning('请填写必填项')
    return
  }
  
  try {
    // 这里应该调用后端API
    console.log('提交定向推送', targetedPushForm)
    ElMessage.success('创建定向推送成功')
    targetedPushDialogVisible.value = false
    fetchTaskList()
  } catch (error) {
    console.error('创建定向推送失败', error)
    ElMessage.error('创建定向推送失败')
  }
}

// 执行推送
const executePush = async (row: any) => {
  try {
    await ElMessageBox.confirm('确定要立即执行此推送任务吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    // 这里应该调用后端API
    console.log('执行推送', row)
    ElMessage.success('推送任务已执行')
    fetchTaskList()
  } catch (error) {
    console.log('取消执行')
  }
}

// 取消推送
const cancelPush = async (row: any) => {
  try {
    await ElMessageBox.confirm('确定要取消此推送任务吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    // 这里应该调用后端API
    console.log('取消推送', row)
    ElMessage.success('推送任务已取消')
    fetchTaskList()
  } catch (error) {
    console.log('取消操作')
  }
}

// 查看统计
const viewStatistics = (row: any) => {
  currentTask.value = row
  statisticsDialogVisible.value = true
}

// 查看详情
const viewDetail = (row: any) => {
  currentTask.value = row
  // 这里可以跳转到详情页面或显示详情对话框
  console.log('查看详情', row)
}

// 标签页切换
const handleTabClick = () => {
  fetchTaskList()
}

// 分页切换
const handlePageChange = () => {
  fetchTaskList()
}

onMounted(() => {
  fetchNewsList()
  fetchTaskList()
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
  font-size: 18px;
  font-weight: bold;
}

.button-group {
  display: flex;
  gap: 10px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style> 