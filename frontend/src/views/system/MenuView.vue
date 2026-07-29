<template>
  <div style="padding: 24px;">
    <div class="page-header">
      <h1>📁 菜单管理</h1>
      <p style="color: #909399; margin-top: 8px;">管理系统菜单结构和按钮权限标识</p>
    </div>

    <div class="panel">
      <div class="panel-header">
        <h3>菜单列表</h3>
        <button class="btn btn-primary" @click="openForm()">➕ 新增菜单</button>
      </div>
      <div class="panel-body">
        <div class="table-wrap">
          <table>
            <thead>
            <tr>
              <th style="width:50px">序号</th>
              <th>菜单名称</th>
              <th>图标</th>
              <th>路由路径</th>
              <th>类型</th>
              <th style="width:60px">排序</th>
              <th>权限标识</th>
              <th style="width:150px">操作</th>
            </tr>
            </thead>
            <tbody>
            <tr v-if="loading">
              <td colspan="8" style="text-align:center;color:#909399;">加载中...</td>
            </tr>
            <tr v-else-if="tableData.length === 0">
              <td colspan="8" style="text-align:center;color:#909399;padding:40px 0;">暂无数据</td>
            </tr>
            <template v-for="item in tableData" :key="item.id">
              <tr>
                <td>{{ item.sortOrder }}</td>
                <td><span v-if="item.menuType === 'DIR'">📁</span> <b>{{ item.menuName }}</b></td>
                <td>{{ item.icon || '-' }}</td>
                <td>{{ item.routePath || '-' }}</td>
                <td><span class="tag" :class="typeTagMap[item.menuType]">{{ typeLabelMap[item.menuType] }}</span></td>
                <td class="text-center">{{ item.sortOrder }}</td>
                <td class="text-sm">{{ item.permission || '-' }}</td>
                <td>
                  <button class="btn btn-text btn-sm" @click="openForm(item)">✏️ 编辑</button>
                  <button class="btn btn-text btn-sm" style="color:#F56C6C" @click="handleDelete(item.id, item.menuName)">🗑 删除</button>
                </td>
              </tr>
              <!-- 子菜单 -->
              <template v-for="child in item.children" :key="child.id">
                <tr style="background:#FAFBFC;">
                  <td></td>
                  <td style="padding-left:40px;">
                    <span v-if="child.menuType === 'MENU'">📄</span>
                    <span v-else-if="child.menuType === 'BUTTON'">🔘</span>
                    {{ child.menuName }}
                  </td>
                  <td>{{ child.icon || '-' }}</td>
                  <td>{{ child.routePath || '-' }}</td>
                  <td><span class="tag" :class="typeTagMap[child.menuType]">{{ typeLabelMap[child.menuType] }}</span></td>
                  <td class="text-center">{{ child.sortOrder }}</td>
                  <td class="text-sm"><code style="font-size:11px;">{{ child.permission || '-' }}</code></td>
                  <td>
                    <button class="btn btn-text btn-sm" @click="openForm(child)">✏️ 编辑</button>
                    <button class="btn btn-text btn-sm" style="color:#F56C6C" @click="handleDelete(child.id, child.menuName)">🗑 删除</button>
                  </td>
                </tr>
              </template>
            </template>
            </tbody>
          </table>
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
          <div class="form-row single">
            <div class="form-item">
              <label>上级菜单</label>
              <select v-model="form.parentId">
                <option :value="0">📁 根目录</option>
                <option v-for="item in menuOptions" :key="item.id" :value="item.id">
                  {{ item.menuName }}
                </option>
              </select>
            </div>
          </div>
          <div class="form-row">
            <div class="form-item">
              <label>菜单类型 <span class="required">*</span></label>
              <select v-model="form.menuType">
                <option value="DIR">📁 目录</option>
                <option value="MENU">📄 菜单</option>
                <option value="BUTTON">🔘 按钮</option>
              </select>
            </div>
            <div class="form-item">
              <label>菜单名称 <span class="required">*</span></label>
              <input type="text" v-model="form.menuName" placeholder="请输入菜单名称">
            </div>
          </div>
          <div class="form-row">
            <div class="form-item">
              <label>图标（Emoji）</label>
              <input type="text" v-model="form.icon" placeholder="如 📊">
            </div>
            <div class="form-item">
              <label>排序</label>
              <input type="number" v-model="form.sortOrder" min="0">
            </div>
          </div>
          <div class="form-row">
            <div class="form-item">
              <label>路由路径</label>
              <input type="text" v-model="form.routePath" placeholder="菜单类型为 MENU 时必填">
            </div>
            <div class="form-item">
              <label>权限标识</label>
              <input type="text" v-model="form.permission" placeholder="按钮类型时必填 如 system:user:add">
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
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getMenuTree, saveOrUpdateMenu, deleteMenu } from '@/api/menu'

const tableData = ref([])
const loading = ref(false)

const dialogVisible = ref(false)
const dialogTitle = ref('新增菜单')
const submitting = ref(false)

const form = reactive({
  id: null,
  parentId: 0,
  menuName: '',
  menuType: 'MENU',
  routePath: '',
  icon: '',
  permission: '',
  sortOrder: 0
})

