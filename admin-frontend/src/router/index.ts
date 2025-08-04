import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { useAuthStore } from '@/stores/auth'

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
        meta: { title: '仪表板', icon: 'Monitor' },
      },
      {
        path: 'cards',
        name: 'Cards',
        component: CardList,
        meta: { title: '信用卡管理', icon: 'CreditCard' },
      },
      {
        path: 'banks',
        name: 'Banks',
        component: BankList,
        meta: { title: '银行管理', icon: 'OfficeBuilding' },
      },
      {
        path: 'news',
        name: 'News',
        meta: { title: '新闻管理', icon: 'Document' },
        children: [
          {
            path: '',
            name: 'NewsList',
            component: NewsList,
            meta: { title: '新闻列表' },
          },
          {
            path: 'create',
            name: 'NewsCreate',
            component: () => import('@/views/news/NewsForm.vue'),
            meta: { title: '新增新闻', hideInMenu: true },
          },
          {
            path: 'edit/:id',
            name: 'NewsEdit',
            component: () => import('@/views/news/NewsForm.vue'),
            meta: { title: '编辑新闻', hideInMenu: true },
          },
          {
            path: 'preview/:id',
            name: 'NewsPreview',
            component: () => import('@/views/news/NewsPreview.vue'),
            meta: { title: '新闻预览', hideInMenu: true },
          },
        ],
      },
      {
        path: 'categories',
        name: 'Categories',
        component: CategoryList,
        meta: { title: '分类管理', icon: 'Menu' },
      },
      {
        path: 'users',
        name: 'Users',
        component: UserList,
        meta: { title: '用户管理', icon: 'User', requiresAdmin: true },
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
  console.log('路由守卫触发:', { from: from.path, to: to.path })
  const userStore = useUserStore()
  const authStore = useAuthStore()
  
  console.log('当前用户状态:', { 
    isLoggedIn: userStore.isLoggedIn, 
    hasToken: !!userStore.token, 
    user: userStore.user 
  })
  
  console.log('当前认证状态:', { 
    isLoggedIn: authStore.isLoggedIn, 
    hasToken: !!authStore.token, 
    user: authStore.user 
  })
  
  // 如果已登录用户访问登录页，重定向到仪表板
  if (to.name === 'Login' && userStore.isLoggedIn) {
    console.log('已登录用户访问登录页，重定向到仪表板')
    next('/dashboard')
    return
  }
  
  // 检查是否需要认证
  if (to.meta.requiresAuth !== false) {
    console.log('当前路由需要认证')
    if (!userStore.isLoggedIn) {
      console.log('用户未登录，检查是否有token')
      // 尝试从本地存储恢复用户信息
      if (userStore.token) {
        console.log('发现token，尝试获取用户信息')
        const success = await userStore.getCurrentUser()
        console.log('获取用户信息结果:', success)
        if (!success) {
          console.log('获取用户信息失败，重定向到登录页')
          next('/login')
          return
        }
        console.log('获取用户信息成功，继续访问')
        
        // 同步更新 authStore
        if (userStore.token && userStore.user) {
          console.log('同步更新 authStore')
          authStore.login(userStore.token, userStore.user)
          console.log('authStore 更新完成:', { token: authStore.token, user: authStore.user })
        }
      } else {
        console.log('没有token，重定向到登录页')
        next('/login')
        return
      }
    } else if (!authStore.isLoggedIn && userStore.token && userStore.user) {
      // 确保 authStore 也是最新的
      console.log('userStore 已登录但 authStore 未登录，同步更新 authStore')
      authStore.login(userStore.token, userStore.user)
      console.log('authStore 更新完成:', { token: authStore.token, user: authStore.user })
    }
    
    // 检查是否需要管理员权限
    if (to.meta.requiresAdmin && !userStore.isAdmin) {
      console.log('当前路由需要管理员权限，但用户不是管理员，重定向到仪表板')
      next('/dashboard')
      return
    }
  }
  
  console.log('路由检查通过，继续访问:', to.path)
  next()
})

export default router
