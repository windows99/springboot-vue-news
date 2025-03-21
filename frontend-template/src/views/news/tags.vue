<template>
  <div class="news-tags">
    <el-card>
      <template #header>
        <div class="flex justify-between">
          <span>新闻标签管理</span>
          <el-button type="primary" @click="handleCreate">新增标签</el-button>
        </div>
      </template>

      <el-table :data="tagList" v-loading="loading" border>
        <el-table-column prop="id" label="ID" min-width="150" />
        <el-table-column prop="tagname" label="标签名称" min-width="150" />
        <el-table-column prop="createtime" label="创建时间" width="180" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="scope">
            <el-button type="primary" size="small" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDelete(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑标签' : '新增标签'" width="30%">
      <el-form :model="formData" label-width="80px" ref="formRef">
        <el-form-item label="标签名称" prop="tagname" required>
          <el-input v-model="formData.tagname" placeholder="请输入标签名称" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script lang="ts" setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getTagListUsingGet,
  addTagUsingPost,
  updateTagUsingPut,
  deleteTagUsingDelete
} from '@/api/newsTagController'

const loading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const tagList = ref([])

// 获取新闻标签
const fetchData = async () => {
  const queryRequest = ({
    current: 1,
    pageSize: 100
  });
  try {
    loading.value = true
    const res = await getTagListUsingGet(queryRequest)
    if (res.code === 0) {
      tagList.value = res.data
    } else {
      message(res.message, { type: "error" });
    }
  } finally {
    loading.value = false
  }
}


const formData = ref({
  id: 0,
  tagname: ''
})

const handleCreate = async () => {
  formData.value = {}
  isEdit.value = false
  dialogVisible.value = true
}

const handleEdit = (row: any) => {
  formData.value.id = BigInt(row.id).toString()
  formData.value.tagname = row.tagname
  isEdit.value = true
  dialogVisible.value = true
}

const handleDelete = async (id: string) => {
  try {
    await ElMessageBox.confirm('确定删除该标签吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    const res = await deleteTagUsingDelete({ "id": +id })
    console.log(res)
    if (res.code !== 0) {
      ElMessage.error('删除失败，该标签下有新闻，无法删除')
      return
    }
    ElMessage.success('删除成功')
    fetchData()
  } catch (error) {
    // 取消删除不处理
  }
}

const handleSubmit = async () => {
  if (isEdit.value) {
    const params = { id: formData.value.id }
    const data = { tagname: formData.value.tagname }
    const res = await updateTagUsingPut(params, data)
    if (res.code === 0) {
      ElMessage.success('修改成功')
      dialogVisible.value = false
      fetchData();
    }
  } else {
    const res = await addTagUsingPost(formData.value)
    if (res.code === 0) {
      ElMessage.success('添加成功')
      dialogVisible.value = false
      fetchData();
    }
  }
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.news-tags {
  padding: 20px;
}
</style>
