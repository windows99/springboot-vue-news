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
                <el-option v-for="item in newsStatusList" :key="item.id" :label="item.name" :value="item.id" />
              </el-select>
            </el-form-item>
          </el-col>

          <el-col :xs="24" :sm="12" :md="6" :lg="4" :xl="4">
            <el-form-item label="新闻ID">
              <el-input v-model="queryRequest.id" placeholder="请输入新闻ID" clearable />
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
        <el-table-column prop="status" label="状态" width="100" :formatter="formatStatus">
        </el-table-column>
        <el-table-column prop="notes" label="备注" width="180" />

        <el-table-column label="操作" width="300">
          <template #default="{ row }">
            <!-- <el-button-group v-for="btn in btnList" :key="btn.title">
              <el-button v-if="btn.showBtn(row)" :type="btn.type" size="small" @click="btn.fn(row)">
                {{ btn.title }}
              </el-button>
            </el-button-group> -->
            <action-buttons :row="row" :current-role="currentRole" />
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
import { ref, onMounted, watch, pushScopeId, h } from 'vue'
import { useRouter } from 'vue-router'
import {
  getNewsListsUsingPost,
  deleteNewsUsingDelete,
  publishNewsUsingPut,
  shelfNewsUsingPut, setStatusNewsUsingPut
} from '@/api/newsController'
import { ElInput, ElMessage, ElMessageBox, ElTag, ElButton } from "element-plus";
import { useNewsStore } from "@/store/modules/news"
import { useUserStoreHook } from '@/store/modules/user'



const newsTagList = useNewsStore().newsTagList
const newsStatusList = useNewsStore().newsStatusList

const router = useRouter()

// 表格数据
const tableData = ref([])
const loading = ref(false)
const currentPage = ref<number>(1)
const pageSize = ref<number>(10)
const total = ref<number>(0)
const queryRequest = ref({
  current: 1,
  pageSize: pageSize.value,
  category: undefined,
  status: undefined,
  author: undefined,
  title: undefined,
  id: undefined
})



// 使用用户store
const useUserStore = useUserStoreHook()
const currentRole = ref(useUserStore.roles.toString())
const statusConfig = {
  0: { label: '草稿', type: 'info' },
  1: { label: '待审核', type: 'warning' },
  2: { label: '审核通过', type: 'success' },
  3: { label: '已发布', type: '' },
  4: { label: '审核失败', type: 'danger' },
  5: { label: '已下架', type: 'info' },
  6: { label: '反馈待审', type: 'warning' }
}
const permissionMatrix = {
  view: ['admin', 'editor', 'manage'],
  edit: ['admin', 'editor', 'manage'],
  delete: ['admin'],
  submit: ['admin', 'editor'],
  recall: ['admin', 'editor'],
  audit_pass: ['admin', 'manage'],
  audit_reject: ['admin', 'manage'],
  publish: ['admin', 'manage'],
  offline: ['admin', 'manage'],
  republish: ['admin']
}


// 权限检查方法

// 按钮配置（完整版）
const actionConfig = [
  { // 编辑/修改
    label: '查看',
    action: 'view',
    states: [0, 1, 2, 3, 4, 5, 6], // 草稿、审核失败
    type: 'primary',
    visible: (row) => checkPermission('edit')
  },
  { // 编辑/修改
    label: (row) => row.status === 4 ? '修改' : '编辑',
    action: 'edit',
    states: [0, 4], // 草稿、审核失败
    type: 'primary',
    icon: 'Edit',
    visible: (row) => checkPermission('edit')
  },
  { // 删除
    label: '删除',
    action: 'delete',
    states: [0, 1, 4, 5, 6], // 可删除状态
    type: 'danger',
    icon: 'Delete',
    visible: () => checkPermission('delete')
  },
  { // 提交/重新提交
    label: (row) => row.status === 4 ? '重新提交' : '提交审核',
    action: 'submit',
    states: [0, 4],
    type: 'success',
    icon: 'Upload',
    visible: () => checkPermission('submit')
  },
  // { // 撤回
  //   label: '撤回',
  //   action: 'recall',
  //   states: [1, 6],
  //   type: 'warning',
  //   icon: 'RefreshLeft',
  //   visible: () => checkPermission('recall')
  // },
  // { // 审核通过
  //   label: '通过',
  //   action: 'audit_pass',
  //   states: [1, 6],
  //   type: 'success',
  //   icon: 'CircleCheck',
  //   visible: () => checkPermission('audit_pass')
  // },
  // { // 审核驳回
  //   label: '驳回',
  //   action: 'audit_reject',
  //   states: [1, 6],
  //   type: 'danger',
  //   icon: 'CloseBold',
  //   visible: () => checkPermission('audit_reject')
  // },
  { // 发布
    label: '发布',
    action: 'publish',
    states: [2],
    type: 'success',
    icon: 'Promotion',
    visible: () => checkPermission('publish')
  },
  { // 下架
    label: '下架',
    action: 'offline',
    states: [3],
    type: 'warning',
    icon: 'Remove',
    visible: () => checkPermission('offline')
  },
  { // 重新发布
    label: '重新发布',
    action: 'republish',
    states: [5],
    type: 'success',
    icon: 'Refresh',
    visible: () => checkPermission('republish')
  }
]
// 权限检查
const checkPermission = (action) => {
  return permissionMatrix[action]?.includes(currentRole.value)
}

