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
          <el-input
            type="textarea"
            :rows="10"
            v-model="newsForm.content"
            placeholder="请输入新闻内容 (Markdown格式)"
          ></el-input>
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

        <el-form-item label="封面图片URL" prop="imageUrl">
          <el-input v-model="newsForm.imageUrl" placeholder="请输入封面图片URL"></el-input>
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
import { newsApi } from '@/api/news'
import { categoryApi } from '@/api/category'
import { tagApi } from '@/api/tag'
import type { FormInstance, FormRules } from 'element-plus'
import type { News, Category, Tag, NewsCreate } from '@/types/api'

const route = useRoute()
const router = useRouter()

const newsFormRef = ref<FormInstance>()
const isEdit = computed(() => !!route.params.id)
const loading = ref(false)

const newsForm = ref<Partial<News & { categoryId?: number; tagIds?: number[] }>>({
  title: '',
  summary: '',
  content: '',
  categoryId: undefined,
  tagIds: [],
  imageUrl: '',
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
      const { category, tags: newsTags, ...rest } = res.data
      newsForm.value = {
        ...rest,
        categoryId: category?.id,
        tagIds: newsTags?.map(tag => tag.id) || [],
      }
    }
  } catch (error) {
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
      loading.value = true
      try {
        const params: NewsCreate = {
          title: newsForm.value.title || '',
          summary: newsForm.value.summary || '',
          content: newsForm.value.content || '',
          imageUrl: newsForm.value.imageUrl,
          status: newsForm.value.status || 'DRAFT',
          categoryId: newsForm.value.categoryId,
          tagIds: newsForm.value.tagIds,
        };

        if (isEdit.value) {
          const newsId = Number(route.params.id)
          await newsApi.update(newsId, params)
          ElMessage.success('新闻更新成功')
        } else {
          await newsApi.create(params as any) // 'any' is used because create expects NewsCreate type
          ElMessage.success('新闻创建成功')
        }
        router.push('/news/list')
      } catch (error) {
        ElMessage.error(isEdit.value ? '新闻更新失败' : '新闻创建失败')
      } finally {
        loading.value = false
      }
    }
  })
}

const handleCancel = () => {
  router.push('/news/list')
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
</style>