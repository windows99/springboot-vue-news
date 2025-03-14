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
            <el-select v-model="selectedTag" placeholder="请选择新闻标签">
              <el-option v-for="tag in tags" :key="tag" :label="tag" :value="tag" />
            </el-select>
          </el-col>

          <el-col :span="12">
            <el-button type="primary" :loading="loading.fetch" @click="fetchNews">
              <el-icon>
                <Download />
              </el-icon>
              获取新闻
            </el-button>
          </el-col>
        </el-row>

        <el-table v-if="newsList.length > 0" :data="newsList" style="width: 100%; margin-top: 20px">
          <el-table-column prop="title" label="标题" />
          <el-table-column prop="src" label="来源" />
          <el-table-column prop="url" label="来源链接" />
          <el-table-column prop="time" label="发布时间" />
        </el-table>

        <el-alert v-if="newsList.length === 0 && !loading.fetch" type="info" title="请先选择标签并获取新闻" show-icon
          style="margin-top: 20px" />
      </el-card>
    </el-main>
  </el-container>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { Download, Upload } from '@element-plus/icons-vue'
import axios from 'axios'
// import { useNewsStore } from '@/stores/news'
import { ElMessage } from 'element-plus'
import {
  getTagListUsingGet
} from '@/api/newsTagController'
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
    ElMessage.warning('请先选择新闻标签')
    return
  }

  loading.value.fetch = true
  try {
    axios.post('https://jisunews.market.alicloudapi.com/news/get', {
      channel: '头条',
      num: 20,
      start: 0,
    }, {
      headers: {
        'Authorization': 'APPCODE ' + '71f6a5c816cb4e33b91615b08b10f392'
      }
    })
      .then(res => console.log(res))
      .catch(err => console.log(err))

    // axios({
    //   method: 'ANY',
    //   url: 'https://jisunews.market.alicloudapi.com/news/get',
    //   dataType: "json",
    //   beforeSend: function (request) {	//向接口发送身份认证
    //     request.setRequestHeader("Authorization", "APPCODE " + "71f6a5c816cb4e33b91615b08b10f392");//注意这里APPCODE后面有一个空格，不能删掉
    //   },
    //   data: {
    //     channel: '头条',
    //     num: 20,
    //     start: 0,
    //   }
    // }).then(res => {
    //   console.log(res)
    // })

    // 调用三方API获取新闻数据
    const response = await fetch(``)
    const data = await response.json()
    newsList.value = data.map(item => ({
      ...item,
      publishTime: new Date(item.publishTime).toLocaleString()
    }))
  } catch (error) {
    // ElMessage.error('获取新闻失败，请稍后重试')
  } finally {
    loading.value.fetch = false
  }
}


onMounted(async () => {
  const queryRequest = ({
    current: 1,
    pageSize: 100
  });
  const res = await getTagListUsingGet(queryRequest)
  if (res.code === 0) {
    tags.value = res.data.map(item => item.tagname)
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
