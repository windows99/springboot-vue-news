<template>
  <div class="operation-log-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span class="header-title">操作记录</span>
        </div>
      </template>

      <!-- 查询条件 -->
      <el-form :model="queryRequest" class="query-form">
        <el-row :gutter="20">
          <el-col :xs="24" :sm="12" :md="6" :lg="4" :xl="4">
            <el-form-item label="操作类型">
              <el-select v-model="queryRequest.operationType" placeholder="全部类型" clearable>
                <el-option-group label="通用操作">
                  <el-option
                    v-for="item in operationTypes.filter(t => t.value.startsWith('LOGIN') || t.value.startsWith('LOGOUT') || t.value === 'REGISTER' || t.value === 'UPDATE_PROFILE')"
                    :key="item.value" :label="item.label" :value="item.value" />
                </el-option-group>
                <el-option-group label="新闻操作">
                  <el-option v-for="item in operationTypes.filter(t => t.value.startsWith('NEWS_'))" :key="item.value"
                    :label="item.label" :value="item.value" />
                </el-option-group>
                <el-option-group label="标签操作">
                  <el-option v-for="item in operationTypes.filter(t => t.value.startsWith('TAG_'))" :key="item.value"
                    :label="item.label" :value="item.value" />
                </el-option-group>
                <el-option-group label="评论操作">
                  <el-option v-for="item in operationTypes.filter(t => t.value.startsWith('COMMENT_'))"
                    :key="item.value" :label="item.label" :value="item.value" />
                </el-option-group>
                <el-option-group label="用户操作">
                  <el-option v-for="item in operationTypes.filter(t => t.value.startsWith('USER_'))" :key="item.value"
                    :label="item.label" :value="item.value" />
                </el-option-group>
                <el-option-group label="系统操作">
                  <el-option
                    v-for="item in operationTypes.filter(t => t.value.startsWith('SYSTEM_') || t.value.startsWith('DATA_'))"
                    :key="item.value" :label="item.label" :value="item.value" />
                </el-option-group>
              </el-select>
            </el-form-item>
          </el-col>

          <el-col :xs="24" :sm="12" :md="6" :lg="4" :xl="4">
            <el-form-item label="目标类型">
              <el-select v-model="queryRequest.targetType" placeholder="全部类型" clearable>
                <el-option v-for="item in targetTypes" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
          </el-col>

          <el-col :xs="24" :sm="12" :md="6" :lg="4" :xl="4">
            <el-form-item label="操作人">
              <el-input v-model="queryRequest.username" placeholder="请输入操作人" clearable />
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

      <el-table :data="tableData" v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="userId" label="操作人ID" width="180" />
        <el-table-column prop="targetId" label="新闻ID" width="180" />
        <el-table-column prop="operationType" label="操作类型" width="190">
          <template #default="{ row }">
            <el-tag :type="getOperationTypeTag(row.operationType)">
              <!-- {{ formatOperationType(row.operationType) }} -->
              {{ row.operationType }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="operationDetail" label="操作内容" />
        <el-table-column prop="operationTime" label="操作时间" width="180">
          <template #default="{ row }">
            {{ formatTime(row.operationTime) }}
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize" :total="total"
          :page-sizes="[10, 20, 50, 100]" layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange" @current-change="handleCurrentChange" />
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import dayjs from 'dayjs'
import { getOperationHistoryUsingGet } from '@/api/userOperationLogController'

// 表格数据
const tableData = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)


// 操作类型数组
const operationTypes = [
  // 通用操作
  { value: 'LOGIN', label: '登录' },
  { value: 'LOGOUT', label: '登出' },
  { value: 'REGISTER', label: '注册' },
  { value: 'UPDATE_PROFILE', label: '更新个人信息' },

  // 新闻相关操作
  { value: 'NEWS_ADD', label: '添加新闻' },
  { value: 'NEWS_UPDATE', label: '更新新闻' },
  { value: 'NEWS_DELETE', label: '删除新闻' },
  { value: 'NEWS_PUBLISH', label: '发布新闻' },
  { value: 'NEWS_SHELF', label: '下架新闻' },
  { value: 'NEWS_VIEW', label: '查看新闻' },

  // 标签相关操作
  { value: 'TAG_ADD', label: '添加标签' },
  { value: 'TAG_UPDATE', label: '更新标签' },
  { value: 'TAG_DELETE', label: '删除标签' },

  // 评论相关操作
  { value: 'COMMENT_ADD', label: '添加评论' },
  { value: 'COMMENT_UPDATE', label: '更新评论' },
  { value: 'COMMENT_DELETE', label: '删除评论' },

  // 用户管理操作
  { value: 'USER_ADD', label: '添加用户' },
  { value: 'USER_UPDATE', label: '更新用户' },
  { value: 'USER_DELETE', label: '删除用户' },
  { value: 'USER_DISABLE', label: '禁用用户' },
  { value: 'USER_ENABLE', label: '启用用户' },

  // 系统操作
  { value: 'SYSTEM_CONFIG', label: '系统配置' },
  { value: 'DATA_BACKUP', label: '数据备份' },
  { value: 'DATA_RESTORE', label: '数据恢复' }
];