// 操作处理器
const handlers = {
  view: (row) => handleView(row.id),
  edit: (row) => handleEdit(row.id),
  delete: (row) => handleDelete(row.id),
  submit: (row) => handleSubmit(row.id),
  // recall: (row) => updateStatus(row, 0, '已撤回为草稿'),
  // audit_pass: (row) => {
  //   const newStatus = row.status === 6 ? 3 : 2 // 反馈新闻直接发布
  //   updateStatus(row, newStatus, '审核通过')
  // },
  // audit_reject: (row) => updateStatus(row, 4, '已驳回修改'),
  publish: (row) => handlePublish(row),
  offline: (row) => handleUnpublish(row),
  republish: (row) => handlePublish(row)
}

// 统一状态更新
const updateStatus = (row, newStatus, message) => {
  ElMessage.success(`${message}，新状态：${statusConfig[newStatus].label}`)
  row.status = newStatus
}

// 删除二次确认
// const handleDelete = (row) => {
//   ElMessageBox.confirm(`确认删除 "${row.title}"？`, '警告', {
//     confirmButtonText: '确认删除',
//     cancelButtonText: '取消',
//     type: 'warning',
//     distinguishCancelAndClose: true
//   }).then(() => {
//     tableData.value = tableData.value.filter(item => item.id !== row.id)
//     ElMessage.success('删除成功')
//   })
// }

// 编辑处理
// const handleEdit = (row) => {
//   ElMessageBox.prompt('请输入新标题', '编辑新闻', {
//     confirmButtonText: '保存',
//     cancelButtonText: '取消',
//     inputValue: row.title,
//     inputPattern: /^.{5,50}$/,
//     inputErrorMessage: '标题需5-50个字符'
//   }).then(({ value }) => {
//     row.title = value
//     ElMessage.success('修改成功')
//   })
// }

// 动态按钮组件
const ActionButtons = ({ row }) => {
  const validActions = actionConfig.filter(btn => {
    return btn.states.includes(row.status) &&
      typeof btn.visible === 'function' &&
      btn.visible(row)
  })

  if (validActions.length === 0) {
    return h(ElTag, { type: 'info' }, '无可用操作')
  }

  return validActions.map(btn => h(ElButton, {
    key: btn.action,
    type: btn.type,
    size: 'small',
    // icon: btn.icon,
    onClick: () => handlers[btn.action](row),
    // style: { marginRight: '8px' }
  }, typeof btn.label === 'function' ? btn.label(row) : btn.label))
}

//  格式化分类
const formatCategory = (row) => {
  const tag = newsTagList.find(item => item.id === row.category)
  return tag ? tag.tagname : '-'
}

//  格式化状态
const formatStatus = (row) => {
  const tag = newsStatusList.find(item => item.id === row.status)
  return tag.name
}



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
    id: undefined
  }
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

const handleSubmit = async (id: number) => {
  await setStatusNewsUsingPut({ "id": id, statusInt: 1 })
  ElMessage.success('提交成功')
  fetchData();
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
  let data = {
    id: row.id
  }
  try {
    const res = await publishNewsUsingPut(data)
    ElMessage.success('发布成功')
    fetchData()
  } catch (error) {
    ElMessage.error('发布失败')
  }
}

// 下架新闻
const handleUnpublish = async (row) => {
  let data = {
    id: row.id
  }
  try {
    const res = await shelfNewsUsingPut(data)
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
