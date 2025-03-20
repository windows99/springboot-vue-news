<template>
  <NavBar />

  <el-row justify="center">
    <el-col :span="18">
      <el-card class="box-card">
        <template #header>
          <div class="card-header">
            <span>浏览记录</span>
            <el-button style="margin-left: 20px;" type="danger" @click="handleClearAll"
              :disabled="!viewHistory.length">全部清空</el-button>
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
    </el-col>
  </el-row>

  <FooterBar />
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import NavBar from '@/components/NavBar.vue'
import FooterBar from '@/components/FooterBar.vue'
import { 
  getViewHistoryUsingGet, 
  deleteViewByIdUsingPost, 
  deleteAllViewsByUserIdUsingPost
} from '@/api/userNewsViewController'
import type { NewsView } from '@/api/typings.d'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStoreHook } from '@/stores/modules/user'

const router = useRouter()
const useUserStore = useUserStoreHook()

// 定义浏览记录项接口
interface ViewHistoryItem extends NewsView {
  id: number
  newsTitle: string
  viewTime: string
}

// 加载状态
const loading = ref<boolean>(false)
// 浏览记录数据
const viewHistory = ref<ViewHistoryItem[]>([])

// 组件挂载时获取数据
onMounted(() => {
  loading.value = true
  fetchData()
})

/**
 * 获取浏览记录数据
 */
const fetchData = async () => {
  try {
    const res = await getViewHistoryUsingGet({ params: { userId: useUserStore.user.id } })
    if (res.data) {
      viewHistory.value = res.data.map((item: NewsView) => ({
        id: item.id || 0,
        newsTitle: item.newsTitle || '',
        viewTime: item.viewTime || '',
        ...item
      })) as ViewHistoryItem[]
    }
  } catch (error) {
    console.error('获取浏览记录失败:', error)
    ElMessage.error('获取浏览记录失败')
  } finally {
    loading.value = false
  }
}

/**
 * 查看新闻详情
 * @param row 当前浏览记录项
 */
const handleView = (row: ViewHistoryItem) => {
  router.push(`/news/${row.id}`)
}

/**
 * 删除单条浏览记录
 * @param row 当前浏览记录项
 */
const handleDelete = async (row: ViewHistoryItem) => {
  try {
    await ElMessageBox.confirm('确定要删除这条浏览记录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    loading.value = true
    await deleteViewByIdUsingPost({ viewId: row.id })
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

/**
 * 清空所有浏览记录
 */
const handleClearAll = async () => {
  try {
    await ElMessageBox.confirm('确定要清空所有浏览记录吗？此操作不可恢复！', '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    loading.value = true
    await deleteAllViewsByUserIdUsingPost({ userId: useUserStore.user.id })
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
