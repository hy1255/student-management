<template>
  <div class="app-layout">
    <!-- 侧边栏 -->
    <aside class="sidebar">
      <div class="sidebar-logo">
        <div class="logo-icon">🎓</div>
        <span>SIMS</span>
      </div>
      <nav class="sidebar-nav">
        <div class="nav-section">
          <div class="nav-section-title">导航菜单</div>
          <router-link to="/dashboard" class="nav-item" :class="{ active: $route.path === '/dashboard' }">
            <span class="icon">📊</span> 仪表盘
          </router-link>
          <router-link to="/student" class="nav-item" :class="{ active: $route.path === '/student' }">
            <span class="icon">👨‍🎓</span> 学生管理
          </router-link>
          <router-link to="/course" class="nav-item" :class="{ active: $route.path === '/course' }">
            <span class="icon">📚</span> 课程管理
          </router-link>
          <router-link to="/exam" class="nav-item" :class="{ active: $route.path === '/exam' }">
            <span class="icon">📝</span> 考试管理
          </router-link>
        </div>
        <div class="nav-section">
          <div class="nav-section-title">成绩管理</div>
          <router-link to="/score/entry" class="nav-item" :class="{ active: $route.path === '/score/entry' }">
            <span class="icon">✏️</span> 成绩录入
          </router-link>
          <router-link to="/score/list" class="nav-item" :class="{ active: $route.path === '/score/list' }">
            <span class="icon">📋</span> 成绩列表
          </router-link>
          <router-link to="/score/stats" class="nav-item" :class="{ active: $route.path === '/score/stats' }">
            <span class="icon">📈</span> 成绩统计
          </router-link>
        </div>
        <div class="nav-section" style="margin-top: auto; border-top: 1px solid rgba(255,255,255,.08); padding-top: 12px;">
          <div class="nav-item" @click="handleLogout" style="cursor:pointer;">
            <span class="icon">🚪</span> 退出登录
          </div>
        </div>
      </nav>
    </aside>

    <!-- 主内容区 -->
    <div class="main-area">
      <header class="header">
        <div class="header-left">
          <span class="breadcrumb">首页 / <span class="current-page">{{ pageTitle }}</span></span>
        </div>
        <div class="header-right">
          <span style="font-size:13px;color:#909399;">角色：<b style="color:#409EFF;">{{ roleText }}</b></span>
          <div class="header-user">
            <div class="avatar">{{ userInfo?.realName?.charAt(0) || 'U' }}</div>
            <span class="name">{{ userInfo?.realName || '用户' }}</span>
          </div>
        </div>
      </header>
      <div class="content">
        <router-view />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'

const router = useRouter()
const route = useRoute()
const userInfo = ref(null)

const roleMap = {
  'ADMIN': '管理员',
  'TEACHER': '教师',
  'STUDENT': '学生'
}

const pageTitleMap = {
  '/dashboard': '仪表盘',
  '/student': '学生管理',
  '/course': '课程管理',
  '/exam': '考试管理',
  '/score/entry': '成绩录入',
  '/score/list': '成绩列表',
  '/score/stats': '成绩统计'
}

const pageTitle = computed(() => pageTitleMap[route.path] || '页面')
const roleText = computed(() => roleMap[userInfo.value?.role] || userInfo.value?.role || '用户')

const handleLogout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('user')
  router.push('/login')
}

onMounted(() => {
  const user = localStorage.getItem('user')
  if (user) {
    userInfo.value = JSON.parse(user)
  }
})
</script>

<style scoped>
.app-layout {
  display: flex;
  min-height: 100vh;
  background: #F2F3F5;
}
.sidebar {
  width: 220px;
  background: linear-gradient(180deg, #1E293B 0%, #0F172A 100%);
  color: #fff;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  position: fixed;
  top: 0;
  left: 0;
  bottom: 0;
  z-index: 100;
}
.sidebar-logo {
  height: 56px;
  display: flex;
  align-items: center;
  padding: 0 20px;
  border-bottom: 1px solid rgba(255,255,255,.08);
  gap: 10px;
}
.logo-icon {
  width: 34px;
  height: 34px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
}
.sidebar-logo span {
  font-size: 18px;
  font-weight: 600;
}
.sidebar-nav {
  flex: 1;
  padding: 12px 0;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
}
.nav-section {
  padding: 0 16px;
}
.nav-section-title {
  font-size: 12px;
  color: rgba(255,255,255,.3);
  text-transform: uppercase;
  letter-spacing: 1px;
  padding: 12px 4px 6px;
  font-weight: 600;
}
.nav-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 11px 16px;
  margin: 2px 0;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  color: rgba(255,255,255,.65);
  transition: all .2s;
  text-decoration: none;
}
.nav-item:hover {
  color: #fff;
  background: rgba(255,255,255,.06);
}
.nav-item.active {
  color: #fff;
  background: rgba(102,126,234,.25);
  font-weight: 500;
}
.nav-item .icon {
  font-size: 18px;
  width: 20px;
  text-align: center;
  flex-shrink: 0;
}
.main-area {
  flex: 1;
  margin-left: 220px;
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}
.header {
  height: 56px;
  background: #fff;
  border-bottom: 1px solid #EBEEF5;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  flex-shrink: 0;
  position: sticky;
  top: 0;
  z-index: 50;
}
.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}
.breadcrumb {
  font-size: 13px;
  color: #909399;
}
.current-page {
  color: #303133;
}
.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}
.header-user {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 4px 8px;
  border-radius: 6px;
  cursor: pointer;
}
.header-user:hover {
  background: #F5F7FA;
}
.avatar {
  width: 34px;
  height: 34px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea, #764ba2);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-weight: 600;
  font-size: 14px;
}
.name {
  font-size: 14px;
  color: #303133;
  font-weight: 500;
}
.content {
  flex: 1;
  padding: 0;
  background: #F2F3F5;
}
</style>