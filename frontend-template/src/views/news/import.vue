<template>
  <el-container>
    <el-main>
      <el-card>
        <template #header>
          <div class="card-header">
            <span>新闻导入</span>
          </div>
        </template>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-select v-model="selectedTag" value-key="id" placeholder="请选择新闻标签">
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

            <el-button type="success" :loading="loading.fetch" @click="ImportNews">
              <el-icon>
                <DocumentAdd />
              </el-icon>
              保存新闻
            </el-button>
          </el-col>
        </el-row>

        <el-table v-if="newsList.length > 0" :data="newsList" style="width: 100%; margin-top: 20px">
          <el-table-column prop="title" label="标题" show-overflow-tooltip />
          <el-table-column prop="content" label="内容" show-overflow-tooltip />
          <el-table-column prop="coverimage" label="封面图片">
            <template #default="scope">
              <el-image :src="scope.row.coverimage" style="width: 100px; height: 100px;" />
            </template>
          </el-table-column>
          <el-table-column prop="source" label="来源" />
          <el-table-column prop="category" label="标签" />
          <el-table-column prop="sourceurl" label="来源链接" show-overflow-tooltip />
          <el-table-column prop="author" label="作者" />
          <el-table-column prop="time" label="创建时间" />
        </el-table>

        <el-alert v-if="newsList.length === 0 && !loading.fetch" type="info" title="请先选择标签并获取新闻" show-icon
          style="margin-top: 20px" />
      </el-card>
    </el-main>
  </el-container>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { Download, DocumentAdd } from '@element-plus/icons-vue'
import axios from 'axios'
// import { useNewsStore } from '@/stores/news'
import { ElMessage } from 'element-plus'
import {
  getTagListUsingGet
} from '@/api/newsTagController'
import { getJisunewsUsingGet, addNewsUsingPost } from '@/api/newsController'
// 新闻标签选项
const tags = ref()



// 选中的标签
const selectedTag = ref('')

// 新闻列表
const newsList = ref([])

// 加载状态
const loading = ref({
  fetch: false,
  import: false
})

// 获取新闻
const fetchNews = async () => {
  if (!selectedTag.value) {
    ElMessage.error('请先选择新闻标签')
    return
  }
  const res = await getJisunewsUsingGet({ channel: selectedTag.value.tagname });

  if (res.msg == "ok") {
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
  } else {
    // ElMessage.error(res.msg)
  }


}

// 导入新闻
const ImportNews = async () => {
  if (newsList.value.length === 0) {
    ElMessage.error('请先获取新闻')
    return
  }
  newsList.value.forEach(async (element) => {
    await addNewsUsingPost(element)
  });
  ElMessage.success('导入成功')
}


onMounted(async () => {
  const queryRequest = ({
    current: 1,
    pageSize: 100
  });
  const res = await getTagListUsingGet(queryRequest)
  if (res.code === 0) {
    tags.value = res.data
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
</style>
