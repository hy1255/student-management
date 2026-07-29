<template>
  <div style="padding: 24px;">
    <div class="page-header">
      <h1>📝 考试管理</h1>
      <p style="color: #909399; margin-top: 8px;">管理考试安排</p>
    </div>

    <div class="panel">
      <div class="panel-header">
        <h3>考试列表</h3>
        <button class="btn btn-primary" @click="openForm()">➕ 新增考试</button>
      </div>
      <div class="panel-body">
        <div class="toolbar">
          <input type="text" class="search-input" placeholder="🔍 搜索考试名称" v-model="keyword" @keyup.enter="loadData">
          <select class="filter-select" v-model="filterCourseId">
            <option :value="null">全部课程</option>
            <option v-for="course in courseList" :key="course.id" :value="course.id">
              {{ course.courseName }}
            </option>
          </select>
          <button class="btn btn-default" @click="loadData">搜索</button>
          <button class="btn btn-text" @click="resetSearch">重置</button>
        </div>

        <div class="table-wrap">
          <table>
            <thead>
            <tr>
              <th>序号</th>
              <th>考试名称</th>
              <th>关联课程</th>
              <th>考试日期</th>
              <th>时间</th>
              <th>地点</th>
              <th>类型</th>
              <th>状态</th>
              <th style="width:140px">操作</th>
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
              <td><b>{{ item.examName }}</b></td>
              <td>{{ getCourseName(item.courseId) }}</td>
              <td>{{ item.examDate }}</td>
              <td>{{ item.examTime || '-' }}</td>
              <td>{{ item.location || '-' }}</td>
              <td><span class="tag" :class="typeTagMap[item.examType]">{{ typeLabelMap[item.examType] }}</span></td>
              <td><span class="tag" :class="statusTagMap[item.status]">{{ statusLabelMap[item.status] }}</span></td>
              <td>
                <button class="btn btn-text btn-sm" @click="openForm(item)">✏️ 编辑</button>
                <button class="btn btn-text btn-sm" style="color:#F56C6C" @click="handleDelete(item.id, item.examName)">🗑 删除</button>
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

    <!-- 新增/编辑弹窗 -->
    <div class="modal-overlay" v-if="dialogVisible" @click.self="closeDialog">
      <div class="modal">
        <div class="modal-header">
          <h3>{{ dialogTitle }}</h3>
          <button class="modal-close" @click="closeDialog">✕</button>
        </div>
        <div class="modal-body">
          <div class="form-row">
            <div class="form-item">
              <label>考试名称 <span class="required">*</span></label>
              <input type="text" v-model="form.examName" placeholder="请输入考试名称">
            </div>
            <div class="form-item">
              <label>关联课程 <span class="required">*</span></label>
              <select v-model="form.courseId">
                <option :value="null">请选择课程</option>
                <option v-for="course in courseList" :key="course.id" :value="course.id">
                  {{ course.courseName }}
                </option>
              </select>
            </div>
          </div>
          <div class="form-row">
            <div class="form-item">
              <label>考试日期 <span class="required">*</span></label>
              <input type="date" v-model="form.examDate">
            </div>
            <div class="form-item">
              <label>考试时间</label>
              <select v-model="form.examTime">
                <option value="">请选择</option>
                <option value="08:00-10:00">08:00-10:00</option>
                <option value="09:00-11:00">09:00-11:00</option>
                <option value="14:00-16:00">14:00-16:00</option>
                <option value="15:00-17:00">15:00-17:00</option>
              </select>
            </div>
          </div>
          <div class="form-row">
            <div class="form-item">
              <label>考试地点</label>
              <input type="text" v-model="form.location" placeholder="如 机房301">
            </div>
            <div class="form-item">
              <label>考试类型 <span class="required">*</span></label>
              <select v-model="form.examType">
                <option value="">请选择</option>
                <option value="MIDTERM">期中</option>
                <option value="FINAL">期末</option>
                <option value="MAKEUP">补考</option>
              </select>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn btn-default" @click="closeDialog">取消</button>
          <button class="btn btn-primary" @click="handleSubmit" :disabled="submitting">{{ submitting ? '保存中...' : '确定' }}</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getExamPage, saveOrUpdateExam, deleteExam } from '@/api/exam'
