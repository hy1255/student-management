<template>
  <div class="modal-overlay" v-if="visible" @click.self="close">
    <div class="modal wide">
      <div class="modal-header">
        <h3>📚 选课管理 - {{ courseName || '课程' }}</h3>
        <button class="modal-close" @click="close">✕</button>
      </div>
      <div class="modal-body">
        <!-- 已选学生列表 -->
        <div style="margin-bottom:16px;">
          <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:8px;">
            <span style="font-weight:600;">已选学生（{{ selectedStudents.length }}人）</span>
          </div>
          <div style="display:flex;flex-wrap:wrap;gap:8px;padding:8px;background:#F5F7FA;border-radius:6px;min-height:40px;">
            <span v-for="stu in selectedStudents" :key="stu.id" style="display:inline-flex;align-items:center;gap:4px;background:#ECF5FF;padding:4px 12px;border-radius:4px;font-size:13px;">
              {{ stu.name }}（{{ stu.studentNo || stu.id }}）
              <span @click="removeStudent(stu.id)" style="cursor:pointer;color:#F56C6C;margin-left:4px;">✕</span>
            </span>
            <span v-if="selectedStudents.length === 0" style="color:#909399;font-size:13px;">暂无学生</span>
          </div>
        </div>

        <!-- 添加学生 -->
        <div>
          <div style="display:flex;align-items:center;gap:12px;margin-bottom:8px;">
            <span style="font-weight:600;">添加学生</span>
            <input type="text" v-model="searchKeyword" placeholder="搜索姓名或学号" style="flex:1;height:32px;border:1px solid #DCDFE6;border-radius:4px;padding:0 10px;font-size:13px;outline:none;">
            <button class="btn btn-default btn-sm" @click="searchAvailable">搜索</button>
          </div>
          <div style="display:flex;flex-wrap:wrap;gap:8px;padding:8px;background:#FAFBFC;border-radius:6px;min-height:40px;max-height:120px;overflow-y:auto;">
            <span v-for="stu in availableStudents" :key="stu.id" style="display:inline-flex;align-items:center;gap:4px;background:#F0F9EB;padding:4px 12px;border-radius:4px;font-size:13px;cursor:pointer;" @click="addStudent(stu.id)">
              {{ stu.name }}（{{ stu.studentNo || stu.id }}）+
            </span>
            <span v-if="availableStudents.length === 0" style="color:#909399;font-size:13px;">无可用学生</span>
          </div>
        </div>
      </div>
      <div class="modal-footer">
        <button class="btn btn-default" @click="close">关闭</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getCourseStudents, getAvailableStudents, addStudentsToCourse, removeStudentFromCourse } from '@/api/studentCourse'

const props = defineProps({
  visible: Boolean,
  courseId: Number,
  courseName: String
})
const emit = defineEmits(['update:visible', 'success'])

const selectedStudents = ref([])
const availableStudents = ref([])
const searchKeyword = ref('')

const loadSelected = async () => {
  if (!props.courseId) return
  try {
    const res = await getCourseStudents(props.courseId)
    selectedStudents.value = res.data || []
  } catch (e) {
    console.error(e)
  }
}

const loadAvailable = async () => {
  if (!props.courseId) return
  try {
    const res = await getAvailableStudents(props.courseId)
    availableStudents.value = res.data || []
  } catch (e) {
    console.error(e)
  }
}

const searchAvailable = () => {
  loadAvailable()
}

const addStudent = async (studentId) => {
  try {
    await addStudentsToCourse(props.courseId, [studentId])
    ElMessage.success('添加成功')
    loadSelected()
    loadAvailable()
  } catch (e) {
    ElMessage.error('添加失败')
  }
}

const removeStudent = async (studentId) => {
  try {
    await removeStudentFromCourse(props.courseId, studentId)
    ElMessage.success('移除成功')
    loadSelected()
    loadAvailable()
  } catch (e) {
    ElMessage.error('移除失败')
  }
}

const close = () => {
  emit('update:visible', false)
}

watch(() => props.visible, (val) => {
  if (val) {
    loadSelected()
    loadAvailable()
  }
})

onMounted(() => {
  if (props.visible) {
    loadSelected()
    loadAvailable()
  }
})
</script>

<style scoped>
.modal-overlay { position: fixed; top:0; left:0; right:0; bottom:0; background:rgba(0,0,0,.45); z-index:1000; display:flex; align-items:center; justify-content:center; }
.modal { background:#fff; border-radius:12px; box-shadow:0 4px 24px rgba(0,0,0,.12); width:720px; max-height:85vh; overflow:hidden; }
.modal-header { padding:16px 20px; border-bottom:1px solid #EBEEF5; display:flex; align-items:center; justify-content:space-between; }
.modal-header h3 { font-size:16px; font-weight:600; }
.modal-close { width:32px; height:32px; border:none; background:none; cursor:pointer; font-size:18px; color:#909399; border-radius:6px; }
.modal-close:hover { background:#F5F7FA; }
.modal-body { padding:20px; overflow-y:auto; max-height:60vh; }
.modal-footer { padding:12px 20px; border-top:1px solid #EBEEF5; display:flex; justify-content:flex-end; gap:8px; }
.btn { display:inline-flex; align-items:center; justify-content:center; gap:6px; height:36px; padding:0 16px; border-radius:6px; font-size:14px; font-weight:500; cursor:pointer; border:none; transition:all .2s; }
.btn-default { background:#fff; color:#606266; border:1px solid #DCDFE6; }
.btn-default:hover { color:#409EFF; border-color:#409EFF; }
.btn-sm { height:30px; padding:0 12px; font-size:13px; }
</style>