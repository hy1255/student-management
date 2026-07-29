<template>
  <div style="padding: 24px;">
    <div class="page-header">
      <h1>📈 成绩统计</h1>
      <p style="color: #909399; margin-top: 8px;">考试成绩分析与数据可视化</p>
    </div>

    <div class="panel">
      <div class="panel-body">
        <div style="display: flex; align-items: center; gap: 12px; flex-wrap: wrap;">
          <select v-model="selectedExamId" style="width: 320px; height: 36px; border: 1px solid #DCDFE6; border-radius: 6px; padding: 0 12px; font-size: 14px; outline: none;">
            <option :value="null">-- 请选择考试 --</option>
            <option v-for="exam in examList" :key="exam.id" :value="exam.id">
              {{ exam.examName }} - {{ getCourseName(exam.courseId) }}
            </option>
          </select>
          <button class="btn btn-primary" @click="loadStats" :disabled="!selectedExamId">查看统计</button>
        </div>

        <div v-if="statsData" style="margin-top: 20px;">
          <!-- 统计卡片 -->
          <div style="display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px; margin-bottom: 20px;">
            <div style="background: #F5F7FA; padding: 20px; border-radius: 12px; text-align: center;">
              <div style="font-size: 28px; font-weight: 700; color: #409EFF;">{{ statsData.avgScore }}</div>
              <div style="color: #909399; font-size: 14px;">平均分</div>
            </div>
            <div style="background: #F5F7FA; padding: 20px; border-radius: 12px; text-align: center;">
              <div style="font-size: 28px; font-weight: 700; color: #67C23A;">{{ statsData.maxScore }}</div>
              <div style="color: #909399; font-size: 14px;">最高分</div>
            </div>
            <div style="background: #F5F7FA; padding: 20px; border-radius: 12px; text-align: center;">
              <div style="font-size: 28px; font-weight: 700; color: #F56C6C;">{{ statsData.minScore }}</div>
              <div style="color: #909399; font-size: 14px;">最低分</div>
            </div>
            <div style="background: #F5F7FA; padding: 20px; border-radius: 12px; text-align: center;">
              <div style="font-size: 28px; font-weight: 700; color: #E6A23C;">{{ statsData.passRate }}%</div>
              <div style="color: #909399; font-size: 14px;">及格率</div>
            </div>
          </div>

          <!-- 等级分布 -->
          <div style="background: #fff; border: 1px solid #EBEEF5; border-radius: 12px; padding: 20px;">
            <h4 style="margin-bottom: 16px;">📊 等级分布</h4>
            <div style="display: flex; gap: 16px; flex-wrap: wrap;">
              <div v-for="(count, grade) in statsData.gradeDistribution" :key="grade"
                   style="display: flex; align-items: center; gap: 8px; background: #F5F7FA; padding: 8px 16px; border-radius: 6px;">
                <span class="tag" :class="getGradeTag(grade)">{{ grade }}</span>
                <span style="font-weight: 600;">{{ count }}</span>
                <span style="color: #909399; font-size: 13px;">人</span>
              </div>
            </div>
          </div>
        </div>

        <div v-else-if="selectedExamId && loaded" style="text-align: center; padding: 40px 0; color: #909399;">
          <div style="font-size: 48px; margin-bottom: 12px;">📭</div>
          <p>该考试暂无成绩数据</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getExamPage } from '@/api/exam'
import { getCoursePage } from '@/api/course'
import { getExamStats } from '@/api/score'

const examList = ref([])
const courseList = ref([])
const selectedExamId = ref(null)
const statsData = ref(null)
const loaded = ref(false)

const loadExams = async () => {
  try {
    const res = await getExamPage(1, 100)
    examList.value = res.data.records || []
  } catch (error) {
    console.error('加载考试失败', error)
  }
}

const loadCourses = async () => {
  try {
    const res = await getCoursePage(1, 100)
    courseList.value = res.data.records || []
  } catch (error) {
    console.error('加载课程失败', error)
  }
}

const getCourseName = (id) => {
  const c = courseList.value.find(item => item.id === id)
  return c ? c.courseName : '-'
}

const getGradeTag = (grade) => {
  const map = { 'A': 'tag-green', 'B': 'tag-blue', 'C': 'tag-orange', 'D': 'tag-red', 'F': 'tag-red' }
  return map[grade] || 'tag-gray'
}

const loadStats = async () => {
  if (!selectedExamId.value) {
    ElMessage.warning('请先选择考试')
    return
  }
  loaded.value = false
  try {
    const res = await getExamStats(selectedExamId.value)
    statsData.value = res.data
    loaded.value = true
  } catch (error) {
    console.error('加载统计数据失败', error)
    ElMessage.error('加载统计数据失败')
  }
}

onMounted(() => {
  loadExams()
  loadCourses()
})
</script>

<style scoped>
.page-header h1 { font-size: 24px; font-weight: 600; }
.panel { background: #fff; border-radius: 12px; box-shadow: 0 2px 12px rgba(0,0,0,.08); margin-top: 16px; }
.panel-body { padding: 20px; }
.btn { display: inline-flex; align-items: center; justify-content: center; gap: 6px; height: 36px; padding: 0 16px; border-radius: 6px; font-size: 14px; font-weight: 500; cursor: pointer; border: none; transition: all .2s; }
.btn-primary { background: #409EFF; color: #fff; }
.btn-primary:hover { background: #337ECC; }
.btn-primary:disabled { opacity: .6; cursor: not-allowed; }
.tag { display: inline-block; padding: 2px 10px; border-radius: 4px; font-size: 12px; font-weight: 500; }
.tag-green { background: #F0F9EB; color: #67C23A; }
.tag-blue { background: #ECF5FF; color: #409EFF; }
.tag-orange { background: #FDF6EC; color: #E6A23C; }
.tag-red { background: #FEF0F0; color: #F56C6C; }
</style>