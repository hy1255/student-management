<template>
  <div style="padding: 24px;">
    <div class="page-header">
      <h1>👋 欢迎回来，{{ userInfo?.realName || '用户' }}！</h1>
      <p style="color: #909399; margin-top: 8px;">今天是 {{ today }}</p>
    </div>

    <!-- 统计卡片 -->
    <div class="stat-cards">
      <div class="stat-card"><div class="stat-icon blue">👨‍🎓</div><div class="stat-info"><div class="stat-value">{{ stats.studentCount }}</div><div class="stat-label">学生总数</div></div></div>
      <div class="stat-card"><div class="stat-icon green">📚</div><div class="stat-info"><div class="stat-value">{{ stats.courseCount }}</div><div class="stat-label">课程总数</div></div></div>
      <div class="stat-card"><div class="stat-icon orange">📝</div><div class="stat-info"><div class="stat-value">{{ stats.examCount }}</div><div class="stat-label">考试总数</div></div></div>
      <div class="stat-card"><div class="stat-icon purple">📊</div><div class="stat-info"><div class="stat-value">{{ stats.avgScore }}</div><div class="stat-label">平均分</div></div></div>
    </div>

    <!-- 两个柱状图 -->
    <div class="charts-row">
      <div class="panel">
        <div class="panel-header"><h3>📊 各考试及格率分布</h3></div>
        <div class="panel-body"><div id="chartPassRate" class="chart-box" style="height: 280px;"></div></div>
      </div>
      <div class="panel">
        <div class="panel-header"><h3>📊 各课程平均分对比</h3></div>
        <div class="panel-body"><div id="chartCourseAvg" class="chart-box" style="height: 280px;"></div></div>
      </div>
    </div>

    <!-- 近期考试安排 -->
    <div class="panel" style="margin-top: 20px;">
      <div class="panel-header"><h3>📋 近期考试安排</h3></div>
      <div class="panel-body">
        <div v-if="recentExams.length === 0" style="text-align:center;padding:40px 0;color:#909399;">
          <div style="font-size:48px;margin-bottom:12px;">📭</div>
          <p>暂无考试安排</p>
        </div>
        <div v-else class="table-wrap">
          <table>
            <thead>
            <tr>
              <th>考试名称</th>
              <th>关联课程</th>
              <th>考试日期</th>
              <th>考试时间</th>
              <th>考试地点</th>
              <th>状态</th>
            </tr>
            </thead>
            <tbody>
            <tr v-for="exam in recentExams" :key="exam.examName + exam.examDate">
              <td><b>{{ exam.examName }}</b></td>
              <td>{{ exam.courseName || '未知课程' }}</td>
              <td>{{ exam.examDate }}</td>
              <td>{{ exam.examTime || '-' }}</td>
              <td>{{ exam.location || '-' }}</td>
              <td><span class="tag" :class="getStatusTag(exam.status)">{{ getStatusLabel(exam.status) }}</span></td>
            </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import * as echarts from 'echarts'
import { getDashboardStats } from '@/api/dashboard'

const userInfo = ref(null)
const stats = ref({
  studentCount: 0,
  courseCount: 0,
  examCount: 0,
  avgScore: 0
})
const recentExams = ref([])
const today = ref('')

const getStatusTag = (status) => {
  const map = { 'PENDING': 'tag-blue', 'ONGOING': 'tag-green', 'FINISHED': 'tag-gray' }
  return map[status] || 'tag-gray'
}
const getStatusLabel = (status) => {
  const map = { 'PENDING': '待考', 'ONGOING': '进行中', 'FINISHED': '已结束' }
  return map[status] || status
}

const loadData = async () => {
  try {
    const res = await getDashboardStats()
    const data = res.data
    stats.value = {
      studentCount: data.studentCount || 0,
      courseCount: data.courseCount || 0,
      examCount: data.examCount || 0,
      avgScore: data.avgScore || 0
    }
    recentExams.value = data.recentExams || []

    await nextTick()
    // 传入正确格式的数据
    renderPassRateChart(data.passRateData || [])
    renderCourseAvgChart(data.courseAvgData || [])
  } catch (error) {
    console.error('加载仪表盘数据失败', error)
  }
}

