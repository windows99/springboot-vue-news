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
            <el-form-item label="来源链接" prop="sourceurl">
              <el-input v-model="addNewsInfo.sourceurl" placeholder="请输入原文链接" />
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

          <el-col :span="24" style="border: 1px solid #ccc">
            <Toolbar style="border-bottom: 1px solid #ccc" :editor="editorRef" :defaultConfig="toolbarConfig"
              :mode="mode" />
            <Editor style="height: 500px; overflow-y: hidden;" v-model="addNewsInfo.content"
              :defaultConfig="editorConfig" :mode="mode" @onCreated="handleCreated" />
            <!-- <el-form-item label="正文内容" prop="content">
              <el-input v-model="addNewsInfo.content" type="textarea" :rows="6" placeholder="请输入新闻正文内容" />
            </el-form-item> -->
          </el-col>

          <!-- <el-col :span="24">
            <el-form-item label="图片集" prop="images">
              <el-upload action="#" list-type="picture-card" :auto-upload="false" multiple
                :on-change="handleImagesChange" :on-remove="handleImagesRemove">
                <i class="el-icon-plus"></i>
              </el-upload>
            </el-form-item>
          </el-col> -->


        </el-row>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, shallowRef, onBeforeUnmount, onMounted } from 'vue'
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
import { Editor, Toolbar } from '@wangeditor/editor-for-vue'

import '@wangeditor/editor/dist/css/style.css' // 引入 css

const imageList1 = ref([])
const imageList2 = ref([])
const editorRef = shallowRef()
const toolbarConfig = {
  excludeKeys: ["insertImage", "group-video", "fullScreen"],
}
const editorConfig = {
  placeholder: '请输入内容...',
  MENU_CONF: {
    uploadImage: {
      // mock地址
      async customUpload(file: File, insertFn: InsertFnType) {
        // file 即选中的文件
        await uploadFileUsingPost({
          biz: 'user_avatar'
        }, {}, file).then(res => {
          if (res.code === 0) {
            addNewsInfo.value.coverimage = res.data
            // 自己实现上传，并得到图片 url alt href
            insertFn(res.data, "", "")
          }
        })
      },
      // 小于该值就插入 base64 格式（而不上传），默认为 0
      base64LimitSize: 20 * 1024, // 20kb
    }
  }
}

//  上传图片后获取的 src
editorConfig.MENU_CONF['insertImage'] = {
  onInsertedImage(imageNode: ImageElement | null) {
    if (imageNode == null) return
    imageList1.value.push(imageNode)
  }
}

/**
 * 删除照片
 */
const delImages = async () => {
  imageList2.value = editorRef.value.getElemsByType('image');
  //  取差集
  const delImagesList = [...imageList1.value].filter(x => [...imageList2.value].every(y => y.src !== x.src));
  delImagesList.forEach(item => {
    const data = { fileKey: item.url.slice(54,) }
    deleteFileUsingDelete(data).then(res => {
      if (res.code === 0) {
        console.log('删除成功')
      }
    })
  })
}

const handleCreated = (editor) => {
  editorRef.value = editor // 记录 editor 实例，重要！
}
// 组件销毁时，也及时销毁编辑器
onBeforeUnmount(() => {
  const editor = editorRef.value
  if (editor == null) return
  editor.destroy()
})


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
  const res = await deleteFileUsingDelete(data)
  if (res.code === 0) {
    console.log("移除了")
    addNewsInfo.value.coverimage = null
  }
}


// 提交表单
const submitForm = async () => {

  delImages();

  return
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
  let id = 0;
  if (route.query.id) {
    id = BigInt(route.query.id).toString()
  }
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
