<template>
  <div class="login-page">
    <div class="login-card">
      <div class="login-logo">
        <div class="icon">🎓</div>
        <h2>学生信息管理系统</h2>
        <p>Student Information Management System</p>
      </div>

      <div class="form-group">
        <label>用户名</label>
        <span class="input-icon">👤</span>
        <input type="text" v-model="form.username" placeholder="请输入用户名" @keyup.enter="handleLogin">
      </div>

      <div class="form-group">
        <label>密码</label>
        <span class="input-icon">🔒</span>
        <input type="password" v-model="form.password" placeholder="请输入密码" @keyup.enter="handleLogin">
      </div>

      <button class="login-btn" :disabled="loading" @click="handleLogin">
        {{ loading ? '登录中...' : '登 录' }}
      </button>

      <div class="login-error" v-if="error">{{ error }}</div>

      <div class="login-tips">
        <span>预置账号</span>
        <span class="tip-item">admin / 123456 (管理员)</span>
        <span class="tip-item">teacher / 123456 (教师)</span>
        <span class="tip-item">student / 123456 (学生)</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { login } from '@/api/auth'

const router = useRouter()
const loading = ref(false)
const error = ref('')

const form = reactive({
  username: 'admin',
  password: '123456'
})

const handleLogin = async () => {
  if (!form.username || !form.password) {
    error.value = '请输入用户名和密码'
    return
  }

  loading.value = true
  error.value = ''

  try {
    const res = await login(form)
    if (res.data.code === 200) {
      const data = res.data.data
      localStorage.setItem('token', data.token)
      localStorage.setItem('user', JSON.stringify({
        username: data.username,
        realName: data.realName,
        role: data.role
      }))
      router.push('/')
    } else {
      error.value = res.data.message || '登录失败'
    }
  } catch (err) {
    error.value = '网络异常，请稍后重试'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  position: relative;
  overflow: hidden;
}
.login-page::before {
  content: '';
  position: absolute;
  width: 600px;
  height: 600px;
  background: rgba(255,255,255,.05);
  border-radius: 50%;
  top: -200px;
  right: -150px;
}
.login-page::after {
  content: '';
  position: absolute;
  width: 400px;
  height: 400px;
  background: rgba(255,255,255,.05);
  border-radius: 50%;
  bottom: -100px;
  left: -100px;
}
.login-card {
  width: 420px;
  background: #FFFFFF;
  border-radius: 12px;
  padding: 48px 40px 36px;
  box-shadow: 0 20px 60px rgba(0,0,0,.2);
  position: relative;
  z-index: 1;
}
.login-logo {
  text-align: center;
  margin-bottom: 32px;
}
.login-logo .icon {
  width: 56px;
  height: 56px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  border-radius: 14px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  color: #fff;
  margin-bottom: 12px;
}
.login-logo h2 {
  font-size: 20px;
  color: #303133;
  font-weight: 600;
}
.login-logo p {
  font-size: 13px;
  color: #909399;
  margin-top: 4px;
}
.form-group {
  margin-bottom: 20px;
  position: relative;
}
.form-group label {
  display: block;
  font-size: 13px;
  color: #606266;
  margin-bottom: 6px;
  font-weight: 500;
}
.form-group input {
  width: 100%;
  height: 44px;
  border: 1px solid #DCDFE6;
  border-radius: 6px;
  padding: 0 16px 0 40px;
  font-size: 14px;
  color: #303133;
  transition: border-color .2s;
  outline: none;
  background: #F5F7FA;
}
.form-group input:focus {
  border-color: #409EFF;
  background: #FFFFFF;
}
.input-icon {
  position: absolute;
  left: 14px;
  bottom: 12px;
  font-size: 16px;
  color: #C0C4CC;
}
.login-btn {
  width: 100%;
  height: 44px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: #fff;
  border: none;
  border-radius: 6px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: all .2s;
  margin-top: 8px;
}
.login-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 16px rgba(102,126,234,.4);
}
.login-btn:disabled {
  opacity: .6;
  cursor: not-allowed;
  transform: none;
}
.login-error {
  color: #F56C6C;
  font-size: 13px;
  text-align: center;
  margin-top: 16px;
  min-height: 20px;
}
.login-tips {
  margin-top: 20px;
  padding-top: 16px;
  border-top: 1px solid #EBEEF5;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #909399;
}
.tip-item {
  color: #606266;
  font-size: 12px;
}
</style>