// 各考试及格率分布（柱状图）
const renderPassRateChart = (data) => {
  const chartDom = document.getElementById('chartPassRate')
  if (!chartDom) return
  const myChart = echarts.init(chartDom)

  const names = data.map(item => item.examName)
  const values = data.map(item => item.passRate)

  if (names.length === 0) {
    myChart.setOption({
      title: { text: '暂无数据', left: 'center', top: 'center', textStyle: { color: '#909399', fontSize: 14 } }
    })
    return
  }

  const option = {
    tooltip: {
      trigger: 'axis',
      formatter: (params) => {
        const p = params[0]
        return `<b>${p.name}</b><br/>及格率: ${p.value}%`
      }
    },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: {
      type: 'category',
      data: names,
      axisLabel: { fontSize: 11, rotate: 15 }
    },
    yAxis: {
      type: 'value',
      min: 0,
      max: 100,
      name: '及格率 (%)'
    },
    series: [{
      name: '及格率',
      type: 'bar',
      data: values,
      barWidth: '35%',
      itemStyle: {
        borderRadius: [4, 4, 0, 0],
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: '#67C23A' },
          { offset: 1, color: '#409EFF' }
        ])
      },
      label: {
        show: true,
        position: 'top',
        formatter: (p) => p.value + '%'
      }
    }]
  }
  myChart.setOption(option)
  window.addEventListener('resize', () => myChart.resize())
}

// 各课程平均分对比（柱状图）
const renderCourseAvgChart = (data) => {
  const chartDom = document.getElementById('chartCourseAvg')
  if (!chartDom) return
  const myChart = echarts.init(chartDom)

  const names = data.map(item => item.courseName)
  const values = data.map(item => item.avgScore)

  if (names.length === 0) {
    myChart.setOption({
      title: { text: '暂无数据', left: 'center', top: 'center', textStyle: { color: '#909399', fontSize: 14 } }
    })
    return
  }

  const option = {
    tooltip: {
      trigger: 'axis',
      formatter: (params) => {
        const p = params[0]
        return `<b>${p.name}</b><br/>平均分: ${p.value} 分`
      }
    },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: {
      type: 'category',
      data: names,
      axisLabel: { fontSize: 11, rotate: 15 }
    },
    yAxis: {
      type: 'value',
      min: 0,
      max: 100,
      name: '平均分'
    },
    series: [{
      name: '平均分',
      type: 'bar',
      data: values,
      barWidth: '35%',
      itemStyle: {
        borderRadius: [4, 4, 0, 0],
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: '#667eea' },
          { offset: 1, color: '#764ba2' }
        ])
      },
      label: {
        show: true,
        position: 'top',
        formatter: (p) => p.value + '分'
      }
    }]
  }
  myChart.setOption(option)
  window.addEventListener('resize', () => myChart.resize())
}

onMounted(() => {
  const user = localStorage.getItem('user')
  if (user) userInfo.value = JSON.parse(user)
  const now = new Date()
  const weekdays = ['星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六']
  today.value = `${now.getFullYear()}年${now.getMonth() + 1}月${now.getDate()}日 ${weekdays[now.getDay()]}`
  loadData()
})
</script>

<style scoped>
.page-header { margin-bottom: 20px; }
.page-header h1 { font-size: 24px; font-weight: 600; }
.stat-cards { display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px; margin-bottom: 20px; }
.stat-card { background: #fff; border-radius: 12px; padding: 20px 24px; box-shadow: 0 2px 12px rgba(0,0,0,.08); display: flex; align-items: center; gap: 16px; }
.stat-icon { width: 52px; height: 52px; border-radius: 12px; display: flex; align-items: center; justify-content: center; font-size: 24px; flex-shrink: 0; }
.stat-icon.blue { background: #E8F4FD; color: #409EFF; }
.stat-icon.green { background: #F0F9EB; color: #67C23A; }
.stat-icon.orange { background: #FDF6EC; color: #E6A23C; }
.stat-icon.purple { background: #F3E8FF; color: #9333EA; }
.stat-info .stat-value { font-size: 28px; font-weight: 700; color: #303133; line-height: 1; }
.stat-info .stat-label { font-size: 14px; color: #909399; margin-top: 4px; }
.charts-row { display: grid; grid-template-columns: 1fr 1fr; gap: 16px; margin-top: 20px; }
.panel { background: #fff; border-radius: 12px; box-shadow: 0 2px 12px rgba(0,0,0,.08); }
.panel-header { padding: 16px 20px; border-bottom: 1px solid #EBEEF5; }
.panel-header h3 { font-size: 16px; font-weight: 600; }
.panel-body { padding: 20px; }
.table-wrap { overflow-x: auto; }
table { width: 100%; border-collapse: collapse; }
table th, table td { padding: 12px 14px; text-align: left; font-size: 14px; border-bottom: 1px solid #EBEEF5; }
table th { background: #FAFBFC; color: #606266; font-weight: 600; font-size: 13px; }
.tag { display: inline-block; padding: 2px 10px; border-radius: 4px; font-size: 12px; font-weight: 500; }
.tag-blue { background: #ECF5FF; color: #409EFF; }
.tag-green { background: #F0F9EB; color: #67C23A; }
.tag-gray { background: #F4F4F5; color: #909399; }
@media (max-width: 1200px) {
  .stat-cards { grid-template-columns: repeat(2, 1fr); }
  .charts-row { grid-template-columns: 1fr; }
}
</style>