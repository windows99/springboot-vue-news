<template>
  <div class="news-push-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>新闻导入</span>
        </div>
      </template>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-select v-model="selectedTag" placeholder="请选择新闻标签">
            <el-option v-for="tag in tags" :key="tag.id" :label="tag.tagname" :value="tag" />
          </el-select>
        </el-col>

        <el-col :span="12">
          <el-button type="primary" :loading="loading.fetch" @click="fetchNews">
            <el-icon>
              <Download />
            </el-icon>
            获取新闻
          </el-button>

          <el-button type="success" :loading="loading.import" @click="importNews" :disabled="selectedRows.length === 0">
            <el-icon>
              <DocumentAdd />
            </el-icon>
            保存选中新闻 ({{ selectedRows.length }})
          </el-button>
        </el-col>
      </el-row>

      <el-table v-if="newsList.length > 0" 
                :data="newsList" 
                style="width: 100%; margin-top: 20px" 
                @selection-change="handleSelectionChange"
                max-height="500"
                v-loading="loading.fetch">
        <el-table-column type="selection" width="55" />
        <el-table-column type="index" label="序号" width="70" />
        <el-table-column prop="title" label="标题" show-overflow-tooltip min-width="200" />
        <el-table-column prop="content" label="内容" min-width="300">
          <template #default="scope">
            <div class="content-preview" v-html="formatContent(scope.row.content)"></div>
          </template>
        </el-table-column>
        <el-table-column prop="coverimage" label="封面图片" width="120">
          <template #default="scope">
            <el-image 
              :src="scope.row.coverimage" 
              style="width: 80px; height: 60px;" 
              fit="cover"
              :preview-src-list="[scope.row.coverimage]"
            />
          </template>
        </el-table-column>
        <el-table-column prop="source" label="来源" width="120" />
        <el-table-column prop="sourceurl" label="来源链接" show-overflow-tooltip min-width="200">
          <template #default="scope">
            <el-link type="primary" :href="scope.row.sourceurl" target="_blank" :underline="false">
              {{ scope.row.sourceurl }}
            </el-link>
          </template>
        </el-table-column>
        <el-table-column prop="author" label="作者" width="120" />
        <el-table-column prop="time" label="创建时间" width="180" />
      </el-table>

      <el-alert v-if="newsList.length === 0 && !loading.fetch" type="info" title="请先选择标签并获取新闻" show-icon
        style="margin-top: 20px" />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { Download, DocumentAdd } from '@element-plus/icons-vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'
import {
  getTagListUsingGet
} from '@/api/newsTagController'
import { getJisunewsUsingGet, addNewsUsingPost } from '@/api/newsController'

// 新闻标签选项
const tags = ref([])

// 选中的标签
const selectedTag = ref(null)

// 新闻列表
const newsList = ref([])

// 选中的行
const selectedRows = ref([])

// 加载状态
const loading = ref({
  fetch: false,
  import: false
})

// 处理选择变化
const handleSelectionChange = (selection) => {
  selectedRows.value = selection
}

// 格式化内容
const formatContent = (content) => {
  // 移除HTML标签，保留纯文本
  const text = content.replace(/<[^>]+>/g, '')
  // 限制长度
  return text.length > 100 ? text.substring(0, 100) + '...' : text
}

// 获取新闻
const fetchNews = async () => {
  if (!selectedTag.value) {
    ElMessage.error('请先选择新闻标签')
    return
  }
  
  try {
    loading.value.fetch = true
    const res = await getJisunewsUsingGet({ channel: selectedTag.value.tagname });

    if (res.msg == "ok" && res.result?.list) {
      newsList.value = res.result.list.map(item => ({
        title: item.title,  // 标题
        content: item.content,  // 内容
        coverimage: item.pic,  // 封面图片
        source: item.src,  // 来源
        sourceurl: item.weburl,   // 来源链接
        author: item.src, // 作者
        time: item.time,  // 创建时间
        category: selectedTag.value.id,     // 标签
        commentcount: 0,
        id: 0,
        isdelete: 0,
        likecount: 0,
        status: 1,
        viewcount: 0,
        images: "",
        updatetime: ''
      }))
      
      ElMessage.success(`成功获取 ${newsList.value.length} 条新闻`)
    } else {
      ElMessage.error('获取新闻失败')
    }
  } catch (error) {
    console.error(error)
    ElMessage.error('获取新闻失败')
  } finally {
    loading.value.fetch = false
  }
}

// 导入新闻
const importNews = async () => {
  if (selectedRows.value.length === 0) {
    ElMessage.warning('请先选择要导入的新闻')
    return
  }
  
  try {
    loading.value.import = true
    let successCount = 0
    
    for (const news of selectedRows.value) {
      try {
        await addNewsUsingPost(news)
        successCount++
      } catch (err) {
        console.error(`导入新闻"${news.title}"失败:`, err)
      }
    }
    
    if (successCount > 0) {
      ElMessage.success(`成功导入 ${successCount} 条新闻`)
    } else {
      ElMessage.error('导入失败')
    }
  } catch (error) {
    console.error(error)
    ElMessage.error('导入失败')
  } finally {
    loading.value.import = false
  }
}

onMounted(async () => {
  try {
    const queryRequest = {
      current: 1,
      pageSize: 100
    };
    const res = await getTagListUsingGet(queryRequest)
    if (res.code === 0) {
      tags.value = res.data || []
    }
  } catch (error) {
    console.error('获取标签失败:', error)
    ElMessage.error('获取标签失败')
  }
})
</script>

<style scoped>
.card-header {
  font-size: 18px;
  font-weight: bold;
}

.el-select {
  width: 100%;
}

.el-table {
  margin-top: 20px;
}

.el-alert {
  margin-top: 20px;
}

.content-preview {
  max-height: 80px;
  overflow-y: auto;
  line-height: 1.5;
  padding: 5px 0;
}

/* 自定义滚动条样式 */
.content-preview::-webkit-scrollbar {
  width: 6px;
}

.content-preview::-webkit-scrollbar-thumb {
  background-color: #909399;
  border-radius: 3px;
}

.content-preview::-webkit-scrollbar-track {
  background-color: #F5F7FA;
}
</style>
