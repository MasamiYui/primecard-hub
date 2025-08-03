<template>
  <el-container class="admin-layout">
    <!-- 侧边栏 -->
    <el-aside
      class="sidebar"
      :width="appStore.sidebarCollapsed ? '80px' : '256px'"
    >
      <div class="logo">
        <img src="/logo.svg" alt="PrimeCard" class="logo-image" />
        <span v-if="!appStore.sidebarCollapsed" class="logo-text">PrimeCard</span>
      </div>
      
      <el-menu
        :default-active="selectedKeys[0]"
        :default-openeds="openKeys"
        :collapse="appStore.sidebarCollapsed"
        class="sidebar-menu"
        background-color="#001529"
        text-color="rgba(255, 255, 255, 0.65)"
        active-text-color="#fff"
        @select="handleMenuSelect"
      >
        <template v-for="route in menuRoutes" :key="route.name">
          <el-sub-menu
            v-if="route.children && route.children.length > 0"
            :index="route.name"
          >
            <template #title>
              <el-icon><component :is="route.meta?.icon" /></el-icon>
              <span>{{ route.meta?.title }}</span>
            </template>
            <el-menu-item
              v-for="child in route.children.filter(c => !c.meta?.hideInMenu)"
              :key="child.name"
              :index="child.name"
            >
              {{ child.meta?.title }}
            </el-menu-item>
          </el-sub-menu>
          
          <el-menu-item v-else :index="route.name">
            <el-icon><component :is="route.meta?.icon" /></el-icon>
            <span>{{ route.meta?.title }}</span>
          </el-menu-item>
        </template>
      </el-menu>
    </el-aside>

    <el-container class="main-layout">
      <!-- 顶部导航 -->
      <el-header class="header">
        <div class="header-left">
          <el-button
            link
            class="trigger"
            @click="appStore.toggleSidebar"
          >
            <el-icon :size="20">
              <Expand v-if="appStore.sidebarCollapsed" />
              <Fold v-else />
            </el-icon>
          </el-button>
          
          <el-breadcrumb class="breadcrumb" separator="/">
            <el-breadcrumb-item
              v-for="(crumb, index) in appStore.breadcrumbs"
              :key="index"
              :to="crumb.path"
            >
              {{ crumb.title }}
            </el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        
        <div class="header-right">
          <el-space>
            <el-button link @click="appStore.toggleTheme">
              <el-icon :size="20">
                <Sunny v-if="appStore.theme === 'dark'" />
                <Moon v-else />
              </el-icon>
            </el-button>
            
            <el-dropdown @command="handleUserMenuClick">
              <el-button link class="user-info">
                <el-icon><User /></el-icon>
                <span class="username">{{ userStore.user?.username }}</span>
                <el-icon><ArrowDown /></el-icon>
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="profile">
                    <el-icon><User /></el-icon>
                    个人资料
                  </el-dropdown-item>
                  <el-dropdown-item command="settings">
                    <el-icon><Setting /></el-icon>
                    设置
                  </el-dropdown-item>
                  <el-dropdown-item divided command="logout">
                    <el-icon><SwitchButton /></el-icon>
                    退出登录
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </el-space>
        </div>
      </el-header>

      <!-- 主要内容区域 -->
      <el-main class="content">
        <div class="content-wrapper">
          <router-view />
        </div>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { useAppStore } from '@/stores/app'
import {
  Fold,
  Expand,
  User,
  ArrowDown,
  SwitchButton,
  Setting,
  Sunny,
  Moon,
  Monitor,
  CreditCard,
  Document,
  Menu as IconMenu,
  Tickets,
} from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const appStore = useAppStore()

const selectedKeys = ref<string[]>([])
const openKeys = ref<string[]>([])

// 菜单路由配置
const menuRoutes = computed(() => {
  const routes = router.getRoutes()
    .find(r => r.path === '/')
    ?.children?.filter(child => 
      child.meta?.title && 
      !child.meta?.hideInMenu &&
      (!child.meta?.requiresAdmin || userStore.isAdmin)
    ) || []
  
  return routes
})

// 处理菜单点击
const handleMenuSelect = (index: string) => {
  const targetRoute = findRouteByName(index)
  if (targetRoute) {
    router.push(targetRoute.path)
  }
}

// 处理用户菜单点击
const handleUserMenuClick = (command: string | number | object) => {
  switch (command) {
    case 'profile':
      router.push('/profile')
      break
    case 'settings':
      // TODO: 实现设置页面
      break
    case 'logout':
      userStore.logout().then(() => {
        router.push('/login')
      })
      break
  }
}

// 根据名称查找路由
const findRouteByName = (name: string): any => {
  const routes = router.getRoutes()
  return routes.find(route => route.name === name)
}

// 更新选中的菜单项
const updateSelectedKeys = () => {
  const currentRoute = route.matched[route.matched.length - 1]
  if (currentRoute) {
    selectedKeys.value = [currentRoute.name as string]
    
    // 如果是子路由，展开父级菜单
    if (route.matched.length > 2) {
      const parentRoute = route.matched[1]
      if (parentRoute) {
        openKeys.value = [parentRoute.name as string]
      }
    }
  }
}

// 更新面包屑
const updateBreadcrumbs = () => {
  const breadcrumbs = route.matched
    .filter(r => r.meta?.title)
    .map(r => ({
      title: r.meta?.title as string,
      path: r.path === '/' ? undefined : r.path,
    }))
  
  appStore.setBreadcrumbs(breadcrumbs)
}

// 监听路由变化
watch(route, () => {
  updateSelectedKeys()
  updateBreadcrumbs()
}, { immediate: true })
</script>

<style scoped>
.admin-layout {
  min-height: 100vh;
  display: flex;
}

.sidebar {
  height: 100vh;
  position: fixed;
  left: 0;
  top: 0;
  bottom: 0;
  z-index: 100;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.1);
  overflow-y: auto;
}

.logo {
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  padding: 0 16px;
  background: rgba(255, 255, 255, 0.1);
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.logo-image {
  width: 32px;
  height: 32px;
}

.logo-text {
  color: white;
  font-size: 18px;
  font-weight: 600;
}

.sidebar-menu {
  border-right: none;
  height: calc(100vh - 64px);
  overflow-y: auto;
}

.main-layout {
  margin-left: 256px;
  transition: margin-left 0.2s;
  width: calc(100% - 256px);
  flex: 1;
}

.admin-layout :deep(.ant-layout-sider-collapsed) + .main-layout {
  margin-left: 80px;
  width: calc(100% - 80px);
}

.header {
  background: white;
  padding: 0 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 99;
  height: 64px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.trigger {
  font-size: 18px;
  line-height: 64px;
  padding: 0 12px;
  cursor: pointer;
  transition: color 0.3s;
}

.trigger:hover {
  color: #1890ff;
}

.breadcrumb {
  margin: 0;
  font-size: 14px;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 0 12px;
}

.username {
  font-weight: 500;
  margin-right: 4px;
}

.content {
  margin: 24px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.content-wrapper {
  padding: 24px;
  min-height: calc(100vh - 112px);
}
</style>