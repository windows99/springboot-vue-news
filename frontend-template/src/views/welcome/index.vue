<template>
  <div class="dashboard-container">
    <!-- 顶部统计卡片 -->
    <el-row :gutter="20" class="stat-cards">
      <el-col :span="6" v-for="(stat, index) in statistics" :key="index">
        <el-card class="stat-card" :body-style="{ padding: '20px' }">
          <div class="stat-icon">
            <el-icon :size="40" :color="stat.color">
              <component :is="stat.icon" />
            </el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stat.value }}</div>
            <div class="stat-label">{{ stat.label }}</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="20" class="chart-row">
      <!-- 新闻分类统计 -->
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>新闻分类统计</span>
              <el-tag type="success">实时</el-tag>
            </div>
          </template>
          <div class="chart-container" ref="categoryChartRef"></div>
        </el-card>
      </el-col>

      <!-- 新闻标签统计 -->
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>新闻标签分布</span>
              <el-tag type="warning">饼图</el-tag>
            </div>
          </template>
          <div class="chart-container" ref="tagChartRef"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 趋势分析 -->
    <el-row :gutter="20" class="chart-row">
      <el-col :span="24">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>新闻发布趋势</span>
              <div class="header-actions">
                <el-radio-group v-model="trendTimeRange" size="small">
                  <el-radio-button label="7">近7天</el-radio-button>
                  <el-radio-button label="30">近30天</el-radio-button>
                </el-radio-group>
              </div>
            </div>
          </template>
          <div class="chart-container" ref="trendChartRef"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 热门新闻排行 -->
    <el-row :gutter="20" class="chart-row">
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>热门新闻排行</span>
              <el-tag type="danger">TOP 10</el-tag>
            </div>
          </template>
          <div class="hot-news-list">
            <div v-for="(news, index) in hotNews" :key="index" class="hot-news-item">
              <div class="rank" :class="'rank-' + (index + 1)">{{ index + 1 }}</div>
              <div class="news-info">
                <div class="news-title">{{ news.title }}</div>
                <div class="news-stats">
                  <span><el-icon>
                      <View />
                    </el-icon> {{ news.viewCount }}</span>
                  <span><el-icon>
                      <Star />
                    </el-icon> {{ news.likeCount }}</span>
                </div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 系统状态 -->
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>系统状态</span>
              <el-tag type="info">实时监控</el-tag>
            </div>
          </template>
          <div class="system-status">
            <div class="status-item">
              <div class="status-label">CPU使用率</div>
              <el-progress :percentage="systemStatus.cpuUsage" :color="getProgressColor(systemStatus.cpuUsage)"
                :stroke-width="20" :show-text="false" />
              <div class="status-value">{{ systemStatus.cpuUsage }}%</div>
            </div>
            <div class="status-item">
              <div class="status-label">内存使用率</div>
              <el-progress :percentage="systemStatus.memoryUsage" :color="getProgressColor(systemStatus.memoryUsage)"
                :stroke-width="20" :show-text="false" />
              <div class="status-value">{{ systemStatus.memoryUsage }}%</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { BarChart, LineChart, PieChart } from 'echarts/charts'
import {
  GridComponent,
  TooltipComponent,
  LegendComponent,
  TitleComponent
} from 'echarts/components'
import * as echarts from 'echarts/core'
import { View, Star } from '@element-plus/icons-vue'
import { getDashboardDataUsingGet } from '@/api/dashboardController'

// 注册 ECharts 组件
use([
  CanvasRenderer,
  BarChart,
  LineChart,
  PieChart,
  GridComponent,
  TooltipComponent,
  LegendComponent,
  TitleComponent
])

// 数据
const statistics = ref([
  { label: '总新闻数', value: 0, icon: 'Document', color: '#409EFF' },
  { label: '总用户数', value: 0, icon: 'User', color: '#67C23A' },
  { label: '今日新增新闻', value: 0, icon: 'Plus', color: '#E6A23C' },
  { label: '今日新增用户', value: 0, icon: 'UserFilled', color: '#F56C6C' }
])

const trendTimeRange = ref('7')
const hotNews = ref([])
const systemStatus = ref({
  cpuUsage: 0,
  memoryUsage: 0
})

// 图表容器引用
const categoryChartRef = ref(null)
const tagChartRef = ref(null)
const trendChartRef = ref(null)

// 图表实例
let categoryChart = null
let tagChart = null
let trendChart = null

// 初始化图表
const initCharts = () => {
  // 使用 ref 引用的 DOM 元素初始化图表
  categoryChart = echarts.init(categoryChartRef.value)
  tagChart = echarts.init(tagChartRef.value)
  trendChart = echarts.init(trendChartRef.value)
}

