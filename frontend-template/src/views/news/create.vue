<template>
  <div class="news-create-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span class="header-title">{{ isEdit ? '编辑新闻' : '新建新闻' }}</span>
          <el-button type="primary" @click="submitForm">提交</el-button>
        </div>
      </template>

      <el-form ref="formRef" :model="addNewsInfo" :rules="rules" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="标题" prop="title">
              <el-input v-model="addNewsInfo.title" placeholder="请输入新闻标题" />
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="作者" prop="author">
              <el-input v-model="addNewsInfo.author" placeholder="请输入作者" />
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="分类" prop="category">
              <el-select v-model="addNewsInfo.category" placeholder="请选择分类" filterable clearable>
                <el-option v-for="tag in tagOptions" :key="tag.id" :label="tag.tagname" :value="tag.id" />
              </el-select>
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="来源" prop="source">
              <el-input v-model="addNewsInfo.source" placeholder="请输入新闻来源" />
            </el-form-item>
          </el-col>

          <el-col :span="24">
            <el-form-item label="封面图" prop="coverimage">
              <el-upload action="#" list-type="picture-card" :auto-upload="false" :on-change="handleCoverImageChange"
                :on-remove="handleCoverImageRemove">
                <i class="el-icon-plus"></i>
              </el-upload>
            </el-form-item>
          </el-col>

          <el-col :span="24">
            <el-form-item label="正文内容" prop="content">
              <el-input v-model="addNewsInfo.content" type="textarea" :rows="6" placeholder="请输入新闻正文内容" />
            </el-form-item>
          </el-col>

          <el-col :span="24">
            <el-form-item label="图片集" prop="images">
              <el-upload action="#" list-type="picture-card" :auto-upload="false" multiple
                :on-change="handleImagesChange" :on-remove="handleImagesRemove">
                <i class="el-icon-plus"></i>
              </el-upload>
            </el-form-item>
          </el-col>

          <el-col :span="24">
            <el-form-item label="来源链接" prop="sourceurl">
              <el-input v-model="addNewsInfo.sourceurl" placeholder="请输入原文链接" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import {
  getNewsByIdUsingGet,
  addNewsUsingPost,
  updateNewsUsingPut
} from '@/api/newsController'
import { getTagListUsingGet } from '@/api/newsTagController'
import {
  uploadFileUsingPost,
  deleteFileUsingDelete
} from '@/api/fileController'
import type { UploadFile, ElMessage } from 'element-plus'
import Decimal from "decimal.js"

const router = useRouter()
const route = useRoute()
const formRef = ref()

const isEdit = ref(false)
const tagOptions = ref([])




// const showAddDialog = ref(false);
const addNewsInfo = ref<API.News>({
  category: '',
  images: "",
  coverimage: ''
})

const getNewsId = ref<API.getNewsByIdUsingGETParams>()

const rules = reactive({
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入内容', trigger: 'blur' }],
  author: [{ required: true, message: '请输入作者', trigger: 'blur' }],
  category: [{ required: true, message: '请选择分类', trigger: 'change' }],
  source: [{ required: true, message: '请输入来源', trigger: 'blur' }],
  sourceurl: [
    { required: true, message: '请输入原文链接', trigger: 'blur' },
    { type: 'url', message: '请输入正确的URL地址', trigger: 'blur' }
  ]
})

// 处理封面图上传
const handleCoverImageChange = async (file: UploadFile) => {

  const res = await uploadFileUsingPost(
    {
      biz: 'user_avatar'
    },
    {},
    file.raw as File);
  if (res.code === 0) {
    addNewsInfo.value.coverimage = res.data
  }
  console.log(res)
}

// 移除封面图
const handleCoverImageRemove = async () => {
  const url = addNewsInfo.value.coverimage;
  const data = { fileKey: url.slice(54,) }
  console.log(data)
  const res = await deleteFileUsingDelete(data)
  if (res.code === 0) {
    console.log("移除了")
    addNewsInfo.value.coverimage = null
  }
}

// 处理图片集上传
const handleImagesChange = (file: UploadFile) => {
  addNewsInfo.value.images += file.raw as File
}

// 移除图片集
const handleImagesRemove = (file: UploadFile) => {
  // const index = addNewsInfo.value.images.findIndex(f => f.name === file.name)
  // if (index !== -1) {
  //   addNewsInfo.value.images.splice(index, 1)
  // }
}

// 提交表单
const submitForm = async () => {

  try {
    await formRef.value.validate()

    if (isEdit.value) {
      await updateNewsUsingPut({ id: addNewsInfo.value.id }, addNewsInfo.value)
    } else {
      await addNewsUsingPost(addNewsInfo.value)
    }

    router.push('/news/list')
  } catch (error) {
    console.error('提交失败:', error)
  }
}


// 初始化数据（编辑模式）
const initEditData = async (id: number) => {
  console.log(id)
  // getNewsId.value.id = id
  const obj = { "id": id }
  try {

    const data = await getNewsByIdUsingGet(obj)
    console.log(data)
    addNewsInfo.value = data
  } catch (error) {
    console.error('获取详情失败:', error)
  }
}

// 初始化标签选项
const fetchTags = async () => {
  try {
    const res = await getTagListUsingGet({
      current: 1,
      pageSize: 100
    })
    if (res.code === 0) {
      tagOptions.value = res.data
    }
  } catch (error) {
    console.error('获取标签失败:', error)
    ElMessage.error('获取分类标签失败')
  }
}

onMounted(() => {
  const id = BigInt(route.query.id).toString()
  // 初始化标签数据
  fetchTags()
  if (id) {
    isEdit.value = true
    addNewsInfo.value.id = id
    initEditData(id)
  } else {
    // 初始化分类字段
    addNewsInfo.value.category = ''
  }
})
</script>

<style lang="scss" scoped>
.news-create-container {
  padding: 20px;

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;

    .header-title {
      font-size: 18px;
      font-weight: bold;
    }
  }

  :deep(.el-upload--picture-card) {
    width: 150px;
    height: 150px;
    line-height: 150px;
  }
}
</style>
