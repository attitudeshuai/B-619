import { createRouter, createWebHistory } from 'vue-router'

const routes = [
    {
        path: '/login',
        name: 'Login',
        component: () => import('../pages/Login.vue'),
        meta: { title: '登录 - 酒店管理系统', requiresAuth: false }
    },
    {
        path: '/',
        component: () => import('../pages/Layout.vue'),
        redirect: '/dashboard',
        meta: { requiresAuth: true },
        children: [
            {
                path: 'dashboard',
                name: 'Dashboard',
                component: () => import('../pages/Dashboard.vue'),
                meta: { title: '仪表盘 - 酒店管理系统', requiresAuth: true }
            },
            {
                path: 'rooms',
                name: 'Rooms',
                component: () => import('../pages/RoomList.vue'),
                meta: { title: '客房管理 - 酒店管理系统', requiresAuth: true }
            },
            {
                path: 'bookings',
                name: 'Bookings',
                component: () => import('../pages/BookingList.vue'),
                meta: { title: '预订管理 - 酒店管理系统', requiresAuth: true }
            },
            {
                path: 'checkins',
                name: 'CheckIns',
                component: () => import('../pages/CheckInList.vue'),
                meta: { title: '入住登记 - 酒店管理系统', requiresAuth: true }
            },
            {
                path: 'members',
                name: 'Members',
                component: () => import('../pages/MemberList.vue'),
                meta: { title: '会员管理 - 酒店管理系统', requiresAuth: true }
            },
            {
                path: 'reports',
                name: 'Reports',
                component: () => import('../pages/ReportDashboard.vue'),
                meta: { title: '财务报表 - 酒店管理系统', requiresAuth: true }
            },
            {
                path: 'settings',
                name: 'Settings',
                component: () => import('../pages/Settings.vue'),
                meta: { title: '系统设置 - 酒店管理系统', requiresAuth: true }
            },
            {
                path: 'admins',
                name: 'Admins',
                component: () => import('../pages/AdminList.vue'),
                meta: { title: '管理员管理 - 酒店管理系统', requiresAuth: true }
            }
        ]
    },
    {
        path: '/:pathMatch(.*)*',
        redirect: '/login'
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
    // 设置页面标题
    document.title = to.meta.title || '酒店管理系统'

    const token = localStorage.getItem('token')

    if (to.meta.requiresAuth && !token) {
        // 需要认证但未登录，跳转登录页
        next({ name: 'Login', query: { redirect: to.fullPath } })
    } else if (to.name === 'Login' && token) {
        // 已登录访问登录页，跳转仪表盘
        next({ name: 'Dashboard' })
    } else {
        next()
    }
})

export default router