// 更新图表数据
const updateCharts = (data) => {
  if (!data) return

  // 更新分类统计图表
  categoryChart?.setOption({
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: data.categoryStats?.map(item => item.categoryName) || [] },
    yAxis: { type: 'value' },
    series: [{
      data: data.categoryStats?.map(item => item.count) || [],
      type: 'bar',
      barWidth: '60%',
      itemStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: '#83bff6' },
          { offset: 0.5, color: '#188df0' },
          { offset: 1, color: '#188df0' }
        ])
      }
    }]
  })

  // 更新标签统计图表
  tagChart?.setOption({
    tooltip: { trigger: 'item' },
    legend: { orient: 'vertical', left: 'left' },
    series: [{
      type: 'pie',
      radius: '50%',
      data: data.tagStats?.map(item => ({
        name: item.tagName,
        value: item.count
      })) || [],
      emphasis: {
        itemStyle: {
          shadowBlur: 10,
          shadowOffsetX: 0,
          shadowColor: 'rgba(0, 0, 0, 0.5)'
        }
      }
    }]
  })

  // 更新趋势图表
  trendChart?.setOption({
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: data.newsTrend?.map(item => item.date) || [] },
    yAxis: { type: 'value' },
    series: [{
      data: data.newsTrend?.map(item => item.count) || [],
      type: 'line',
      smooth: true,
      areaStyle: {
        opacity: 0.3
      }
    }]
  })
}

// 获取进度条颜色
const getProgressColor = (percentage) => {
  if (percentage < 60) return '#67C23A'
  if (percentage < 80) return '#E6A23C'
  return '#F56C6C'
}

// 获取仪表盘数据
const fetchDashboardData = async () => {
  try {
    const res = await getDashboardDataUsingGet()
    if (res.code === 0 && res.data) {
      statistics.value[0].value = res.data.totalNews || 0
      statistics.value[1].value = res.data.totalUsers || 0
      statistics.value[2].value = res.data.todayNews || 0
      statistics.value[3].value = res.data.todayUsers || 0
      hotNews.value = res.data.hotNews || []
      systemStatus.value = res.data.systemStatus || { cpuUsage: 0, memoryUsage: 0 }
      updateCharts(res.data)
    }
  } catch (error) {
    console.error('获取仪表盘数据失败:', error)
  }
}

// 定时刷新数据
let timer = null
const startAutoRefresh = () => {
  timer = setInterval(fetchDashboardData, 30000) // 每30秒刷新一次
}

// 监听窗口大小变化，调整图表大小
const handleResize = () => {
  categoryChart?.resize()
  tagChart?.resize()
  trendChart?.resize()
}

onMounted(() => {
  initCharts()
  fetchDashboardData()
  startAutoRefresh()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  if (timer) clearInterval(timer)
  window.removeEventListener('resize', handleResize)
  categoryChart?.dispose()
  tagChart?.dispose()
  trendChart?.dispose()
})
</script>

<style scoped>
.dashboard-container {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: 100vh;
}

.stat-cards {
  margin-bottom: 20px;
}

.stat-card {
  display: flex;
  align-items: center;
  transition: all 0.3s;
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, .1);
}

.stat-icon {
  margin-right: 20px;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}

.chart-row {
  margin-bottom: 20px;
}

.chart-card {
  height: 400px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chart-container {
  height: 320px;
}

.hot-news-list {
  height: 320px;
  overflow-y: auto;
}

.hot-news-item {
  display: flex;
  align-items: center;
  padding: 10px;
  border-bottom: 1px solid #EBEEF5;
}

.rank {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 10px;
  font-weight: bold;
  color: #fff;
}

.rank-1 {
  background-color: #F56C6C;
}

.rank-2 {
  background-color: #E6A23C;
}

.rank-3 {
  background-color: #67C23A;
}

.news-info {
  flex: 1;
}

.news-title {
  font-size: 14px;
  color: #303133;
  margin-bottom: 5px;
}

.news-stats {
  font-size: 12px;
  color: #909399;
}

.news-stats span {
  margin-right: 15px;
}

.system-status {
  padding: 20px;
}

.status-item {
  margin-bottom: 20px;
}

.status-label {
  margin-bottom: 10px;
  color: #606266;
}

.status-value {
  text-align: right;
  margin-top: 5px;
  color: #909399;
}

/* 自定义滚动条样式 */
.hot-news-list::-webkit-scrollbar {
  width: 6px;
}

.hot-news-list::-webkit-scrollbar-thumb {
  background-color: #909399;
  border-radius: 3px;
}

.hot-news-list::-webkit-scrollbar-track {
  background-color: #F5F7FA;
}
</style>