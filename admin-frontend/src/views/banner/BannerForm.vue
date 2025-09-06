<template>
  <div class="banner-form">
    <div class="page-header">
      <h1 class="page-title">{{ isEdit ? '编辑轮播图' : '新增轮播图' }}</h1>
    </div>

    <div class="form-container">
      <el-form
        ref="formRef"
        :model="bannerForm"
        :rules="rules"
        label-width="120px"
        label-position="right"
        status-icon
      >
        <el-form-item label="轮播图标题" prop="title">
          <el-input v-model="bannerForm.title" placeholder="请输入轮播图标题" maxlength="100" show-word-limit />
        </el-form-item>

        <el-form-item label="轮播图片" prop="imageUrl">
          <el-upload
            class="banner-upload"
            action="/api/files/oss/upload?type=banner"
            :show-file-list="false"
            :on-success="handleUploadSuccess"
            :before-upload="beforeUpload"
            :headers="uploadHeaders"
          >
            <img v-if="bannerForm.imageUrl" :src="bannerForm.imageUrl" class="banner-image" />
            <el-icon v-else class="banner-uploader-icon"><Plus /></el-icon>
          </el-upload>
          <div class="upload-tip">建议尺寸: 1200 x 400 像素，格式: JPG/PNG，文件将上传到阿里云OSS</div>
        </el-form-item>

        <el-form-item label="链接类型" prop="linkType">
          <el-select v-model="bannerForm.linkType" placeholder="请选择链接类型">
            <el-option label="无链接" :value="BannerLinkType.NONE" />
            <el-option label="信用卡" :value="BannerLinkType.CARD" />
            <el-option label="资讯" :value="BannerLinkType.NEWS" />
            <el-option label="外部链接" :value="BannerLinkType.EXTERNAL" />
            <el-option label="小程序" :value="BannerLinkType.MINIPROGRAM" />
          </el-select>
        </el-form-item>

        <!-- 根据链接类型显示不同的表单项 -->
        <template v-if="bannerForm.linkType === BannerLinkType.CARD">
          <el-form-item label="关联信用卡" prop="linkId">
            <el-select
              v-model="bannerForm.linkId"
              placeholder="请选择关联的信用卡"
              filterable
              clearable
              style="width: 100%"
              @focus="loadCreditCards"
            >
              <el-option
                v-for="card in creditCards"
                :key="card.id"
                :label="`${card.name} (${card.bankName})`"
                :value="card.id"
              >
                <div style="display: flex; justify-content: space-between; align-items: center;">
                  <span>{{ card.name }}</span>
                  <el-tag size="small" type="info">{{ card.bankName }}</el-tag>
                </div>
              </el-option>
            </el-select>
            <div class="form-help-text">选择轮播图要关联的信用卡</div>
          </el-form-item>
        </template>

        <template v-if="bannerForm.linkType === BannerLinkType.NEWS">
          <el-form-item label="关联新闻" prop="linkId">
            <el-select
              v-model="bannerForm.linkId"
              placeholder="请选择关联的新闻"
              filterable
              clearable
              style="width: 100%"
              @focus="loadNews"
            >
              <el-option
                v-for="news in newsList"
                :key="news.id"
                :label="news.title"
                :value="news.id"
              >
                <div style="display: flex; justify-content: space-between; align-items: center;">
                  <span style="max-width: 300px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap;">{{ news.title }}</span>
                  <div>
                    <el-tag size="small" :type="getNewsStatusType(news.status)">{{ getNewsStatusText(news.status) }}</el-tag>
                    <el-tag size="small" type="info" style="margin-left: 4px;">{{ news.categoryName || '无分类' }}</el-tag>
                  </div>
                </div>
              </el-option>
            </el-select>
            <div class="form-help-text">选择轮播图要关联的新闻资讯</div>
          </el-form-item>
        </template>

        <template v-if="bannerForm.linkType === BannerLinkType.EXTERNAL">
          <el-form-item label="外部链接" prop="linkUrl">
            <el-input v-model="bannerForm.linkUrl" placeholder="请输入外部链接URL" />
          </el-form-item>
        </template>

        <template v-if="bannerForm.linkType === BannerLinkType.MINIPROGRAM">
          <el-form-item label="小程序AppID" prop="linkAppid">
            <el-input v-model="bannerForm.linkAppid" placeholder="请输入小程序AppID" />
          </el-form-item>
          <el-form-item label="小程序页面路径" prop="linkPage">
            <el-input v-model="bannerForm.linkPage" placeholder="请输入小程序页面路径" />
          </el-form-item>
        </template>

        <el-form-item label="排序顺序" prop="sortOrder">
          <el-input-number v-model="bannerForm.sortOrder" :min="0" placeholder="数字越小越靠前" />
        </el-form-item>

        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="bannerForm.status">
            <el-radio :label="BannerStatus.ACTIVE">活跃</el-radio>
            <el-radio :label="BannerStatus.INACTIVE">非活跃</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="展示时间范围">
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="开始时间" label-width="80px" prop="startTime">
                <el-date-picker
                  v-model="bannerForm.startTime"
                  type="datetime"
                  placeholder="选择开始时间"
                  format="YYYY-MM-DD HH:mm:ss"
                  value-format="YYYY-MM-DD HH:mm:ss"
                  clearable
                />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="结束时间" label-width="80px" prop="endTime">
                <el-date-picker
                  v-model="bannerForm.endTime"
                  type="datetime"
                  placeholder="选择结束时间"
                  format="YYYY-MM-DD HH:mm:ss"
                  value-format="YYYY-MM-DD HH:mm:ss"
                  clearable
                />
              </el-form-item>
            </el-col>
          </el-row>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="submitForm">保存</el-button>
          <el-button @click="goBack">取消</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { bannerApi } from '@/api/banner'
