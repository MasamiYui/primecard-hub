import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'
import { useUserStore } from '@/stores/user'

// 布局组件
const AdminLayout = () => import('@/layouts/AdminLayout.vue')
const AuthLayout = () => import('@/layouts/AuthLayout.vue')

// 页面组件
const Login = () => import('@/views/auth/Login.vue')
const Dashboard = () => import('@/views/dashboard/Dashboard.vue')
const CardList = () => import('@/views/card/CardList.vue')
const BankList = () => import('@/views/bank/BankList.vue')
const NewsList = () => import('@/views/news/NewsList.vue')
const CategoryList = () => import('@/views/category/CategoryList.vue')
const UserList = () => import('@/views/user/UserList.vue')
const Profile = () => import('@/views/profile/Profile.vue')

const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    component: AuthLayout,
    children: [
      {
        path: '',
        name: 'Login',
        component: Login,
        meta: { title: '登录', requiresAuth: false },
      },
    ],
  },
  {
    path: '/',
    component: AdminLayout,
    redirect: '/dashboard',
    meta: { requiresAuth: true },
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: Dashboard,
        meta: { title: '仪表板', icon: 'DashboardOutlined' },
      },
      {
        path: 'cards',
        name: 'Cards',
        component: CardList,
        meta: { title: '信用卡管理', icon: 'CreditCardOutlined' },
      },
      {
        path: 'banks',
        name: 'Banks',
        component: BankList,
        meta: { title: '银行管理', icon: 'BankOutlined' },
      },
      {
        path: 'news',
        name: 'News',
        component: NewsList,
        meta: { title: '新闻管理', icon: 'FileTextOutlined' },
      },
      {
        path: 'categories',
        name: 'Categories',
        component: CategoryList,
        meta: { title: '分类管理', icon: 'AppstoreOutlined' },
      },
      {
        path: 'users',
        name: 'Users',
        component: UserList,
        meta: { title: '用户管理', icon: 'UserOutlined', requiresAdmin: true },
      },
      {
        path: 'profile',
        name: 'Profile',
        component: Profile,
        meta: { title: '个人资料', hideInMenu: true },
      },
    ],
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/dashboard',
  },
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
})

// 路由守卫
router.beforeEach(async (to, from, next) => {
  const userStore = useUserStore()
  
  // 检查是否需要认证
  if (to.meta.requiresAuth !== false) {
    if (!userStore.isLoggedIn) {
      // 尝试从本地存储恢复用户信息
      if (userStore.token) {
        const success = await userStore.getCurrentUser()
        if (!success) {
          next('/login')
          return
        }
      } else {
        next('/login')
        return
      }
    }
    
    // 检查是否需要管理员权限
    if (to.meta.requiresAdmin && !userStore.isAdmin) {
      next('/dashboard')
      return
    }
  }
  
  // 如果已登录用户访问登录页，重定向到仪表板
  if (to.name === 'Login' && userStore.isLoggedIn) {
    next('/dashboard')
    return
  }
  
  next()
})

export default router
