<template>
  <div style="padding: 24px;">
    <div class="page-header">
      <h1>📋 成绩列表</h1>
      <p style="color: #909399; margin-top: 8px;">查看、筛选和修改成绩记录</p>
    </div>

    <div class="panel">
      <div class="panel-header">
        <h3>成绩列表</h3>
      </div>
      <div class="panel-body">
        <div class="toolbar">
          <select v-model="filters.studentId" style="width: 160px; height: 36px; border: 1px solid #DCDFE6; border-radius: 6px; padding: 0 12px; font-size: 14px; outline: none;">
            <option :value="null">👨‍🎓 全部学生</option>
            <option v-for="s in studentList" :key="s.id" :value="s.id">{{ s.name }}</option>
          </select>
          <select v-model="filters.courseId" style="width: 160px; height: 36px; border: 1px solid #DCDFE6; border-radius: 6px; padding: 0 12px; font-size: 14px; outline: none;">
            <option :value="null">📚 全部课程</option>
            <option v-for="c in courseList" :key="c.id" :value="c.id">{{ c.courseName }}</option>
          </select>
          <select v-model="filters.examId" style="width: 200px; height: 36px; border: 1px solid #DCDFE6; border-radius: 6px; padding: 0 12px; font-size: 14px; outline: none;">
            <option :value="null">📝 全部考试</option>
            <option v-for="e in examList" :key="e.id" :value="e.id">{{ e.examName }}</option>
          </select>
          <button class="btn btn-default" @click="loadData">搜索</button>
          <button class="btn btn-text" @click="resetFilters">重置</button>
        </div>

        <div class="table-wrap">
          <table>
            <thead>
            <tr>
              <th>序号</th>
              <th>学号</th>
              <th>姓名</th>
              <th>课程</th>
              <th>考试</th>
              <th>分数</th>
              <th>等级</th>
              <th>日期</th>
              <th style="width: 100px">操作</th>
            </tr>
            </thead>
            <tbody>
            <tr v-if="loading">
              <td colspan="9" style="text-align:center;color:#909399;">加载中...</td>
            </tr>
            <tr v-else-if="tableData.length === 0">
              <td colspan="9" style="text-align:center;color:#909399;padding:40px 0;">暂无数据</td>
            </tr>
            <tr v-for="(item, index) in tableData" :key="item.id">
              <td>{{ (pageNum - 1) * pageSize + index + 1 }}</td>
              <td>{{ item.studentNo || '-' }}</td>
              <td>{{ item.studentName || '-' }}</td>
              <td>{{ item.courseName || '-' }}</td>
              <td>{{ item.examName || '-' }}</td>
              <td><b>{{ item.score !== null ? item.score : '-' }}</b></td>
              <td><span class="tag" :class="getGradeTag(item.grade)">{{ item.grade || '-' }}</span></td>
              <td class="text-sm">{{ item.createTime ? item.createTime.slice(0,10) : '-' }}</td>
              <td>
                <button class="btn btn-text btn-sm" style="color:#F56C6C" @click="handleDelete(item.id)">🗑</button>
              </td>
            </tr>
            </tbody>
          </table>
        </div>

        <div class="pagination">
          <span class="page-btn" :class="{ disabled: pageNum <= 1 }" @click="pageNum > 1 && changePage(pageNum - 1)">◀</span>
          <span class="page-btn active">{{ pageNum }}</span>
          <span class="page-btn" @click="changePage(pageNum + 1)">▶</span>
          <span class="page-info">共 {{ total }} 条，每页 {{ pageSize }} 条</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getScorePage, deleteScore } from '@/api/score'
import { getCoursePage } from '@/api/course'
import { getExamPage } from '@/api/exam'
import { getStudentPage } from '@/api/student'

const tableData = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(5)
const loading = ref(false)

const courseList = ref([])
const examList = ref([])
const studentList = ref([])

const filters = ref({
  studentId: null,
  courseId: null,
  examId: null
})

const getGradeTag = (grade) => {
  const map = { 'A': 'tag-green', 'B': 'tag-blue', 'C': 'tag-orange', 'D': 'tag-red', 'F': 'tag-red' }
  return map[grade] || 'tag-gray'
}

const loadCourses = async () => {
  try {
    const res = await getCoursePage(1, 100)
    courseList.value = res.data.records || []
  } catch (e) {}
}

