<template>
  <div style="padding: 24px;">
    <div class="page-header">
      <h1>✏️ 成绩录入</h1>
      <p style="color: #909399; margin-top: 8px;">选择考试后录入学生成绩，等级自动换算</p>
    </div>

    <div class="panel">
      <div class="panel-header">
        <h3>选择考试</h3>
      </div>
      <div class="panel-body">
        <div style="display: flex; align-items: center; gap: 16px; flex-wrap: wrap;">
          <select v-model="selectedExamId" style="width: 320px; height: 36px; border: 1px solid #DCDFE6; border-radius: 6px; padding: 0 12px; font-size: 14px; outline: none;">
            <option :value="null">-- 请选择考试 --</option>
            <option v-for="exam in examList" :key="exam.id" :value="exam.id">
              {{ exam.examName }} - {{ getCourseName(exam.courseId) }} ({{ exam.examDate }})
            </option>
          </select>
          <button class="btn btn-primary" @click="loadScoreData" :disabled="!selectedExamId">确认选择</button>
        </div>

        <div v-if="scoreData.length > 0" style="margin-top: 20px;">
          <div style="background: #F0F9FF; padding: 12px 16px; border-radius: 6px; margin-bottom: 16px; display: flex; align-items: center; gap: 24px; flex-wrap: wrap;">
            <span>📝 考试：<b>{{ getExamName(selectedExamId) }}</b></span>
            <span>📚 课程：<b>{{ getCourseNameByExam(selectedExamId) }}</b></span>
            <span>📅 日期：<b>{{ getExamDate(selectedExamId) }}</b></span>
            <span>👨‍🎓 学生数：<b>{{ scoreData.length }}</b></span>
          </div>

          <div class="table-wrap">
            <table>
              <thead>
              <tr>
                <th>序号</th>
                <th>学号</th>
                <th>姓名</th>
                <th>班级</th>
                <th>分数</th>
                <th>等级</th>
                <th>评语</th>
              </tr>
              </thead>
              <tbody>
              <tr v-for="(item, index) in scoreData" :key="item.studentId">
                <td>{{ index + 1 }}</td>
                <td>{{ item.studentNo }}</td>
                <td>{{ item.studentName }}</td>
                <td>{{ item.className || '-' }}</td>
                <td>
                  <input type="number" class="score-input" v-model="item.score"
                         min="0" max="100" step="0.5"
                         @input="onScoreChange(item)"
                         :class="{ 'has-score': item.score !== null && item.score !== '' }">
                </td>
                <td>
                    <span class="tag" :class="getGradeTag(item.grade)">
                      {{ item.grade || '-' }}
                    </span>
                </td>
                <td>
                  <input type="text" class="comment-input" v-model="item.comment" placeholder="可选评语">
                </td>
              </tr>
              </tbody>
            </table>
          </div>

          <div style="margin-top: 16px; display: flex; gap: 12px;">
            <button class="btn btn-success btn-lg" @click="handleBatchSave" :disabled="saving">
              {{ saving ? '保存中...' : '💾 批量保存' }}
            </button>
            <button class="btn btn-default" @click="resetData">重置</button>
          </div>
        </div>

        <div v-else-if="selectedExamId && loaded" style="text-align: center; padding: 40px 0; color: #909399;">
          <div style="font-size: 48px; margin-bottom: 12px;">📭</div>
          <p>该考试暂无学生数据</p>
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
import { getScoresByExam, batchSaveScores } from '@/api/score'

const examList = ref([])
const courseList = ref([])
const selectedExamId = ref(null)
const scoreData = ref([])
const saving = ref(false)
const loaded = ref(false)

// 加载考试列表
const loadExams = async () => {
  try {
    const res = await getExamPage(1, 100)
    examList.value = res.data.records || []
  } catch (error) {
    console.error('加载考试失败', error)
  }
}

// 加载课程列表
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

const getExamName = (id) => {
  const e = examList.value.find(item => item.id === id)
  return e ? e.examName : '-'
}

const getExamDate = (id) => {
  const e = examList.value.find(item => item.id === id)
  return e ? e.examDate : '-'
}

const getCourseNameByExam = (examId) => {
  const e = examList.value.find(item => item.id === examId)
  return e ? getCourseName(e.courseId) : '-'
}

// 加载成绩数据
const loadScoreData = async () => {
  if (!selectedExamId.value) {
    ElMessage.warning('请先选择考试')
    return
  }
  loaded.value = false
  try {
    const res = await getScoresByExam(selectedExamId.value)
    scoreData.value = res.data || []
    // 确保每个 item 都有 score 字段
    scoreData.value.forEach(item => {
      if (item.score === null || item.score === undefined) {
        item.score = ''
      }
    })
    loaded.value = true
  } catch (error) {
    console.error('加载成绩数据失败', error)
    ElMessage.error('加载成绩数据失败')
  }
}