import { newsApi } from '@/api/news'
import { creditCardApi } from '@/api/creditCard'
import { useAuthStore } from '@/stores/auth'
import { BannerStatus, BannerLinkType } from '@/types/api'
import type { BannerCreate, News, CreditCard } from '@/types/api'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const formRef = ref<FormInstance>()
const loading = ref(false)

// 上传请求头
const uploadHeaders = computed(() => ({
  'Authorization': `Bearer ${authStore.token || localStorage.getItem('token')}`,
  'Accept': 'application/json'
}))

// 选择器数据
const newsList = ref<News[]>([])
const creditCards = ref<CreditCard[]>([])
const newsLoading = ref(false)
const cardLoading = ref(false)

// 判断是否为编辑模式
const isEdit = computed(() => route.params.id !== undefined)

// 表单数据
const bannerForm = reactive<BannerCreate>({
  title: '',
  imageUrl: '',
  linkType: BannerLinkType.NONE,
  status: BannerStatus.ACTIVE,
})

// 表单验证规则
const rules = reactive<FormRules>({
  title: [
    { required: true, message: '请输入轮播图标题', trigger: 'blur' },
    { max: 100, message: '标题长度不能超过100个字符', trigger: 'blur' }
  ],
  imageUrl: [
    { required: true, message: '请上传轮播图片', trigger: 'change' }
  ],
  linkType: [
    { required: true, message: '请选择链接类型', trigger: 'change' }
  ],
  linkUrl: [
    { required: true, message: '请输入外部链接URL', trigger: 'blur', 
      validator: (rule, value, callback) => {
        if (bannerForm.linkType === BannerLinkType.EXTERNAL && !value) {
          callback(new Error('请输入外部链接URL'))
        } else {
          callback()
        }
      }
    }
  ],
  linkId: [
    { required: true, message: '请输入关联ID', trigger: 'blur',
      validator: (rule, value, callback) => {
        if ((bannerForm.linkType === BannerLinkType.CARD || bannerForm.linkType === BannerLinkType.NEWS) && !value) {
          callback(new Error('请输入关联ID'))
        } else {
          callback()
        }
      }
    }
  ],
  linkAppid: [
    { required: true, message: '请输入小程序AppID', trigger: 'blur',
      validator: (rule, value, callback) => {
        if (bannerForm.linkType === BannerLinkType.MINIPROGRAM && !value) {
          callback(new Error('请输入小程序AppID'))
        } else {
          callback()
        }
      }
    }
  ],
  linkPage: [
    { required: true, message: '请输入小程序页面路径', trigger: 'blur',
      validator: (rule, value, callback) => {
        if (bannerForm.linkType === BannerLinkType.MINIPROGRAM && !value) {
          callback(new Error('请输入小程序页面路径'))
        } else {
          callback()
        }
      }
    }
  ],
  endTime: [
    { validator: (rule, value, callback) => {
        if (bannerForm.startTime && value && new Date(value) < new Date(bannerForm.startTime)) {
          callback(new Error('结束时间不能早于开始时间'))
        } else {
          callback()
        }
      }, trigger: 'change'
    }
  ]
})

// 获取轮播图详情
const fetchBannerDetail = async (id: number) => {
  loading.value = true
  try {
    const res = await bannerApi.getById(id)
    console.log('获取轮播图详情响应：', res)
    if (res && res.data) {
      // 将获取到的数据填充到表单中
      bannerForm.title = res.data.title
      bannerForm.imageUrl = res.data.imageUrl
      bannerForm.linkType = res.data.linkType
      bannerForm.linkUrl = res.data.linkUrl
      bannerForm.linkId = res.data.linkId
      bannerForm.linkAppid = res.data.linkAppid
      bannerForm.linkPage = res.data.linkPage
      bannerForm.sortOrder = res.data.sortOrder
      bannerForm.status = res.data.status
      bannerForm.startTime = res.data.startTime
      bannerForm.endTime = res.data.endTime
    } else {
      console.warn('轮播图详情响应为空或数据格式不正确')
    }
  } catch (error) {
    console.error('获取轮播图详情失败', error)
    ElMessage.error('获取轮播图详情失败')
  } finally {
    loading.value = false
  }
}

