<template>
  <div class="sensitive-words">
    <h1>敏感词维护</h1>

    <el-card>
      <div class="mb-4">
        <el-button type="primary" @click="handleAdd">添加敏感词</el-button>
        <el-input v-model="searchQuery" placeholder="搜索敏感词" style="width: 200px; margin-left: 10px" clearable
          @clear="handleSearch" @keyup.enter="handleSearch">
          <template #append>
            <el-button :icon="Search" @click="handleSearch" />
          </template>
        </el-input>
      </div>

      <el-table :data="tableData" style="width: 100%">
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
          <template #default="{ row, $index }">
            <el-button-group>
              <el-button size="small" @click="handleEdit(row)">编辑</el-button>
              <el-button size="small" type="danger" @click="handleDelete($index, row)">
                删除
              </el-button>
            </el-button-group>
          </template>
        </el-table-column>
      </el-table>

      <div class="mt-4 flex justify-end">
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
          <el-button type="primary" @click="handleSubmit"> 确定 </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script lang="ts" setup>
import { ref, reactive, onMounted } from 'vue'
import { Search } from '@element-plus/icons-vue'
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
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const searchQuery = ref('')

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

    const res = await getSensitiveWordUsingGet()
    console.log(res)
    tableData.value = res.data
    // total.value = res.data
  } catch (error) {
    console.error(error)
  }
}

// 搜索
const handleSearch = () => {
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
  form.status = row.status
  dialogVisible.value = true
}

// 删除
const handleDelete = (index: number, row: SensitiveWord) => {
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
    const res = await updateSensitiveWordUsingPut({ id: row.id }, { status: row.status })
    // if (!res.data) { ElMessage.success('状态更新成功') }

  } catch (error) {
    // 回滚状态
    row.status = row.status === '1' ? '0' : '1'
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!form.word.trim()) {
    ElMessage.warning('请输入敏感词')
    return
  }

  try {
    if (form.id) {
      await updateSensitiveWordUsingPut({ id: form.id }, form)
      ElMessage.success('更新成功')
    } else {
      await addSensitiveWordUsingPost(form)
      ElMessage.success('添加成功')
    }
    dialogVisible.value = false
    fetchData()
  } catch (error) {
    console.error(error)
  }
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.sensitive-words {
  padding: 20px;
}
</style>
