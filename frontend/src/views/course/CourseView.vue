<template>
  <div style="padding: 24px;">
    <div class="page-header">
      <h1>📚 课程管理</h1>
      <p style="color: #909399; margin-top: 8px;">管理课程信息</p>
    </div>

    <div class="panel">
      <div class="panel-header">
        <h3>课程列表</h3>
        <button class="btn btn-primary" @click="openForm()">➕ 新增课程</button>
      </div>
      <div class="panel-body">
        <div class="toolbar">
          <input type="text" class="search-input" placeholder="🔍 搜索课程名称或编码" v-model="keyword" @keyup.enter="loadData">
          <button class="btn btn-default" @click="loadData">搜索</button>
          <button class="btn btn-text" @click="resetSearch">重置</button>
        </div>

        <div class="table-wrap">
          <table>
            <thead>
            <tr>
              <th>序号</th>
              <th>课程编码</th>
              <th>课程名称</th>
              <th>学分</th>
              <th>学时</th>
              <th>授课教师</th>
              <th>学期</th>
              <th style="width:140px">操作</th>
            </tr>
            </thead>
            <tbody>
            <tr v-if="loading">
              <td colspan="8" style="text-align:center;color:#909399;">加载中...</td>
            </tr>
            <tr v-else-if="tableData.length === 0">
              <td colspan="8" style="text-align:center;color:#909399;padding:40px 0;">暂无数据</td>
            </tr>
            <tr v-for="(item, index) in tableData" :key="item.id">
              <td>{{ (pageNum - 1) * pageSize + index + 1 }}</td>
              <td><b>{{ item.courseCode }}</b></td>
              <td>{{ item.courseName }}</td>
              <td class="text-center">{{ item.credit }}</td>
              <td class="text-center">{{ item.hours }}</td>
              <td>{{ item.teacherName || '-' }}</td>
              <td>{{ item.semester || '-' }}</td>
              <td>
                <button class="btn btn-text btn-sm" @click="openForm(item)">✏️ 编辑</button>
                <button class="btn btn-text btn-sm" style="color:#F56C6C" @click="handleDelete(item.id, item.courseName)">🗑 删除</button>
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
              <label>课程编码 <span class="required">*</span></label>
              <input type="text" v-model="form.courseCode" placeholder="如 CS001" :readonly="!!form.id" style="background:#F5F7FA;">
            </div>
            <div class="form-item">
              <label>课程名称 <span class="required">*</span></label>
              <input type="text" v-model="form.courseName" placeholder="请输入课程名称">
            </div>
          </div>
          <div class="form-row">
            <div class="form-item">
              <label>学分</label>
              <input type="number" v-model="form.credit" step="0.5" min="0.5" max="10">
            </div>
            <div class="form-item">
              <label>学时</label>
              <input type="number" v-model="form.hours" min="1" max="200">
            </div>
          </div>
          <div class="form-row">
            <div class="form-item">
              <label>授课教师</label>
              <input type="text" v-model="form.teacherName" placeholder="请输入教师姓名">
            </div>
            <div class="form-item">
              <label>学期</label>
              <select v-model="form.semester">
                <option value="">请选择</option>
                <option value="2026-2027-1">2026-2027-1</option>
                <option value="2026-2027-2">2026-2027-2</option>
                <option value="2027-2028-1">2027-2028-1</option>
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
import { getCoursePage, saveOrUpdateCourse, deleteCourse } from '@/api/course'

// ===== 数据 =====
const tableData = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(5)
const keyword = ref('')
const loading = ref(false)

const dialogVisible = ref(false)
const dialogTitle = ref('新增课程')
const submitting = ref(false)
const form = reactive({
  id: null,
  courseCode: '',
  courseName: '',
  credit: 3.0,
  hours: 48,
  teacherName: '',
  semester: ''
})

// ===== 方法 =====
const loadData = async () => {
  loading.value = true
  try {
    const res = await getCoursePage(pageNum.value, pageSize.value, keyword.value)
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
  pageNum.value = 1
  loadData()
}

const openForm = (row = null) => {
  if (row) {
    dialogTitle.value = '编辑课程'
    Object.assign(form, { ...row })
  } else {
    dialogTitle.value = '新增课程'
    Object.assign(form, { id: null, courseCode: '', courseName: '', credit: 3.0, hours: 48, teacherName: '', semester: '' })
  }
  dialogVisible.value = true
}

const closeDialog = () => {
  dialogVisible.value = false
}

const handleSubmit = async () => {
  if (!form.courseCode || !form.courseName) {
    ElMessage.warning('请填写课程编码和名称')
    return
  }
  submitting.value = true
  try {
    await saveOrUpdateCourse(form)
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
  ElMessageBox.confirm(`确定要删除课程「${name}」吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteCourse(id)
      ElMessage.success('删除成功')
      loadData()
    } catch (error) {
      console.error(error)
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

onMounted(() => {
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
.search-input { width: 240px; height: 36px; border: 1px solid #DCDFE6; border-radius: 6px; padding: 0 12px; font-size: 14px; outline: none; }
.search-input:focus { border-color: #409EFF; }
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
.form-item input, .form-item select { height: 36px; border: 1px solid #DCDFE6; border-radius: 6px; padding: 0 12px; font-size: 14px; outline: none; }
.form-item input:focus, .form-item select:focus { border-color: #409EFF; }
</style>