// 提交表单
const submitForm = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        if (isEdit.value) {
          // 编辑模式
          const id = Number(route.params.id)
          console.log('提交更新轮播图表单：', bannerForm)
          const res = await bannerApi.update(id, bannerForm)
          console.log('更新轮播图响应：', res)
          ElMessage.success('更新轮播图成功')
        } else {
          // 创建模式
          console.log('提交创建轮播图表单：', bannerForm)
          const res = await bannerApi.create(bannerForm)
          console.log('创建轮播图响应：', res)
          ElMessage.success('创建轮播图成功')
        }
        goBack()
      } catch (error) {
        console.error('保存轮播图失败', error)
        ElMessage.error('保存轮播图失败')
      } finally {
        loading.value = false
      }
    } else {
      ElMessage.error('请完善表单信息')
    }
  })
}

// 上传前验证
const beforeUpload = (file: File) => {
  const isImage = file.type === 'image/jpeg' || file.type === 'image/png' || file.type === 'image/webp'
  const isLt10M = file.size / 1024 / 1024 < 10

  if (!isImage) {
    ElMessage.error('上传图片只能是JPG/PNG/WEBP格式!')
    return false
  }
  if (!isLt10M) {
    ElMessage.error('上传图片大小不能超过10MB!')
    return false
  }
  
  // 显示上传中提示
  ElMessage.info('正在上传到阿里云OSS...')
  return true
}

// 上传成功回调
const handleUploadSuccess = (response: any) => {
  console.log('上传成功响应:', response)
  // 根据实际接口返回格式调整
  if (response && response.data && response.data.fileUrl) {
    bannerForm.imageUrl = response.data.fileUrl
    ElMessage.success('图片上传成功')
  } else {
    console.error('响应格式错误，期望的字段：fileUrl，实际响应：', response)
    ElMessage.error('图片上传失败：响应格式错误')
  }
}

// 返回列表页
const goBack = () => {
  router.push('/banners')
}

// 加载新闻数据
const loadNews = async () => {
  if (newsList.value.length > 0) return // 避免重复加载
  
  newsLoading.value = true
  try {
    const res = await newsApi.getList({
      page: 0,
      size: 100, // 加载前100条
      status: 'PUBLISHED' // 只加载已发布的新闻
    })
    if (res.data && res.data.content) {
      newsList.value = res.data.content
    }
  } catch (error) {
    console.error('加载新闻列表失败', error)
    ElMessage.error('加载新闻列表失败')
  } finally {
    newsLoading.value = false
  }
}

// 加载信用卡数据
const loadCreditCards = async () => {
  if (creditCards.value.length > 0) return // 避免重复加载
  
  cardLoading.value = true
  try {
    const res = await creditCardApi.getList({
      page: 0,
      size: 100 // 加载前100条
    })
    if (res.data && res.data.content) {
      creditCards.value = res.data.content
    }
  } catch (error) {
    console.error('加载信用卡列表失败', error)
    ElMessage.error('加载信用卡列表失败')
  } finally {
    cardLoading.value = false
  }
}

// 获取新闻状态类型
const getNewsStatusType = (status: string) => {
  switch (status) {
    case 'PUBLISHED': return 'success'
    case 'DRAFT': return 'warning'
    case 'ARCHIVED': return 'info'
    default: return 'info'
  }
}

// 获取新闻状态文本
const getNewsStatusText = (status: string) => {
  switch (status) {
    case 'PUBLISHED': return '已发布'
    case 'DRAFT': return '草稿'
    case 'ARCHIVED': return '已归档'
    default: return '未知'
  }
}

onMounted(() => {
  // 如果是编辑模式，获取轮播图详情
  if (isEdit.value && route.params.id) {
    fetchBannerDetail(Number(route.params.id))
  }
})
</script>

<style scoped>
.banner-form {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
}

.form-container {
  background-color: #fff;
  border-radius: 4px;
  padding: 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.banner-upload {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 300px;
  height: 100px;
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: border-color 0.3s;
}

.banner-upload:hover {
  border-color: #409eff;
}

.banner-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.banner-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
}

.upload-tip {
  font-size: 12px;
  color: #606266;
  margin-top: 5px;
}

.form-help-text {
  color: #909399;
  font-size: 12px;
  margin-top: 4px;
  line-height: 1.4;
}

:deep(.el-select-dropdown__item) {
  height: auto;
  padding: 8px 20px;
}

:deep(.el-form-item__content) {
  flex-direction: column;
  align-items: flex-start;
}
</style>