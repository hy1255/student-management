<template>
  <div style="padding: 24px;">
    <div style="display: flex; justify-content: space-between; align-items: center;">
      <div>
        <h1 style="font-size: 24px; font-weight: 600;">📜 操作日志</h1>
        <p style="color: #909399; margin-top: 8px;">查看系统所有操作记录</p>
      </div>
    </div>

    <div style="background:#fff; border-radius:12px; box-shadow:0 2px 12px rgba(0,0,0,.08); margin-top:16px; padding:20px;">
      <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px;">
        <span style="font-weight:600;">日志列表</span>
      </div>

      <div class="table-wrap">
        <table>
          <thead>
          <tr>
            <th>序号</th>
            <th>操作人</th>
            <th>操作模块</th>
            <th>操作类型</th>
            <th>操作描述</th>
            <th>IP地址</th>
            <th>操作时间</th>
          </tr>
          </thead>
          <tbody>
          <tr v-if="loading">
            <td colspan="7" style="text-align:center;color:#909399;padding:20px 0;">加载中...</td>
          </tr>
          <tr v-else-if="tableData.length === 0">
            <td colspan="7" style="text-align:center;color:#909399;padding:40px 0;">
              <div style="font-size:48px;margin-bottom:12px;">📭</div>
              <p>暂无日志数据</p>
            </td>
          </tr>
          <tr v-for="(item, index) in tableData" :key="item.id">
            <td>{{ (pageNum - 1) * pageSize + index + 1 }}</td>
            <td><b>{{ item.username || '-' }}</b></td>
            <td>{{ item.module || '-' }}</td>
            <td><span class="tag" :class="getTag(item.operationType)">{{ getLabel(item.operationType) }}</span></td>
            <td>{{ item.description || '-' }}</td>
            <td>{{ item.ipAddress || '-' }}</td>
            <td>{{ item.operateTime || '-' }}</td>
          </tr>
          </tbody>
        </table>
      </div>

      <div style="display:flex;align-items:center;justify-content:center;gap:8px;margin-top:16px;padding-top:16px;">
        <span style="font-size:13px;color:#909399;">共 {{ total }} 条，每页 10 条</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getLogPage } from '@/api/log'

const tableData = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const loading = ref(false)

const getTag = (type) => {
  const map = { 'ADD': 'tag-green', 'UPDATE': 'tag-blue', 'DELETE': 'tag-red', 'LOGIN': 'tag-gray' }
  return map[type] || 'tag-gray'
}
const getLabel = (type) => {
  const map = { 'ADD': '新增', 'UPDATE': '修改', 'DELETE': '删除', 'LOGIN': '登录' }
  return map[type] || type || '其他'
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getLogPage(pageNum.value, pageSize.value)
    tableData.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (error) {
    console.error('加载日志失败', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.table-wrap { overflow-x: auto; }
table { width: 100%; border-collapse: collapse; }
table th, table td { padding: 12px 14px; text-align: left; font-size: 14px; border-bottom: 1px solid #EBEEF5; }
table th { background: #FAFBFC; color: #606266; font-weight: 600; font-size: 13px; }
.tag { display: inline-block; padding: 2px 10px; border-radius: 4px; font-size: 12px; font-weight: 500; }
.tag-green { background: #F0F9EB; color: #67C23A; }
.tag-blue { background: #ECF5FF; color: #409EFF; }
.tag-red { background: #FEF0F0; color: #F56C6C; }
.tag-gray { background: #F4F4F5; color: #909399; }
</style>