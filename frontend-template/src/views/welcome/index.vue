<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElCard, ElStatistic, ElTimeline, ElTimelineItem } from 'element-plus'
import * as echarts from 'echarts'

defineOptions({
  name: "Welcome"
})

// Mock data
const userCount = ref(12345)
const newUsersToday = ref(23)
const onlineUsers = ref(456)
const systemUptime = ref(12345) // Using number type for ElStatistic

const notifications = ref([
  { type: 'info', title: '系统公告', content: '系统将于今晚进行维护', time: '10:25' },
  { type: 'warning', title: '操作提醒', content: '请及时更新个人信息', time: '09:45' }
])

const logs = ref([
  { type: 'success' as const, content: '用户 admin 登录成功', timestamp: '2025-03-17 10:24' },
  { type: 'info' as const, content: '系统检查完成，无异常', timestamp: '2025-03-17 10:20' }
])

// Chart setup
let userGrowthChart: echarts.ECharts | null = null
let accessChart: echarts.ECharts | null = null

onMounted(() => {
  // User growth chart
  userGrowthChart = echarts.init(document.getElementById('user-growth-chart'))
  userGrowthChart.setOption({
    title: { text: '用户增长趋势' },
    xAxis: { type: 'category', data: ['1月', '2月', '3月', '4月', '5月', '6月'] },
    yAxis: { type: 'value' },
    series: [{ data: [120, 200, 150, 80, 70, 110], type: 'bar' }]
  })

  // Access chart
  accessChart = echarts.init(document.getElementById('access-chart'))
  accessChart.setOption({
    title: { text: '系统访问量' },
    xAxis: { type: 'category', data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日'] },
    yAxis: { type: 'value' },
    series: [{ data: [820, 932, 901, 934, 1290, 1330, 1320], type: 'line' }]
  })
})
</script>

<template>
  <div class="welcome-container">
    <!-- Header -->

    <!-- Statistics Section -->
    <div class="statistics-grid">
      <ElCard>
        <ElStatistic title="用户总数" :value="userCount" />
      </ElCard>
      <ElCard>
        <ElStatistic title="今日新增用户" :value="newUsersToday" />
      </ElCard>
      <ElCard>
        <ElStatistic title="当前在线用户" :value="onlineUsers" />
      </ElCard>
      <ElCard>
        <ElStatistic title="系统运行时间" :value="systemUptime" />
      </ElCard>
    </div>

    <!-- Charts Section -->
    <div class="charts-grid">
      <div id="user-growth-chart" class="chart"></div>
      <div id="access-chart" class="chart"></div>
    </div>

    <!-- Notifications Section -->
    <div class="notifications-section">
      <h2>消息通知</h2>
      <div class="notification-list">
        <div v-for="(notice, index) in notifications" :key="index" class="notification-item">
          <span class="notice-type" :class="notice.type">{{ notice.title }}</span>
          <span class="notice-content">{{ notice.content }}</span>
          <span class="notice-time">{{ notice.time }}</span>
        </div>
      </div>
    </div>

    <!-- Logs Section -->
    <div class="logs-section">
      <h2>系统日志</h2>
      <ElTimeline>
        <ElTimelineItem v-for="(log, index) in logs" :key="index" :type="log.type" :timestamp="log.timestamp">
          {{ log.content }}
        </ElTimelineItem>
      </ElTimeline>
    </div>
  </div>
</template>

<style scoped>
.welcome-container {
  padding: 20px;
}

h1 {
  text-align: center;
  margin-bottom: 30px;
}

.statistics-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.charts-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.chart {
  height: 300px;
}

.notifications-section {
  margin-bottom: 30px;
}

.notification-list {
  border: 1px solid #ebeef5;
  border-radius: 4px;
  padding: 10px;
}

.notification-item {
  display: flex;
  justify-content: space-between;
  padding: 8px 0;
  border-bottom: 1px solid #ebeef5;
}

.notification-item:last-child {
  border-bottom: none;
}

.notice-type {
  font-weight: bold;
}

.notice-type.info {
  color: #409eff;
}

.notice-type.warning {
  color: #e6a23c;
}

.logs-section {
  margin-bottom: 30px;
}
</style>