// 目标类型数组
const targetTypes = [
  { value: 'USER', label: '用户' },
  { value: 'NEWS', label: '新闻' },
  { value: 'TAG', label: '标签' },
  { value: 'COMMENT', label: '评论' },
  { value: 'SYSTEM', label: '系统' },
  { value: 'NEWS_TAG', label: '新闻标签' },
  { value: 'NEWS_COMMENT', label: '新闻评论' },
  { value: 'USER_PROFILE', label: '用户资料' },
  { value: 'SYSTEM_CONFIG', label: '系统配置' }
];

// 查询条件
const queryRequest = ref({
  current: 1,
  pageSize: pageSize.value,
  operationType: undefined,
  targetType: undefined,
  username: undefined
})

// 格式化时间
const formatTime = (time: string) => {
  return dayjs(time).format('YYYY-MM-DD HH:mm:ss')
}

// 格式化操作类型
const formatOperationType = (type: string) => {
  const found = operationTypes.find(item => item.value === type)
  return found ? found.label : type
}

// 获取操作类型对应的标签类型
const getOperationTypeTag = (type: string) => {
  const tagMap = {
    // 通用操作
    'LOGIN': 'success',
    'LOGOUT': 'info',
    'REGISTER': 'success',
    'UPDATE_PROFILE': 'warning',

    // 新闻操作
    'NEWS_ADD': 'success',
    'NEWS_UPDATE': 'warning',
    'NEWS_DELETE': 'danger',
    'NEWS_PUBLISH': 'success',
    'NEWS_SHELF': 'info',
    'NEWS_VIEW': 'info',

    // 标签操作
    'TAG_ADD': 'success',
    'TAG_UPDATE': 'warning',
    'TAG_DELETE': 'danger',

    // 评论操作
    'COMMENT_ADD': 'success',
    'COMMENT_UPDATE': 'warning',
    'COMMENT_DELETE': 'danger',

    // 用户操作
    'USER_ADD': 'success',
    'USER_UPDATE': 'warning',
    'USER_DELETE': 'danger',
    'USER_DISABLE': 'warning',
    'USER_ENABLE': 'success',

    // 系统操作
    'SYSTEM_CONFIG': 'warning',
    'DATA_BACKUP': 'success',
    'DATA_RESTORE': 'warning'
  }
  return tagMap[type] || ''
}

// 获取操作记录列表
const fetchData = async () => {
  try {
    loading.value = true
    const params = {
      ...queryRequest.value,
      current: currentPage.value,
      pageSize: pageSize.value
    }
    const res = await getOperationHistoryUsingGet(params)
    if (res.code === 0) {
      tableData.value = res.data.records
      total.value = Number(res.data.total)
    } else {
      ElMessage.error(res.message)
    }
  } catch (error) {
    console.error(error)
    ElMessage.error('获取数据失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  fetchData()
}

// 重置
const handleReset = () => {
  queryRequest.value = {
    current: 1,
    pageSize: pageSize.value,
    operationType: undefined,
    targetType: undefined,
    username: undefined
  }
  currentPage.value = 1
  fetchData()
}

// 分页大小变化
const handleSizeChange = (val: number) => {
  pageSize.value = val
  fetchData()
}

// 当前页变化
const handleCurrentChange = (val: number) => {
  currentPage.value = val
  fetchData()
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.operation-log-container {
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

.query-form {
  margin-bottom: 20px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

:deep(.el-card__header) {
  padding: 15px 20px;
}

:deep(.el-form-item__label) {
  font-weight: normal;
}

:deep(.el-table) {
  margin-top: 15px;
}
</style>