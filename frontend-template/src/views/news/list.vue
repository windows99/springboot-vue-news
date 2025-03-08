<template>
  <div class="news-list-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span class="header-title">新闻列表</span>
          <!-- <el-button type="primary" @click="handleCreate">新建新闻</el-button> -->
        </div>
      </template>

      <!-- 查询条件 -->
      <el-form :model="queryRequest" class="query-form">
        <el-row :gutter="20">
          <el-col :xs="24" :sm="12" :md="6" :lg="4" :xl="4">
            <el-form-item label="分类">
              <el-select v-model="queryRequest.category" placeholder="全部分类" clearable>
                <el-option v-for="tag in newsTagList" :key="tag.id" :label="tag.tagname" :value="tag.id" />
              </el-select>
            </el-form-item>
          </el-col>

          <el-col :xs="24" :sm="12" :md="6" :lg="4" :xl="4">
            <el-form-item label="状态">
              <el-select v-model="queryRequest.status" placeholder="全部状态" clearable>
                <el-option label="已发布" value="1" />
                <el-option label="草稿" value="0" />
              </el-select>
            </el-form-item>
          </el-col>

          <el-col :xs="24" :sm="12" :md="8" :lg="6" :xl="6">
            <el-form-item label="发布时间">
              <el-date-picker v-model="dateRange" type="daterange" range-separator="至" start-placeholder="开始日期"
                end-placeholder="结束日期" value-format="YYYY-MM-DD" />
            </el-form-item>
          </el-col>

          <el-col :xs="24" :sm="12" :md="6" :lg="4" :xl="4">
            <el-form-item label="作者">
              <el-input v-model="queryRequest.author" placeholder="输入作者" clearable />
            </el-form-item>
          </el-col>

          <el-col :xs="24" :sm="12" :md="6" :lg="4" :xl="4">
            <el-form-item label="标题">
              <el-input v-model="queryRequest.title" placeholder="输入标题" clearable />
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
        <el-table-column prop="id" label="ID" width="200" />
        <el-table-column prop="title" label="标题" width="200" />
        <el-table-column prop="author" label="作者" width="120" />
        <el-table-column prop="category" label="分类" width="120" :formatter="formatCategory" />
        <el-table-column prop="createtime" label="发布日期" width="180" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '已发布' : '草稿' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="300">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleView(row.id)">查看</el-button>
            <el-button type="warning" size="small" @click="handleEdit(row.id)">编辑</el-button>
            <el-button v-if="row.status === 0" type="success" size="small" @click="handlePublish(row)">
              发布
            </el-button>
            <el-button v-if="row.status === 1" type="info" size="small" @click="handleUnpublish(row)">
              下架
            </el-button>
            <el-button type="danger" size="small" @click="handleDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize" :total="total"
          layout="total, sizes, prev, pager, next, jumper" @current-change="handlePageChange" />
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import {
  getNewsListsUsingPost,
  deleteNewsUsingDelete,
  publishNewsUsingPut,
  shelfNewsUsingPut
} from '@/api/newsController'
import { ElInput, ElMessage, ElMessageBox, ElTag, ElBacktop } from "element-plus";
import { useNewsStore } from "@/store/modules/news"


const newsTagList = useNewsStore().newsTagList

const router = useRouter()

// 表格数据
const tableData = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const dateRange = ref()
const queryRequest = ref({
  current: 1,
  pageSize: pageSize.value,
  category: undefined,
  status: undefined,
  author: undefined,
  title: undefined,
  startTime: undefined,
  endTime: undefined
})
function formatCategory(row) {
  const tag = newsTagList.find(item => item.id === row.category)
  return tag ? tag.tagname : '-'
}

// 处理日期范围
// watch(dateRange, (newVal) => {
//   if (newVal && newVal.length === 2) {
//     queryRequest.value.startTime = newVal[0]
//     queryRequest.value.endTime = newVal[1]
//   } else {
//     queryRequest.value.startTime = undefined
//     queryRequest.value.endTime = undefined
//   }
// })

// 处理查询
const handleSearch = () => {
  currentPage.value = 1
  fetchData()
}

// 处理重置
const handleReset = () => {
  queryRequest.value = {
    current: 1,
    pageSize: pageSize.value,
    category: undefined,
    status: undefined,
    author: undefined,
    title: undefined,
    startTime: undefined,
    endTime: undefined
  }
  dateRange.value = []
  currentPage.value = 1
  fetchData()
}

// 获取新闻列表
const fetchData = async () => {
  try {
    loading.value = true
    // 合并分页参数和查询条件
    const params = {
      ...queryRequest.value,
      current: currentPage.value,
      pageSize: pageSize.value
    }
    const res = await getNewsListsUsingPost(params)

    if (res.code === 0) {
      tableData.value = res.data.records
      total.value = Number(res.data.total)
    } else {
      ElMessage.error(res.message)
    }
  } finally {
    loading.value = false
  }
}

// 分页变化
const handlePageChange = (newPage: number) => {
  currentPage.value = newPage
  fetchData()
}

// // 新建新闻
// const handleCreate = () => {
//   router.push('/news/create')
// }

// 查看详情
const handleView = (id: number) => {
  router.push(`/news/detail/${id}`)
}

// 编辑新闻
const handleEdit = (id: number) => {
  router.push(`/news/create?id=${id}`)
}

const handleDelete = (id: number) => {
  ElMessageBox.confirm('确认删除该新闻？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    const data = { "id": id }
    await deleteNewsUsingDelete(data);
    ElMessage.success('删除成功');
    fetchData();
  });
};

// 发布新闻
const handlePublish = async (row) => {
  try {
    const res = await publishNewsUsingPut(row)
    ElMessage.success('发布成功')
    fetchData()
  } catch (error) {
    ElMessage.error('发布失败')
  }
}

// 下架新闻
const handleUnpublish = async (row) => {
  try {
    const res = await shelfNewsUsingPut(row)
    ElMessage.success('下架成功')
    fetchData()
  } catch (error) {
    ElMessage.error('下架失败')
  }
}

onMounted(() => {
  fetchData()
})
</script>
