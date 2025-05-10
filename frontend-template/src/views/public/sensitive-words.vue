<template>
  <div class="sensitive-words-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span class="header-title">敏感词维护</span>
          <el-button type="primary" @click="handleAdd">添加敏感词</el-button>
        </div>
      </template>

      <!-- 查询条件 -->
      <el-form :model="queryRequest" class="query-form">
        <el-row :gutter="20">
          <el-col :xs="24" :sm="12" :md="6" :lg="4" :xl="4">
            <el-form-item label="敏感词">
              <el-input v-model="queryRequest.word" placeholder="请输入敏感词" clearable />
            </el-form-item>
          </el-col>

          <el-col :xs="24" :sm="12" :md="6" :lg="4" :xl="4">
            <el-form-item label="状态">
              <el-select v-model="queryRequest.status" placeholder="全部状态" clearable>
                <el-option label="启用" value="1" />
                <el-option label="禁用" value="0" />
              </el-select>
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

      <el-table :data="tableData" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="180" />
        <el-table-column prop="word" label="敏感词" />
        <el-table-column prop="createtime" label="创建时间" width="180">
          <template #default="{ row }">
            {{ formatTime(row.createtime) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-switch v-model="row.status" active-value="1" inactive-value="0" @change="handleStatusChange(row)" />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="190">
          <template #default="{ row }">
            <el-button-group>
              <el-button size="small" @click="handleEdit(row)">编辑</el-button>
              <el-button size="small" type="danger" @click="handleDelete(row)">
                删除
              </el-button>
            </el-button-group>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize" :total="total"
          :page-sizes="[10, 20, 50, 100]" layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange" @current-change="handleCurrentChange" />
      </div>
    </el-card>

    <!-- 添加/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px" destroy-on-close>
      <el-form :model="form" label-width="80px">
        <el-form-item label="敏感词" required>
          <el-input v-model="form.word" placeholder="请输入敏感词" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script lang="ts" setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import dayjs from 'dayjs'
import { addSensitiveWordUsingPost, updateSensitiveWordUsingPut, getSensitiveWordUsingGet, deleteSensitiveWordUsingDelete } from '@/api/sensitiveWordController'

interface SensitiveWord {
  id: number
  word: string
  createtime: string
  status: string
}

// 表格数据
const tableData = ref<SensitiveWord[]>([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 查询条件
const queryRequest = ref({
  current: 1,
  pageSize: pageSize.value,
  word: undefined,
  status: undefined
})

// 对话框相关
const dialogVisible = ref(false)
const dialogTitle = ref('')
const form = reactive({
  id: 0,
  word: '',
  status: '1'
})

// 格式化时间
const formatTime = (time: string) => {
  return dayjs(time).format('YYYY-MM-DD HH:mm:ss')
}

// 获取敏感词列表
const fetchData = async () => {
  try {
    loading.value = true
    const res = await getSensitiveWordUsingGet()
    if (res.code === 0) {
      // 确保状态值为字符串类型
      tableData.value = res.data.map(item => ({
        ...item,
        status: String(item.status)
      }))
      total.value = res.data.length
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
    word: undefined,
    status: undefined
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

// 添加
const handleAdd = () => {
  dialogTitle.value = '添加敏感词'
  form.id = 0
  form.word = ''
  form.status = '1'
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row: SensitiveWord) => {
  dialogTitle.value = '编辑敏感词'
  form.id = row.id
  form.word = row.word
  form.status = String(row.status)
  dialogVisible.value = true
}

// 删除
const handleDelete = (row: SensitiveWord) => {
  ElMessageBox.confirm('确定删除该敏感词吗?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
    .then(async () => {
      await deleteSensitiveWordUsingDelete({ id: row.id })
      ElMessage.success('删除成功')
      fetchData()
    })
    .catch(() => { })
}

// 状态变更
const handleStatusChange = async (row: SensitiveWord) => {
  try {
    const newStatus = row.status === '1' ? '0' : '1'
    await updateSensitiveWordUsingPut({ id: row.id }, { status: Number(newStatus) })
    row.status = newStatus
    ElMessage.success('状态更新成功')
  } catch (error) {
    // 回滚状态
    row.status = row.status === '1' ? '0' : '1'
    ElMessage.error('状态更新失败')
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!form.word.trim()) {
    ElMessage.warning('请输入敏感词')
    return
  }

  try {
    const submitData = {
      ...form,
      status: Number(form.status)
    }
    
    if (form.id) {
      await updateSensitiveWordUsingPut({ id: form.id }, submitData)
      ElMessage.success('更新成功')
    } else {
      await addSensitiveWordUsingPost(submitData)
      ElMessage.success('添加成功')
    }
    dialogVisible.value = false
    fetchData()
  } catch (error) {
    console.error(error)
    ElMessage.error('操作失败')
  }
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.sensitive-words-container {
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

:deep(.el-button-group) {
  .el-button {
    margin-right: 8px;
  }
}
</style>
