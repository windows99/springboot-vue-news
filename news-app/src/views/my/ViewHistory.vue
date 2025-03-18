<template>
  <NavBar />

  <el-card class="box-card">
    <template #header>
      <div class="card-header">
        <span>浏览记录</span>
        <el-button style="margin-left: 20px;" type="danger" @click="handleClearAll" :disabled="!viewHistory.length">全部清空</el-button>
      </div>
    </template>

    <el-table :data="viewHistory" style="width: 100%" v-loading="loading" empty-text="暂无浏览记录">
      <el-table-column prop="newsTitle" label="标题" />
      <el-table-column prop="viewTime" label="浏览时间" width="180" />
      <el-table-column label="操作" width="240">
        <template #default="scope">
          <el-button size="small" @click="handleView(scope.row)">
            查看
          </el-button>
          <el-button type="danger" size="small" @click="handleDelete(scope.row)">
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-card>
  <FooterBar />
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import NavBar from '@/components/NavBar.vue'
import FooterBar from '@/components/FooterBar.vue'
import { getViewHistoryUsingGet, deleteViewByIdUsingPost, deleteAllViewsByUserIdUsingPost } from '@/api/userNewsViewController'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const loading = ref(false)
const viewHistory = ref([])
import { useUserStoreHook } from '@/stores/modules/user'

// 模拟数据

const useUserStore = useUserStoreHook()


onMounted(() => {
  loading.value = true
  // 模拟异步获取数据
  fetchData()
})

const fetchData = async () => {
  const res = await getViewHistoryUsingGet({ userId: useUserStore.user.id })
  viewHistory.value = res.data
  console.log(res.data)
  console.log(viewHistory.value)
  loading.value = false
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除这条浏览记录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    loading.value = true
    // 调用删除接口
    await deleteViewByIdUsingPost({ viewId: row.id })
    // 重新获取数据
    await fetchData()
    ElMessage.success('删除成功')
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
      ElMessage.error('删除失败')
    }
  } finally {
    loading.value = false
  }
}

const handleClearAll = async () => {
  try {
    await ElMessageBox.confirm('确定要清空所有浏览记录吗？此操作不可恢复！', '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    loading.value = true
    // 调用清空接口
    await deleteAllViewsByUserIdUsingPost({ userId: useUserStore.user.id })
    // 重新获取数据
    await fetchData()
    ElMessage.success('清空成功')
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
      ElMessage.error('清空失败')
    }
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.box-card {
  margin: 20px;
  margin-top: 80px;
  min-height: 600px;
}
</style>