import { getCoursePage } from '@/api/course'

// ===== 数据 =====
const tableData = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(5)
const keyword = ref('')
const filterCourseId = ref(null)
const loading = ref(false)
const courseList = ref([])

const dialogVisible = ref(false)
const dialogTitle = ref('新增考试')
const submitting = ref(false)
const form = reactive({
  id: null,
  examName: '',
  courseId: null,
  examDate: '',
  examTime: '',
  location: '',
  examType: ''
})

// ===== 标签映射 =====
const typeTagMap = {
  'MIDTERM': 'tag-orange',
  'FINAL': 'tag-red',
  'MAKEUP': 'tag-gray'
}
const typeLabelMap = {
  'MIDTERM': '期中',
  'FINAL': '期末',
  'MAKEUP': '补考'
}
const statusTagMap = {
  'PENDING': 'tag-blue',
  'ONGOING': 'tag-green',
  'FINISHED': 'tag-gray'
}
const statusLabelMap = {
  'PENDING': '待考',
  'ONGOING': '进行中',
  'FINISHED': '已结束'
}

// ===== 方法 =====
const loadCourses = async () => {
  try {
    const res = await getCoursePage(1, 100)
    courseList.value = res.data.records || []
  } catch (error) {
    console.error('加载课程失败', error)
  }
}

