<template>
  <div class="news-review">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>新闻审核</span>
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
            <el-form-item label="标题">
              <el-input v-model="queryRequest.title" placeholder="输入标题" clearable />
            </el-form-item>
          </el-col>

          <el-col :xs="24" :sm="12" :md="6" :lg="4" :xl="4">
            <el-form-item label="作者">
              <el-input v-model="queryRequest.author" placeholder="输入作者" clearable />
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



      <el-table :data="tableData" style="width: 100%">
        <el-table-column prop="id" label="ID" width="200" />
        <el-table-column prop="title" label="标题" />
        <el-table-column prop="author" label="作者" width="120" />
        <el-table-column prop="category" label="分类" width="120" :formatter="formatCategory" />
        <el-table-column prop="createtime" label="发布日期" width="180" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag type="info">
              待审核
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button size="small" @click="showDetail(scope.row)">查看</el-button>
            <el-button size="small" type="success" @click="approve(scope.row.id)">通过</el-button>
            <el-button size="small" type="danger" @click="reject(scope.row.id)">拒绝</el-button>
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

<script lang="ts" setup>
import { onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getNewsListsUsingPost, updateNewsUsingPut } from '@/api/newsController'
import { useNewsStore } from "@/store/modules/news"


// 待审核新闻列表
const tableData = ref([])
const loading = ref<Boolean>(false)
const currentPage = ref<Number>(1)
const pageSize = ref<Number>(10)
const total = ref<Number>(0)
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

const newsTagList = useNewsStore().newsTagList

interface NewsItem {
  id: string
  title: string
  content: string
  author: string
  submitTime: string
  category: string
}

// 处理查询
const handleSearch = () => {
  // currentPage.value = 1
  fetchData()
}

//  处理分类格式化
const formatCategory = (row) => {
  const tag = newsTagList.find(item => item.id === row.category)
  return tag ? tag.tagname : '-'
}


// 详情对话框可见性
const detailVisible = ref(false)

// 分页变化
const handlePageChange = (newPage: number) => {
  currentPage.value = newPage
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

// 显示新闻详情
const showDetail = (news: NewsItem) => {
  // currentNews.value = news
  // detailVisible.value = true
}


//  获取数据
const fetchData = async () => {
  try {
    loading.value = true
    // 合并分页参数和查询条件
    const params = {
      ...queryRequest.value,
      current: currentPage.value,
      pageSize: pageSize.value,
      status: 1,
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

onMounted(() => {
  fetchData()
})

// 通过审核
const approve = (id: string) => {
  ElMessageBox.confirm('确定要通过该新闻吗?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await updateNewsUsingPut({ id: id }, { status: 2 })
    fetchData()
    // TODO: 调用API通过审核
    ElMessage.success('审核通过')
    detailVisible.value = false
  })
}

// 拒绝审核
const reject = (id: string) => {
  ElMessageBox.prompt('请输入拒绝原因', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    inputPattern: /\S+/,
    inputErrorMessage: '请输入拒绝原因'
  }).then(async (value) => {
    await updateNewsUsingPut({ id: id }, { status: 4, notes: value.value })
    fetchData()
    // TODO: 调用API拒绝审核并提交原因
    ElMessage.success(`已拒绝: ${value.value}`)
    detailVisible.value = false
  })
}
</script>

<style scoped></style>
