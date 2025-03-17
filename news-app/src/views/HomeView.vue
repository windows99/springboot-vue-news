<template>
  <NavBar />
  <el-container style="margin-top: 60px;">

    <!-- 主要内容区域 -->
    <el-main style="padding: 0;">
      <!-- 轮播图 -->
      <el-carousel height="400px" :interval="5000" arrow="always">
        <el-carousel-item v-for="item in carouselItems" :key="item.id">
          <img :src="item.image" :alt="item.title" class="carousel-image" />
        </el-carousel-item>
      </el-carousel>

      <!-- 新闻分类标签页 -->
      <el-row justify="center">
        <el-col :span="18">
          <el-tabs class="tabs-box" v-model="activeTab" type="card" @tab-click="handleTabClick">
            <el-tab-pane v-for="tab in tabs" :key="tab.id" :label="tab.tagname" :name="tab.id">

              <el-row :gutter="20">
                <el-col v-for="news in newsList" :key="news.id" :span="24" class="news-item">
                  <el-card shadow="hover" class="card-border">
                    <el-row>
                      <!-- 左侧图片 -->
                      <el-col :span="4">
                        <el-image :src="news.coverimage" fit="cover" :preview-src-list="[news.coverimage]" />
                      </el-col>

                      <!-- 右侧内容 -->
                      <el-col :span="20" class="news-content">
                        <el-text line-clamp="1">{{ news.title }}</el-text>
                        <br />
                        <el-text line-clamp="3" size="small">{{ htmlToText(news.content) }}</el-text>
                        <br />
                        <el-button type="primary" size="small" @click="viewDetail(news.id)">
                          查看更多
                        </el-button>
                      </el-col>
                    </el-row>
                  </el-card>
                </el-col>
              </el-row>



            </el-tab-pane>
          </el-tabs>
        </el-col>
      </el-row>

    </el-main>

  </el-container>

  <footer-bar />
</template>

<script setup>
import { ref, onMounted, shallowRef } from 'vue'
import { getNewsListsUsingPost } from "@/api/newsController"
import { getTagListUsingGet } from "@/api/newsTagController"
import { useRouter } from 'vue-router'
import NavBar from '../components/NavBar.vue'
import FooterBar from '../components/FooterBar.vue'
import { Editor, Toolbar } from '@wangeditor/editor-for-vue'

import '@wangeditor/editor/dist/css/style.css' // 引入 css

const route = useRouter()

//  编辑器配置
const editorRef = shallowRef()
const toolbarConfig = {}
const editorConfig = { placeholder: '请输入内容...' }
const mode = "default"
const valueHtml = ref('<p>hello</p>')


const newsList = ref([])

// 轮播图数据
const carouselItems = ref([
  {
    id: 1,
    image: 'src/assets/banner/1.jpg',
    title: '新闻1'
  },
  {
    id: 2,
    image: 'src/assets/banner/2.jpg',
    title: '新闻2'
  },
  {
    id: 3,
    image: 'src/assets/banner/3.jpg',
    title: '新闻3'
  }
])

const tabs = ref([
  { id: 1, name: 'all', tagname: '全部' },
])

// 标签页
const activeTab = ref(1)
const viewDetail = (id) => {
  route.push('/news/' + id)

}

const htmlToText = (html) => {
  const parser = new DOMParser();
  const doc = parser.parseFromString(html, 'text/html');
  return doc.body.textContent || '';
}

const handleTabClick = (tab, event) => {
  fetchData(tab.props.name)
}


// 获取新闻列表
const fetchData = async (category) => {
  try {
    // 合并分页参数和查询条件
    let params = {
      current: 1,
      pageSize: 10,
      status: 0,
      category: category === 1 ? null : category
    }

    const res = await getNewsListsUsingPost(params)
    if (res.code == 0) {
      newsList.value = res.data.records


    }
  } finally {
    console.log("获取新闻列表成功")
  }
}

// 获取新闻标签列表
const fetchTabsData = async () => {
  try {
    // 合并分页参数和查询条件
    let params = {
      page: 1,
      pageSize: 100
    }
    const res = await getTagListUsingGet(params)
    if (res.code == 0) {
      tabs.value.push(...res.data)
    }
  } finally {
    console.log("err")
  }
}




onMounted(() => {
  fetchTabsData()
  fetchData(1)
})

</script>

<style scoped>
.tabs-box .el-tabs__item {
  font-size: 20px;
}

.carousel-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.card-border {
  border-radius: 18px;
}



.news-content {
  padding-left: 20px;
}



.news-description {
  color: #666;
  margin-bottom: 10px;
}

.news-time {
  color: #999;
  font-size: 12px;
}

.footer-content {
  text-align: center;
  padding: 20px;
}

.user-avatar {
  float: right;
}
</style>
