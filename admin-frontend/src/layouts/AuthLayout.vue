<template>
  <div class="auth-layout">
    <div class="auth-container">
      <div class="auth-header">
        <div class="logo">
          <img src="/logo.svg" alt="PrimeCard" class="logo-image" />
          <h1 class="logo-text">PrimeCard Admin</h1>
        </div>
      </div>
      <div class="auth-content">
        <router-view />
      </div>
      <div class="auth-footer">
        <p>&copy; 2024 PrimeCard. All rights reserved.</p>
      </div>
    </div>
    <div class="auth-background">
      <div class="background-pattern"></div>
    </div>
  </div>
</template>

<script setup lang="ts">
// 认证布局组件
</script>

<style scoped>
.auth-layout {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  position: relative;
  overflow: hidden;
}

.auth-background {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 0;
}

.background-pattern {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-image: 
    radial-gradient(circle at 25% 25%, rgba(255, 255, 255, 0.1) 0%, transparent 50%),
    radial-gradient(circle at 75% 75%, rgba(255, 255, 255, 0.1) 0%, transparent 50%);
  background-size: 100px 100px;
  animation: float 20s ease-in-out infinite;
}

@keyframes float {
  0%, 100% { transform: translateY(0px) rotate(0deg); }
  50% { transform: translateY(-20px) rotate(180deg); }
}

.auth-container {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 24px;
  padding: 48px;
  box-shadow: 
    0 20px 40px rgba(0, 0, 0, 0.1),
    0 0 0 1px rgba(255, 255, 255, 0.2);
  width: 100%;
  max-width: 480px;
  position: relative;
  z-index: 1;
}

.auth-header {
  text-align: center;
  margin-bottom: 32px;
}

.logo {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  margin-bottom: 24px;
}

.logo-image {
  width: 48px;
  height: 48px;
}

.logo-text {
  font-size: 28px;
  font-weight: 700;
  color: #1a1a1a;
  margin: 0;
  background: linear-gradient(135deg, #667eea, #764ba2);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.auth-content {
  margin-bottom: 32px;
}

.auth-footer {
  text-align: center;
  color: #666;
  font-size: 14px;
}

.auth-footer p {
  margin: 0;
}

@media (max-width: 768px) {
  .auth-container {
    margin: 20px;
    padding: 32px 24px;
    max-width: none;
  }
  
  .logo-text {
    font-size: 24px;
  }
}
</style>