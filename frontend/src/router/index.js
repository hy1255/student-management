import { createRouter, createWebHistory } from 'vue-router'
import Layout from '@/layout/Layout.vue'

const routes = [
    {
        path: '/login',
        name: 'Login',
        component: () => import('../views/login/LoginView.vue'),
        meta: { requiresAuth: false }
    },
    {
        path: '/',
        component: Layout,
        redirect: '/dashboard',
        children: [
            // ===== 仪表盘 =====
            {
                path: '/dashboard',
                name: 'Dashboard',
                component: () => import('../views/dashboard/DashboardView.vue'),
                meta: { requiresAuth: true }
            },
            // ===== 学生管理 =====
            {
                path: '/student',
                name: 'Student',
                component: () => import('../views/student/StudentView.vue'),
                meta: { requiresAuth: true }
            },
            // ===== 课程管理 =====
            {
                path: '/course',
                name: 'Course',
                component: () => import('../views/course/CourseView.vue'),
                meta: { requiresAuth: true }
            },
            // ===== 考试管理 =====
            {
                path: '/exam',
                name: 'Exam',
                component: () => import('../views/exam/ExamView.vue'),
                meta: { requiresAuth: true }
            },
            // ===== 成绩管理 =====
            {
                path: '/score/entry',
                name: 'ScoreEntry',
                component: () => import('../views/score/ScoreEntry.vue'),
                meta: { requiresAuth: true }
            },
            {
                path: '/score/list',
                name: 'ScoreList',
                component: () => import('../views/score/ScoreList.vue'),
                meta: { requiresAuth: true }
            },
            {
                path: '/score/stats',
                name: 'ScoreStats',
                component: () => import('../views/score/ScoreStats.vue'),
                meta: { requiresAuth: true }
            },
            // ===== 系统管理 =====
            {
                path: '/system/menu',
                name: 'Menu',
                component: () => import('../views/system/MenuView.vue'),
                meta: { requiresAuth: true }
            },
            {
                path: '/system/role',
                name: 'Role',
                component: () => import('../views/system/RoleView.vue'),
                meta: { requiresAuth: true }
            },
            {
                path: '/system/user',
                name: 'User',
                component: () => import('../views/system/UserView.vue'),
                meta: { requiresAuth: true }
            },
            {
                path: '/system/class',
                name: 'Class',
                component: () => import('../views/system/ClassView.vue'),
                meta: { requiresAuth: true }
            },
            {
                path: '/system/log',
                name: 'Log',
                component: () => import('../views/system/LogView.vue'),
                meta: { requiresAuth: true }
            }
        ]
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

// ===== 路由守卫 =====
router.beforeEach((to, from, next) => {
    const token = localStorage.getItem('token')
    if (to.meta.requiresAuth) {
        if (!token) {
            next('/login')
        } else {
            next()
        }
    } else {
        if (token && to.path === '/login') {
            next('/')
        } else {
            next()
        }
    }
})

export default router