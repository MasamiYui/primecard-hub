<template>
  <div class="news-form-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>{{ isEdit ? '编辑新闻' : '新增新闻' }}</span>
        </div>
      </template>

      <el-form
        ref="newsFormRef"
        :model="newsForm"
        :rules="rules"
        label-width="100px"
        v-loading="loading"
      >
        <el-form-item label="标题" prop="title">
          <el-input v-model="newsForm.title" placeholder="请输入新闻标题"></el-input>
        </el-form-item>

        <el-form-item label="摘要" prop="summary">
          <el-input
            type="textarea"
            :rows="3"
            v-model="newsForm.summary"
            placeholder="请输入新闻摘要"
          ></el-input>
        </el-form-item>

        <el-form-item label="内容" prop="content">
          <MarkdownEditor
            v-model="newsForm.content"
            height="500px"
          />
        </el-form-item>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="分类" prop="categoryId">
              <el-select v-model="newsForm.categoryId" placeholder="请选择分类" style="width: 100%;">
                <el-option
                  v-for="item in categories"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="标签" prop="tagIds">
              <el-select
                v-model="newsForm.tagIds"
                multiple
                placeholder="请选择标签"
                style="width: 100%;"
              >
                <el-option
                  v-for="item in tags"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="封面图片" prop="coverImage">
          <div class="cover-image-upload">
            <el-upload
              class="image-uploader"
              action="/api/news/upload"
              :show-file-list="false"
              :on-success="handleImageUploadSuccess"
              :before-upload="beforeImageUpload"
              :headers="uploadHeaders"
            >
              <img v-if="newsForm.coverImage" :src="newsForm.coverImage" class="uploaded-image" />
              <el-icon v-else class="image-uploader-icon"><Plus /></el-icon>
            </el-upload>
            <div class="upload-tips">
              <p>建议尺寸: 800 x 450 像素</p>
              <p>支持格式: JPG/PNG/WEBP，文件将上传到阿里云OSS</p>
            </div>
          </div>
          <el-input 
            v-model="newsForm.coverImage" 
            placeholder="或直接输入图片URL"
            style="margin-top: 10px"
          />
        </el-form-item>

        <el-form-item label="状态" prop="status">
            <el-radio-group v-model="newsForm.status">
                <el-radio label="DRAFT">草稿</el-radio>
                <el-radio label="PUBLISHED">发布</el-radio>
            </el-radio-group>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleSubmit">{{ isEdit ? '更新' : '创建' }}</el-button>
          <el-button @click="handleCancel">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { newsApi } from '@/api/news'
import { categoryApi } from '@/api/category'
import { tagApi } from '@/api/tag'
import { useAuthStore } from '@/stores/auth'
import MarkdownEditor from '@/components/MarkdownEditor.vue'
import type { FormInstance, FormRules } from 'element-plus'
import type { News, Category, Tag, NewsCreate } from '@/types/api'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const newsFormRef = ref<FormInstance>()
const isEdit = computed(() => !!route.params.id)
const loading = ref(false)

// 上传请求头
const uploadHeaders = computed(() => ({
  'Authorization': `Bearer ${authStore.token || localStorage.getItem('token')}`,
  'Accept': 'application/json'
}))

const newsForm = ref<Partial<News & { categoryId?: number; tagIds?: number[] }>>({  
  title: '',
  summary: '',
  content: '',
  categoryId: undefined,
  tagIds: [],
  coverImage: '',
  status: 'DRAFT',
})

const categories = ref<Category[]>([])
const tags = ref<Tag[]>([])

const rules = ref<FormRules>({
  title: [{ required: true, message: '请输入新闻标题', trigger: 'blur' }],
  summary: [{ required: true, message: '请输入新闻摘要', trigger: 'blur' }],
  content: [{ required: true, message: '请输入新闻内容', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }],
})

const getCategoryList = async () => {
  try {
    const res = await categoryApi.getAll()
    if (res.data) {
      categories.value = res.data
    }
  } catch (error) {
    ElMessage.error('获取分类列表失败')
  }
}

const getTagList = async () => {
  try {
    const res = await tagApi.getAll()
    if (res.data) {
      tags.value = res.data
    }
  } catch (error) {
    ElMessage.error('获取标签列表失败')
  }
}

const fetchNewsDetails = async (id: number) => {
  loading.value = true
  try {
    const res = await newsApi.getById(id)
    if (res.data) {
      const { category, categoryId, categoryName, tags: newsTags, ...rest } = res.data
      newsForm.value = {
        ...rest,
        categoryId: categoryId || category?.id || undefined,
        tagIds: newsTags?.map(tag => tag.id) || [],
      }
      console.log('Fetched news details:', res.data) // 记录原始数据
      console.log('Processed news form:', newsForm.value) // 记录处理后的表单数据
    }
  } catch (error) {
    console.error('获取新闻详情失败:', error)
    ElMessage.error('获取新闻详情失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  getCategoryList()
  getTagList()
  if (isEdit.value) {
    const newsId = Number(route.params.id)
    fetchNewsDetails(newsId)
  }
})

const handleSubmit = () => {
  newsFormRef.value?.validate(async (valid) => {
    if (valid) {
      // 确保categoryId有值
      if (!newsForm.value.categoryId) {
        ElMessage.error('请选择分类')
        return
      }
      
      loading.value = true
      try {
        const params: NewsCreate = {
          title: newsForm.value.title || '',
          summary: newsForm.value.summary || '',
          content: newsForm.value.content || '',
          coverImage: newsForm.value.coverImage,
          status: newsForm.value.status || 'DRAFT',
          categoryId: newsForm.value.categoryId,
          tagIds: newsForm.value.tagIds,
        };
        
        console.log('Submitting news with params:', params); // 添加日志

        if (isEdit.value) {
          const newsId = Number(route.params.id)
          await newsApi.update(newsId, params)
          ElMessage.success('新闻更新成功')
        } else {
          await newsApi.create(params as any) // 'any' is used because create expects NewsCreate type
          ElMessage.success('新闻创建成功')
        }
        router.push('/news')
      } catch (error) {
        console.error('提交新闻失败:', error); // 添加错误日志
        ElMessage.error('提交失败')
      } finally {
        loading.value = false
      }
    }
  })
}

const handleCancel = () => {
  router.push('/news/list')
}

// 图片上传前验证
const beforeImageUpload = (file: File) => {
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
  
  ElMessage.info('正在上传到阿里云OSS...')
  return true
}

// 图片上传成功回调
const handleImageUploadSuccess = (response: any) => {
  console.log('新闻图片上传成功响应:', response)
  if (response && response.data && response.data.fileUrl) {
    newsForm.value.coverImage = response.data.fileUrl
    ElMessage.success('封面图片上传成功')
  } else {
    console.error('响应格式错误，期望的字段：fileUrl，实际响应：', response)
    ElMessage.error('图片上传失败：响应格式错误')
  }
}
</script>

<style scoped>
.news-form-container {
  padding: 24px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.cover-image-upload {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
}

.image-uploader {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: border-color 0.3s;
  width: 200px;
  height: 120px;
  display: flex;
  justify-content: center;
  align-items: center;
}

.image-uploader:hover {
  border-color: #409eff;
}

.image-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
}

.uploaded-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.upload-tips {
  margin-top: 8px;
  font-size: 12px;
  color: #606266;
  line-height: 1.4;
}

.upload-tips p {
  margin: 2px 0;
}
</style>