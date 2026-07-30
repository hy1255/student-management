<template>
  <div style="padding: 24px;">
    <div class="page-header">
      <h1>👋 欢迎回来，{{ userInfo?.realName || '用户' }}！</h1>
      <p style="color: #909399; margin-top: 8px;">今天是 {{ today }}</p>
    </div>

    <div class="stat-cards">
      <div class="stat-card"><div class="stat-icon blue">👨‍🎓</div><div class="stat-info"><div class="stat-value">{{ stats.studentCount }}</div><div class="stat-label">学生总数</div></div></div>
      <div class="stat-card"><div class="stat-icon green">📚</div><div class="stat-info"><div class="stat-value">{{ stats.courseCount }}</div><div class="stat-label">课程总数</div></div></div>
      <div class="stat-card"><div class="stat-icon orange">📝</div><div class="stat-info"><div class="stat-value">{{ stats.examCount }}</div><div class="stat-label">考试总数</div></div></div>
      <div class="stat-card"><div class="stat-icon purple">📊</div><div class="stat-info"><div class="stat-value">{{ stats.avgScore }}</div><div class="stat-label">平均分</div></div></div>
    </div>

    <div class="panel">
      <div class="panel-header"><h3>📋 近期考试</h3></div>
      <div class="panel-body">
        <div v-if="stats.recentExams && stats.recentExams.length === 0" style="text-align:center;padding:40px 0;color:#909399;">暂无考试安排</div>
        <div v-else>
          <div v-for="exam in stats.recentExams" :key="exam.id" class="exam-item">
            <div class="exam-date">{{ exam.examDate }}</div>
            <div class="exam-info"><div class="exam-name">{{ exam.examName }}</div><div class="exam-course">{{ exam.courseName || '未知课程' }}</div></div>
            <span class="tag" :class="getStatusTag(exam.status)">{{ getStatusLabel(exam.status) }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getDashboardStats } from '@/api/dashboard'

const userInfo = ref(null)
const stats = ref({
  studentCount: 0,
  courseCount: 0,
  examCount: 0,
  avgScore: 0,
  recentExams: []
})
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
      avgScore: data.avgScore || 0,
      recentExams: data.recentExams || []
    }
  } catch (error) {
    console.error('加载仪表盘数据失败', error)
  }
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
.panel { background: #fff; border-radius: 12px; box-shadow: 0 2px 12px rgba(0,0,0,.08); margin-top: 20px; }
.panel-header { padding: 16px 20px; border-bottom: 1px solid #EBEEF5; }
.panel-header h3 { font-size: 16px; font-weight: 600; }
.panel-body { padding: 20px; }
.exam-item { display: flex; align-items: center; gap: 16px; padding: 12px 0; border-bottom: 1px solid #F5F7FA; }
.exam-item:last-child { border-bottom: none; }
.exam-date { font-size: 13px; color: #909399; min-width: 90px; }
.exam-info { flex: 1; }
.exam-name { font-weight: 500; }
.exam-course { font-size: 13px; color: #909399; }
.tag { display: inline-block; padding: 2px 10px; border-radius: 4px; font-size: 12px; font-weight: 500; }
.tag-blue { background: #ECF5FF; color: #409EFF; }
.tag-green { background: #F0F9EB; color: #67C23A; }
.tag-gray { background: #F4F4F5; color: #909399; }
</style>