const typeTagMap = {
  'DIR': 'tag-blue',
  'MENU': 'tag-green',
  'BUTTON': 'tag-orange'
}
const typeLabelMap = {
  'DIR': '目录',
  'MENU': '菜单',
  'BUTTON': '按钮'
}

// 菜单选项（用于上级菜单下拉）
const menuOptions = computed(() => {
  const options = []
  const flatten = (items, level = 0) => {
    for (const item of items) {
      options.push({ id: item.id, menuName: '　'.repeat(level) + item.menuName })
      if (item.children && item.children.length > 0) {
        flatten(item.children, level + 1)
      }
    }
  }
  flatten(tableData.value)
  return options
})

const loadData = async () => {
  loading.value = true
  try {
    const res = await getMenuTree()
    tableData.value = res.data || []
  } catch (error) {
    console.error(error)
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

const openForm = (row = null) => {
  if (row) {
    dialogTitle.value = '编辑菜单'
    Object.assign(form, { ...row })
  } else {
    dialogTitle.value = '新增菜单'
    Object.assign(form, { id: null, parentId: 0, menuName: '', menuType: 'MENU', routePath: '', icon: '', permission: '', sortOrder: 0 })
  }
  dialogVisible.value = true
}

const closeDialog = () => {
  dialogVisible.value = false
}

const handleSubmit = async () => {
  if (!form.menuName || !form.menuType) {
    ElMessage.warning('请填写菜单名称和类型')
    return
  }
  if (form.menuType === 'MENU' && !form.routePath) {
    ElMessage.warning('菜单类型为"菜单"时，路由路径不能为空')
    return
  }
  if (form.menuType === 'BUTTON' && !form.permission) {
    ElMessage.warning('菜单类型为"按钮"时，权限标识不能为空')
    return
  }
  submitting.value = true
  try {
    await saveOrUpdateMenu(form)
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
  ElMessageBox.confirm(`确定要删除菜单「${name}」吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteMenu(id)
      ElMessage.success('删除成功')
      loadData()
    } catch (error) {
      console.error(error)
      ElMessage.error('删除失败，该菜单下可能有子菜单')
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
.table-wrap { overflow-x: auto; }
table { width: 100%; border-collapse: collapse; }
table th, table td { padding: 12px 14px; text-align: left; font-size: 14px; border-bottom: 1px solid #EBEEF5; }
table th { background: #FAFBFC; color: #606266; font-weight: 600; font-size: 13px; }
table tbody tr:hover { background: #F5F7FA; }
.btn { display: inline-flex; align-items: center; justify-content: center; gap: 6px; height: 36px; padding: 0 16px; border-radius: 6px; font-size: 14px; font-weight: 500; cursor: pointer; border: none; transition: all .2s; }
.btn-primary { background: #409EFF; color: #fff; }
.btn-primary:hover { background: #337ECC; }
.btn-primary:disabled { opacity: .6; cursor: not-allowed; }
.btn-text { background: transparent; color: #409EFF; }
.btn-text:hover { background: #ECF5FF; }
.btn-sm { height: 30px; padding: 0 12px; font-size: 13px; }
.tag { display: inline-block; padding: 2px 10px; border-radius: 4px; font-size: 12px; font-weight: 500; }
.tag-blue { background: #ECF5FF; color: #409EFF; }
.tag-green { background: #F0F9EB; color: #67C23A; }
.tag-orange { background: #FDF6EC; color: #E6A23C; }
.text-center { text-align: center; }
.text-sm { font-size: 13px; }
.modal-overlay { position: fixed; top: 0; left: 0; right: 0; bottom: 0; background: rgba(0,0,0,.45); z-index: 1000; display: flex; align-items: center; justify-content: center; }
.modal { background: #fff; border-radius: 12px; box-shadow: 0 4px 24px rgba(0,0,0,.12); width: 560px; max-height: 85vh; overflow: hidden; }
.modal-header { padding: 16px 20px; border-bottom: 1px solid #EBEEF5; display: flex; align-items: center; justify-content: space-between; }
.modal-header h3 { font-size: 16px; font-weight: 600; }
.modal-close { width: 32px; height: 32px; border: none; background: none; cursor: pointer; font-size: 18px; color: #909399; border-radius: 6px; display: flex; align-items: center; justify-content: center; }
.modal-close:hover { background: #F5F7FA; }
.modal-body { padding: 20px; overflow-y: auto; max-height: 60vh; }
.modal-footer { padding: 12px 20px; border-top: 1px solid #EBEEF5; display: flex; justify-content: flex-end; gap: 8px; }
.form-row { display: grid; grid-template-columns: 1fr 1fr; gap: 16px; margin-bottom: 16px; }
.form-row.single { grid-template-columns: 1fr; }
.form-item { display: flex; flex-direction: column; gap: 6px; }
.form-item label { font-size: 13px; color: #606266; font-weight: 500; }
.form-item .required { color: #F56C6C; margin-left: 2px; }
.form-item input, .form-item select { height: 36px; border: 1px solid #DCDFE6; border-radius: 6px; padding: 0 12px; font-size: 14px; outline: none; background: #fff; }
.form-item input:focus, .form-item select:focus { border-color: #409EFF; }
</style>