// 分数变化时自动计算等级
const onScoreChange = (item) => {
  const val = parseFloat(item.score)
  if (isNaN(val) || val < 0 || val > 100) {
    item.grade = null
    return
  }
  if (val >= 90) item.grade = 'A'
  else if (val >= 80) item.grade = 'B'
  else if (val >= 70) item.grade = 'C'
  else if (val >= 60) item.grade = 'D'
  else item.grade = 'F'
}

const getGradeTag = (grade) => {
  const map = {
    'A': 'tag-green',
    'B': 'tag-blue',
    'C': 'tag-orange',
    'D': 'tag-red',
    'F': 'tag-red'
  }
  return map[grade] || 'tag-gray'
}

// 批量保存
const handleBatchSave = async () => {
  // 构建保存数据
  const saveList = scoreData.value
      .filter(item => item.score !== null && item.score !== '' && item.score !== undefined)
      .map(item => ({
        id: item.scoreId || null,
        studentId: item.studentId,
        examId: selectedExamId.value,
        score: parseFloat(item.score),
        grade: item.grade,
        comment: item.comment || ''
      }))

  if (saveList.length === 0) {
    ElMessage.warning('没有可保存的成绩')
    return
  }

  saving.value = true
  try {
    await batchSaveScores(saveList)
    ElMessage.success(`成功保存 ${saveList.length} 条成绩`)
    // 重新加载数据
    await loadScoreData()
  } catch (error) {
    console.error('保存失败', error)
    ElMessage.error('保存失败')
  } finally {
    saving.value = false
  }
}

// 重置
const resetData = () => {
  selectedExamId.value = null
  scoreData.value = []
  loaded.value = false
}

onMounted(() => {
  loadExams()
  loadCourses()
})
</script>

<style scoped>
.page-header h1 { font-size: 24px; font-weight: 600; }
.panel { background: #fff; border-radius: 12px; box-shadow: 0 2px 12px rgba(0,0,0,.08); margin-top: 16px; }
.panel-header { padding: 16px 20px; border-bottom: 1px solid #EBEEF5; display: flex; align-items: center; justify-content: space-between; }
.panel-header h3 { font-size: 16px; font-weight: 600; }
.panel-body { padding: 20px; }
.btn { display: inline-flex; align-items: center; justify-content: center; gap: 6px; height: 36px; padding: 0 16px; border-radius: 6px; font-size: 14px; font-weight: 500; cursor: pointer; border: none; transition: all .2s; }
.btn-primary { background: #409EFF; color: #fff; }
.btn-primary:hover { background: #337ECC; }
.btn-primary:disabled { opacity: .6; cursor: not-allowed; }
.btn-success { background: #67C23A; color: #fff; }
.btn-success:hover { background: #5DAF34; }
.btn-success:disabled { opacity: .6; cursor: not-allowed; }
.btn-default { background: #fff; color: #606266; border: 1px solid #DCDFE6; }
.btn-default:hover { color: #409EFF; border-color: #409EFF; }
.btn-lg { height: 44px; padding: 0 24px; font-size: 16px; }
.table-wrap { overflow-x: auto; }
table { width: 100%; border-collapse: collapse; }
table th, table td { padding: 12px 14px; text-align: left; font-size: 14px; border-bottom: 1px solid #EBEEF5; }
table th { background: #FAFBFC; color: #606266; font-weight: 600; font-size: 13px; }
table tbody tr:hover { background: #F5F7FA; }
.score-input { width: 80px; height: 32px; text-align: center; border: 1px solid #DCDFE6; border-radius: 4px; font-size: 14px; outline: none; padding: 0 4px; }
.score-input:focus { border-color: #409EFF; }
.score-input.has-score { border-color: #67C23A; background: #F0F9EB; }
.comment-input { width: 120px; height: 32px; border: 1px solid #DCDFE6; border-radius: 4px; font-size: 13px; outline: none; padding: 0 8px; }
.comment-input:focus { border-color: #409EFF; }
.tag { display: inline-block; padding: 2px 10px; border-radius: 4px; font-size: 12px; font-weight: 500; }
.tag-green { background: #F0F9EB; color: #67C23A; }
.tag-blue { background: #ECF5FF; color: #409EFF; }
.tag-orange { background: #FDF6EC; color: #E6A23C; }
.tag-red { background: #FEF0F0; color: #F56C6C; }
.tag-gray { background: #F4F4F5; color: #909399; }
</style>