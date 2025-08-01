<template>
  <div class="login-form">
    <div class="form-header">
      <h2>欢迎回来</h2>
      <p>请登录您的管理员账户</p>
    </div>
    
    <a-form
      :model="formData"
      :rules="rules"
      @finish="handleSubmit"
      layout="vertical"
      class="form"
    >
      <a-form-item name="username" label="用户名">
        <a-input
          v-model:value="formData.username"
          size="large"
          placeholder="请输入用户名"
          :prefix="h(UserOutlined)"
        />
      </a-form-item>
      
      <a-form-item name="password" label="密码">
        <a-input-password
          v-model:value="formData.password"
          size="large"
          placeholder="请输入密码"
          :prefix="h(LockOutlined)"
        />
      </a-form-item>
      
      <a-form-item>
        <div class="form-options">
          <a-checkbox v-model:checked="rememberMe">
            记住我
          </a-checkbox>
          <a href="#" class="forgot-password">忘记密码？</a>
        </div>
      </a-form-item>
      
      <a-form-item>
        <a-button
          type="primary"
          html-type="submit"
          size="large"
          block
          :loading="userStore.loading"
          class="submit-button"
        >
          登录
        </a-button>
      </a-form-item>
    </a-form>
  </div>
</template>

<script setup lang="ts">
import { ref, h } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { UserOutlined, LockOutlined } from '@ant-design/icons-vue'
import type { LoginRequest } from '@/types/api'

const router = useRouter()
const userStore = useUserStore()

const formData = ref<LoginRequest>({
  username: '',
  password: '',
})

const rememberMe = ref(false)

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度应在3-20个字符之间', trigger: 'blur' },
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6个字符', trigger: 'blur' },
  ],
}

const handleSubmit = async () => {
  try {
    const success = await userStore.login(formData.value)
    if (success) {
      router.push('/dashboard')
    }
  } catch (error) {
    console.error('Login error:', error)
  }
}
</script>

<style scoped>
.login-form {
  width: 100%;
  min-width: 400px;
}

.form-header {
  text-align: center;
  margin-bottom: 32px;
}

.form-header h2 {
  font-size: 32px;
  font-weight: 600;
  color: #1a1a1a;
  margin: 0 0 12px 0;
}

.form-header p {
  color: #666;
  margin: 0;
  font-size: 16px;
}

.form {
  width: 100%;
}

.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.forgot-password {
  color: #1890ff;
  text-decoration: none;
  font-size: 16px;
}

.forgot-password:hover {
  color: #40a9ff;
}

.submit-button {
  height: 56px;
  font-size: 18px;
  font-weight: 500;
  border-radius: 8px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  border: none;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
  transition: all 0.3s ease;
  margin-top: 8px;
}

.submit-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(102, 126, 234, 0.4);
}

.submit-button:active {
  transform: translateY(0);
}

:deep(.ant-form-item-label > label) {
  font-weight: 500;
  color: #333;
  font-size: 16px;
}

:deep(.ant-input-affix-wrapper) {
  border-radius: 8px;
  border: 1px solid #e0e0e0;
  transition: all 0.3s ease;
}

:deep(.ant-input-affix-wrapper:focus),
:deep(.ant-input-affix-wrapper-focused) {
  border-color: #667eea;
  box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.1);
}

:deep(.ant-input) {
  font-size: 16px;
  height: 48px;
}

:deep(.ant-checkbox-wrapper) {
  font-size: 16px;
}

:deep(.ant-form-item) {
  margin-bottom: 24px;
}

:deep(.ant-input-affix-wrapper) {
  padding: 8px 12px;
}
</style>