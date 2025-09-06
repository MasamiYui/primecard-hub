<template>
  <div class="banner-list">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">轮播图管理</h1>
        <div class="header-actions">
          <el-button type="success" @click="goToRelationManage">
            <el-icon><Link /></el-icon>
            关联关系管理
          </el-button>
          <el-button type="primary" @click="handleCreate">
            <el-icon><Plus /></el-icon>
            新增轮播图
          </el-button>
        </div>
      </div>
    </div>

    <!-- 搜索区域 -->
    <div class="search-bar">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="关键词">
          <el-input v-model="searchForm.keyword" placeholder="标题/图片URL" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="全部" clearable>
            <el-option label="活跃" :value="BannerStatus.ACTIVE" />
            <el-option label="非活跃" :value="BannerStatus.INACTIVE" />
          </el-select>
        </el-form-item>
        <el-form-item label="链接类型">
          <el-select v-model="searchForm.linkType" placeholder="全部" clearable>
            <el-option label="无链接" :value="BannerLinkType.NONE" />
            <el-option label="信用卡" :value="BannerLinkType.CARD" />
            <el-option label="资讯" :value="BannerLinkType.NEWS" />
            <el-option label="外部链接" :value="BannerLinkType.EXTERNAL" />
            <el-option label="小程序" :value="BannerLinkType.MINIPROGRAM" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 表格区域 -->
    <div class="table-container">
      <el-table
        v-loading="loading"
        :data="bannerList"
        border
        style="width: 100%"
        row-key="id"
        @sort-change="handleSortChange"
      >
        <el-table-column label="排序" width="80" align="center">
          <template #default="{ row, $index }">
            <div class="sort-buttons">
              <el-button
                v-if="$index > 0"
                type="primary"
                size="small"
                circle
                @click="moveUp($index)"
                class="sort-button-up"
              >
                <el-icon><ArrowUp /></el-icon>
              </el-button>
              <el-button
                v-if="$index < bannerList.length - 1"
                type="primary"
                size="small"
                circle
                @click="moveDown($index)"
                class="sort-button-down"
              >
                <el-icon><ArrowDown /></el-icon>
              </el-button>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="标题" min-width="120" />
        <el-table-column label="图片" width="120" align="center">
          <template #default="{ row }">
            <el-image
              :src="row.imageUrl"
              :preview-src-list="[row.imageUrl]"
              fit="cover"
              style="width: 100px; height: 50px"
            />
          </template>
        </el-table-column>
        <el-table-column prop="linkType" label="链接类型" width="120">
          <template #default="{ row }">
            <el-tag :type="getLinkTypeTag(row.linkType)">
              {{ getLinkTypeLabel(row.linkType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="链接目标" min-width="200">
          <template #default="{ row }">
            <div class="link-target-cell">
              <div class="link-type-tag">
                <el-tag :type="getLinkTypeTag(row.linkType)" size="small">
                  {{ getLinkTypeLabel(row.linkType) }}
                </el-tag>
              </div>
              <div class="link-info" v-if="row.linkType !== BannerLinkType.NONE">
                <span v-if="row.linkType === BannerLinkType.EXTERNAL">
                  <el-icon><Link /></el-icon>
                  <el-link :href="row.linkUrl" target="_blank" type="primary" style="margin-left: 4px;">
                    {{ row.linkUrl && row.linkUrl.length > 25 ? row.linkUrl.substring(0, 25) + '...' : row.linkUrl }}
                  </el-link>
                </span>
                <span v-else-if="row.linkType === BannerLinkType.CARD">
                  <el-icon><CreditCard /></el-icon>
                  <span style="margin-left: 4px;">ID: {{ row.linkId }}</span>
                </span>
                <span v-else-if="row.linkType === BannerLinkType.NEWS">
                  <el-icon><Document /></el-icon>
                  <span style="margin-left: 4px;">ID: {{ row.linkId }}</span>
                </span>
                <span v-else-if="row.linkType === BannerLinkType.MINIPROGRAM">
                  <el-icon><Cellphone /></el-icon>
                  <div style="margin-left: 4px; font-size: 12px;">
                    <div>AppID: {{ row.linkAppid }}</div>
                    <div>Path: {{ row.linkPage }}</div>
                  </div>
                </span>
              </div>
              <span v-else class="no-link">无链接</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="展示时间" width="240">
          <template #default="{ row }">
            <div v-if="row.startTime || row.endTime">
              {{ row.startTime ? formatDateTime(row.startTime) : '不限' }} 至
              {{ row.endTime ? formatDateTime(row.endTime) : '不限' }}
            </div>
            <span v-else>不限制</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-switch
              v-model="row.status"
              :active-value="BannerStatus.ACTIVE"
              :inactive-value="BannerStatus.INACTIVE"
              @change="handleStatusChange(row)"
            />
          </template>
        </el-table-column>
        <el-table-column label="数据统计" width="150" align="center">
          <template #default="{ row }">
            <div>浏览: {{ row.viewCount }}</div>
            <div>点击: {{ row.clickCount }}</div>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right" align="center">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pagination.current"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="pagination.total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, ArrowUp, ArrowDown, Document, CreditCard, Link, Cellphone } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'
import { bannerApi } from '@/api/banner'
import { formatDateTime } from '@/utils/format.ts'
import type { Banner } from '@/types/api'
import { BannerStatus, BannerLinkType } from '@/types/api'

const router = useRouter()
const loading = ref(false)
const bannerList = ref<Banner[]>([])

// 搜索表单
const searchForm = reactive({
  keyword: '',
  status: undefined as BannerStatus | undefined,
  linkType: undefined as BannerLinkType | undefined,
})

// 分页
const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0,
})

// 获取轮播图列表
const fetchBannerList = async () => {
  loading.value = true
  try {
    console.log('开始获取轮播图列表，参数：', {
      page: pagination.current - 1,
      size: pagination.pageSize,
      keyword: searchForm.keyword,
      status: searchForm.status,
      linkType: searchForm.linkType,
    })
    
    const res = await bannerApi.getList({
      page: pagination.current - 1, // 后端从0开始
      size: pagination.pageSize,
      keyword: searchForm.keyword,
      status: searchForm.status,
      linkType: searchForm.linkType,
    })

    console.log('获取轮播图列表响应：', res)
    
    if (res && res.data) {
      console.log('设置轮播图列表数据：', res.data.content)
      bannerList.value = res.data.content
      pagination.total = res.data.totalElements
    } else {
      console.warn('轮播图列表响应为空或数据格式不正确')
    }
  } catch (error) {
    console.error('获取轮播图列表失败', error)
    ElMessage.error('获取轮播图列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.current = 1
  fetchBannerList()
}

// 重置搜索
const resetSearch = () => {
  searchForm.keyword = ''
  searchForm.status = undefined
  searchForm.linkType = undefined
  handleSearch()
}

// 分页大小变化
const handleSizeChange = (size: number) => {
  pagination.pageSize = size
  fetchBannerList()
}

// 页码变化
const handleCurrentChange = (page: number) => {
  pagination.current = page
  fetchBannerList()
}

// 排序变化
const handleSortChange = (column: { prop: string; order: string }) => {
  // 实现排序逻辑
  fetchBannerList()
}

// 创建轮播图
const handleCreate = () => {
  router.push('/banners/create')
}

// 跳转到关联关系管理
const goToRelationManage = () => {
  router.push('/banners/relations')
}

// 编辑轮播图
const handleEdit = (row: Banner) => {
  router.push(`/banners/edit/${row.id}`)
}

// 删除轮播图
const handleDelete = (row: Banner) => {
  ElMessageBox.confirm(`确定要删除轮播图「${row.title}」吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(async () => {
    try {
      await bannerApi.delete(row.id)
      ElMessage.success('删除成功')
      fetchBannerList()
    } catch (error) {
      console.error('删除轮播图失败', error)
      ElMessage.error('删除轮播图失败')
    }
  }).catch(() => {
    // 取消删除
  })
}

// 更新轮播图状态
const handleStatusChange = async (row: Banner) => {
  try {
    await bannerApi.updateStatus(row.id, row.status)
    ElMessage.success('状态更新成功')
  } catch (error) {
    console.error('更新轮播图状态失败', error)
    ElMessage.error('更新轮播图状态失败')
    // 恢复状态
    row.status = row.status === BannerStatus.ACTIVE ? BannerStatus.INACTIVE : BannerStatus.ACTIVE
  }
}

// 上移
const moveUp = (index: number) => {
  if (index > 0) {
    const temp = bannerList.value[index]
    bannerList.value[index] = bannerList.value[index - 1]
    bannerList.value[index - 1] = temp
    updateBannerOrder()
  }
}

// 下移
const moveDown = (index: number) => {
  if (index < bannerList.value.length - 1) {
    const temp = bannerList.value[index]
    bannerList.value[index] = bannerList.value[index + 1]
    bannerList.value[index + 1] = temp
    updateBannerOrder()
  }
}

// 更新轮播图顺序
const updateBannerOrder = async () => {
  try {
    const bannerIds = bannerList.value.map(banner => banner.id)
    await bannerApi.updateOrder(bannerIds)
    ElMessage.success('排序更新成功')
  } catch (error) {
    console.error('更新轮播图排序失败', error)
    ElMessage.error('更新轮播图排序失败')
    // 刷新列表，恢复原始顺序
    fetchBannerList()
  }
}

// 获取链接类型标签样式
const getLinkTypeTag = (linkType: BannerLinkType) => {
  const typeMap: Record<BannerLinkType, string> = {
    [BannerLinkType.NONE]: 'info',
    [BannerLinkType.CARD]: 'success',
    [BannerLinkType.NEWS]: 'primary',
    [BannerLinkType.EXTERNAL]: 'warning',
    [BannerLinkType.MINIPROGRAM]: 'danger',
  }
  return typeMap[linkType] || 'info'
}

// 获取链接类型标签文本
const getLinkTypeLabel = (linkType: BannerLinkType) => {
  const labelMap: Record<BannerLinkType, string> = {
    [BannerLinkType.NONE]: '无链接',
    [BannerLinkType.CARD]: '信用卡',
    [BannerLinkType.NEWS]: '资讯',
    [BannerLinkType.EXTERNAL]: '外部链接',
    [BannerLinkType.MINIPROGRAM]: '小程序',
  }
  return labelMap[linkType] || '未知'
}

onMounted(() => {
  fetchBannerList()
})
</script>

<style scoped>
.banner-list {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-actions {
  display: flex;
  gap: 12px;
}

.search-bar {
  margin-bottom: 20px;
  padding: 15px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.table-container {
  background-color: #fff;
  border-radius: 4px;
  padding: 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.sort-buttons {
  display: flex;
  flex-direction: row;
  align-items: center;
  gap: 6px;
  height: 100%;
  justify-content: center;
}

.sort-buttons .el-button {
  margin: 0;
  box-shadow: none;
  transition: all 0.2s ease;
  width: 24px;
  height: 24px;
  padding: 0;
}

.sort-buttons .el-button:hover {
  transform: none;
  opacity: 0.8;
}

.sort-button-up {
  background-color: #409EFF;
  border: none;
}

.sort-button-down {
  background-color: #67C23A;
  border: none;
}

.sort-buttons .el-icon {
  font-size: 12px;
}

.link-target-cell {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.link-type-tag {
  align-self: flex-start;
}

.link-info {
  display: flex;
  align-items: center;
  font-size: 12px;
  color: #606266;
}

.no-link {
  color: #909399;
  font-style: italic;
}
</style>