const getCourseName = (id) => {
  const course = courseList.value.find(c => c.id === id)
  return course ? course.courseName : '-'
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getExamPage(pageNum.value, pageSize.value, keyword.value, filterCourseId.value)
    tableData.value = res.data.records || []
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

const resetSearch = () => {
  keyword.value = ''
  filterCourseId.value = null
  pageNum.value = 1
  loadData()
}

const openForm = (row = null) => {
  if (row) {
    dialogTitle.value = '编辑考试'
    Object.assign(form, { ...row })
  } else {
    dialogTitle.value = '新增考试'
    Object.assign(form, { id: null, examName: '', courseId: null, examDate: '', examTime: '', location: '', examType: '' })
  }
  dialogVisible.value = true
}

const closeDialog = () => {
  dialogVisible.value = false
}

const handleSubmit = async () => {
  if (!form.examName || !form.courseId || !form.examDate || !form.examType) {
    ElMessage.warning('请填写完整信息')
    return
  }
  submitting.value = true
  try {
    await saveOrUpdateExam(form)
    ElMessage.success('操作成功')
    closeDialog()
    loadData()
  } catch (error) {
    console.error(error)
    ElMessage.error('保存失败')
  } finally {
    submitting.value = false
  }
}

const handleDelete = (id, name) => {
  ElMessageBox.confirm(`确定要删除考试「${name}」吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteExam(id)
      ElMessage.success('删除成功')
      loadData()
    } catch (error) {
      console.error(error)
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

onMounted(() => {
  loadCourses()
  loadData()
})
</script>

<style scoped>
.page-header h1 { font-size: 24px; font-weight: 600; }
.panel { background: #fff; border-radius: 12px; box-shadow: 0 2px 12px rgba(0,0,0,.08); margin-top: 16px; }
.panel-header { padding: 16px 20px; border-bottom: 1px solid #EBEEF5; display: flex; align-items: center; justify-content: space-between; }
.panel-header h3 { font-size: 16px; font-weight: 600; }
.panel-body { padding: 20px; }
.toolbar { display: flex; align-items: center; gap: 12px; margin-bottom: 16px; flex-wrap: wrap; }
.search-input { width: 200px; height: 36px; border: 1px solid #DCDFE6; border-radius: 6px; padding: 0 12px; font-size: 14px; outline: none; }
.filter-select { height: 36px; border: 1px solid #DCDFE6; border-radius: 6px; padding: 0 12px; font-size: 14px; outline: none; background: #fff; }
.search-input:focus, .filter-select:focus { border-color: #409EFF; }
.btn { display: inline-flex; align-items: center; justify-content: center; gap: 6px; height: 36px; padding: 0 16px; border-radius: 6px; font-size: 14px; font-weight: 500; cursor: pointer; border: none; transition: all .2s; }
.btn-primary { background: #409EFF; color: #fff; }
.btn-primary:hover { background: #337ECC; }
.btn-default { background: #fff; color: #606266; border: 1px solid #DCDFE6; }
.btn-default:hover { color: #409EFF; border-color: #409EFF; }
.btn-text { background: transparent; color: #409EFF; }
.btn-text:hover { background: #ECF5FF; }
.btn-sm { height: 30px; padding: 0 12px; font-size: 13px; }
.btn:disabled { opacity: .6; cursor: not-allowed; }
.text-center { text-align: center; }
.table-wrap { overflow-x: auto; }
table { width: 100%; border-collapse: collapse; }
table th, table td { padding: 12px 14px; text-align: left; font-size: 14px; border-bottom: 1px solid #EBEEF5; }
table th { background: #FAFBFC; color: #606266; font-weight: 600; font-size: 13px; }
table tbody tr:hover { background: #F5F7FA; }
.pagination { display: flex; align-items: center; justify-content: center; gap: 4px; margin-top: 16px; }
.page-btn { width: 32px; height: 32px; border: 1px solid #DCDFE6; border-radius: 6px; display: flex; align-items: center; justify-content: center; cursor: pointer; background: #fff; transition: all .2s; user-select: none; }
.page-btn:hover { color: #409EFF; border-color: #409EFF; }
.page-btn.active { background: #409EFF; color: #fff; border-color: #409EFF; }
.page-btn.disabled { opacity: .4; cursor: not-allowed; }
.page-info { font-size: 13px; color: #909399; margin: 0 8px; }
.tag { display: inline-block; padding: 2px 10px; border-radius: 4px; font-size: 12px; font-weight: 500; }
.tag-blue { background: #ECF5FF; color: #409EFF; }
.tag-green { background: #F0F9EB; color: #67C23A; }
.tag-orange { background: #FDF6EC; color: #E6A23C; }
.tag-red { background: #FEF0F0; color: #F56C6C; }
.tag-gray { background: #F4F4F5; color: #909399; }
.modal-overlay { position: fixed; top: 0; left: 0; right: 0; bottom: 0; background: rgba(0,0,0,.45); z-index: 1000; display: flex; align-items: center; justify-content: center; }
.modal { background: #fff; border-radius: 12px; box-shadow: 0 4px 24px rgba(0,0,0,.12); width: 560px; max-height: 85vh; overflow: hidden; }
.modal-header { padding: 16px 20px; border-bottom: 1px solid #EBEEF5; display: flex; align-items: center; justify-content: space-between; }
.modal-header h3 { font-size: 16px; font-weight: 600; }
.modal-close { width: 32px; height: 32px; border: none; background: none; cursor: pointer; font-size: 18px; color: #909399; border-radius: 6px; display: flex; align-items: center; justify-content: center; }
.modal-close:hover { background: #F5F7FA; }
.modal-body { padding: 20px; overflow-y: auto; max-height: 60vh; }
.modal-footer { padding: 12px 20px; border-top: 1px solid #EBEEF5; display: flex; justify-content: flex-end; gap: 8px; }
.form-row { display: grid; grid-template-columns: 1fr 1fr; gap: 16px; margin-bottom: 16px; }
.form-item { display: flex; flex-direction: column; gap: 6px; }
.form-item label { font-size: 13px; color: #606266; font-weight: 500; }
.form-item .required { color: #F56C6C; margin-left: 2px; }
.form-item input, .form-item select { height: 36px; border: 1px solid #DCDFE6; border-radius: 6px; padding: 0 12px; font-size: 14px; outline: none; background: #fff; }
.form-item input:focus, .form-item select:focus { border-color: #409EFF; }
</style>