const loadExams = async () => {
  try {
    const res = await getExamPage(1, 100)
    examList.value = res.data.records || []
  } catch (e) {}
}

const loadStudents = async () => {
  try {
    const res = await getStudentPage(1, 100)
    studentList.value = res.data.records || []
  } catch (e) {}
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getScorePage(
        pageNum.value,
        pageSize.value,
        filters.value.studentId,
        filters.value.courseId,
        filters.value.examId
    )
    // 补充关联信息
    const records = res.data.records || []
    for (let item of records) {
      const student = studentList.value.find(s => s.id === item.studentId)
      if (student) {
        item.studentNo = student.studentNo
        item.studentName = student.name
      }
      const exam = examList.value.find(e => e.id === item.examId)
      if (exam) {
        item.examName = exam.examName
        const course = courseList.value.find(c => c.id === exam.courseId)
        item.courseName = course ? course.courseName : '-'
      }
    }
    tableData.value = records
    total.value = res.data.total || 0
  } catch (error) {
    console.error(error)
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

const changePage = (page) => {
  pageNum.value = page
  loadData()
}

const resetFilters = () => {
  filters.value = { studentId: null, courseId: null, examId: null }
  pageNum.value = 1
  loadData()
}

const handleDelete = (id) => {
  ElMessageBox.confirm('确定要删除该成绩记录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteScore(id)
      ElMessage.success('删除成功')
      loadData()
    } catch (error) {
      console.error(error)
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

onMounted(() => {
  Promise.all([loadCourses(), loadExams(), loadStudents()]).then(() => {
    loadData()
  })
})
</script>

<style scoped>
.page-header h1 { font-size: 24px; font-weight: 600; }
.panel { background: #fff; border-radius: 12px; box-shadow: 0 2px 12px rgba(0,0,0,.08); margin-top: 16px; }
.panel-header { padding: 16px 20px; border-bottom: 1px solid #EBEEF5; display: flex; align-items: center; justify-content: space-between; }
.panel-header h3 { font-size: 16px; font-weight: 600; }
.panel-body { padding: 20px; }
.toolbar { display: flex; align-items: center; gap: 12px; margin-bottom: 16px; flex-wrap: wrap; }
.btn { display: inline-flex; align-items: center; justify-content: center; gap: 6px; height: 36px; padding: 0 16px; border-radius: 6px; font-size: 14px; font-weight: 500; cursor: pointer; border: none; transition: all .2s; }
.btn-default { background: #fff; color: #606266; border: 1px solid #DCDFE6; }
.btn-default:hover { color: #409EFF; border-color: #409EFF; }
.btn-text { background: transparent; color: #409EFF; }
.btn-text:hover { background: #ECF5FF; }
.btn-sm { height: 30px; padding: 0 12px; font-size: 13px; }
.table-wrap { overflow-x: auto; }
table { width: 100%; border-collapse: collapse; }
table th, table td { padding: 12px 14px; text-align: left; font-size: 14px; border-bottom: 1px solid #EBEEF5; }
table th { background: #FAFBFC; color: #606266; font-weight: 600; font-size: 13px; }
table tbody tr:hover { background: #F5F7FA; }
.text-sm { font-size: 13px; }
.tag { display: inline-block; padding: 2px 10px; border-radius: 4px; font-size: 12px; font-weight: 500; }
.tag-green { background: #F0F9EB; color: #67C23A; }
.tag-blue { background: #ECF5FF; color: #409EFF; }
.tag-orange { background: #FDF6EC; color: #E6A23C; }
.tag-red { background: #FEF0F0; color: #F56C6C; }
.tag-gray { background: #F4F4F5; color: #909399; }
.pagination { display: flex; align-items: center; justify-content: center; gap: 4px; margin-top: 16px; }
.page-btn { width: 32px; height: 32px; border: 1px solid #DCDFE6; border-radius: 6px; display: flex; align-items: center; justify-content: center; cursor: pointer; background: #fff; transition: all .2s; user-select: none; }
.page-btn:hover { color: #409EFF; border-color: #409EFF; }
.page-btn.active { background: #409EFF; color: #fff; border-color: #409EFF; }
.page-btn.disabled { opacity: .4; cursor: not-allowed; }
.page-info { font-size: 13px; color: #909399; margin: 0 8px; }
</style>