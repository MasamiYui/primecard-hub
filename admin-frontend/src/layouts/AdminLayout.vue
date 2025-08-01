<template>
  <a-layout class="admin-layout">
    <!-- 侧边栏 -->
    <a-layout-sider
      v-model:collapsed="appStore.sidebarCollapsed"
      :trigger="null"
      collapsible
      class="sidebar"
      :width="256"
      :collapsed-width="80"
    >
      <div class="logo">
        <img src="/logo.svg" alt="PrimeCard" class="logo-image" />
        <span v-if="!appStore.sidebarCollapsed" class="logo-text">PrimeCard</span>
      </div>
      
      <a-menu
        v-model:selectedKeys="selectedKeys"
        v-model:openKeys="openKeys"
        mode="inline"
        theme="dark"
        class="sidebar-menu"
        @click="handleMenuClick"
      >
        <template v-for="route in menuRoutes" :key="route.name">
          <a-sub-menu
            v-if="route.children && route.children.length > 0"
            :key="route.name"
          >
            <template #icon>
              <component :is="route.meta?.icon" />
            </template>
            <template #title>{{ route.meta?.title }}</template>
            <a-menu-item
              v-for="child in route.children.filter(c => !c.meta?.hideInMenu)"
              :key="child.name"
            >
              {{ child.meta?.title }}
            </a-menu-item>
          </a-sub-menu>
          
          <a-menu-item v-else :key="route.name">
            <template #icon>
              <component :is="route.meta?.icon" />
            </template>
            {{ route.meta?.title }}
          </a-menu-item>
        </template>
      </a-menu>
    </a-layout-sider>

    <a-layout class="main-layout">
      <!-- 顶部导航 -->
      <a-layout-header class="header">
        <div class="header-left">
          <a-button
            type="text"
            class="trigger"
            @click="appStore.toggleSidebar"
          >
            <MenuUnfoldOutlined v-if="appStore.sidebarCollapsed" />
            <MenuFoldOutlined v-else />
          </a-button>
          
          <a-breadcrumb class="breadcrumb">
            <a-breadcrumb-item
              v-for="(crumb, index) in appStore.breadcrumbs"
              :key="index"
            >
              <router-link v-if="crumb.path" :to="crumb.path">
                {{ crumb.title }}
              </router-link>
              <span v-else>{{ crumb.title }}</span>
            </a-breadcrumb-item>
          </a-breadcrumb>
        </div>
        
        <div class="header-right">
          <a-space>
            <a-button type="text" @click="appStore.toggleTheme">
              <SunOutlined v-if="appStore.theme === 'dark'" />
              <MoonOutlined v-else />
            </a-button>
            
            <a-dropdown>
              <a-button type="text" class="user-info">
                <UserOutlined />
                <span class="username">{{ userStore.user?.username }}</span>
                <DownOutlined />
              </a-button>
              <template #overlay>
                <a-menu @click="handleUserMenuClick">
                  <a-menu-item key="profile">
                    <UserOutlined />
                    个人资料
                  </a-menu-item>
                  <a-menu-item key="settings">
                    <SettingOutlined />
                    设置
                  </a-menu-item>
                  <a-menu-divider />
                  <a-menu-item key="logout">
                    <LogoutOutlined />
                    退出登录
                  </a-menu-item>
                </a-menu>
              </template>
            </a-dropdown>
          </a-space>
        </div>
      </a-layout-header>

      <!-- 主要内容区域 -->
      <a-layout-content class="content">
        <div class="content-wrapper">
          <router-view />
        </div>
      </a-layout-content>
    </a-layout>
  </a-layout>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { useAppStore } from '@/stores/app'
import {
  MenuFoldOutlined,
  MenuUnfoldOutlined,
  UserOutlined,
  DownOutlined,
  LogoutOutlined,
  SettingOutlined,
  SunOutlined,
  MoonOutlined,
  DashboardOutlined,
  CreditCardOutlined,
  FileTextOutlined,
  AppstoreOutlined,
  TagsOutlined,
} from '@ant-design/icons-vue'

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
const handleMenuClick = ({ key }: { key: string }) => {
  const targetRoute = findRouteByName(key)
  if (targetRoute) {
    router.push(targetRoute.path)
  }
}

// 处理用户菜单点击
const handleUserMenuClick = ({ key }: { key: string }) => {
  switch (key) {
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