<template>
  <div class="markdown-editor" :style="{ height: height }">
    <Editor
      :value="content"
      :plugins="plugins"
      @change="handleChange"
      :locale="locale"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import { Editor } from '@bytemd/vue-next'
import gfm from '@bytemd/plugin-gfm'
import highlight from '@bytemd/plugin-highlight'
import gemoji from '@bytemd/plugin-gemoji'
import math from '@bytemd/plugin-math'
import mermaid from '@bytemd/plugin-mermaid'

// 导入样式
import 'bytemd/dist/index.css'
import 'highlight.js/styles/default.css'

interface Props {
  modelValue?: string
  height?: string
  placeholder?: string
}

interface Emits {
  (e: 'update:modelValue', value: string): void
}

const props = withDefaults(defineProps<Props>(), {
  modelValue: '',
  height: '500px',
  placeholder: '请输入Markdown内容...'
})

const emit = defineEmits<Emits>()

const content = ref(props.modelValue)

// 配置插件
const plugins = [
  gfm(),
  highlight(),
  gemoji(),
  math(),
  mermaid()
]

// 中文本地化
const locale = {
  write: '编辑',
  preview: '预览',
  writeOnly: '仅编辑',
  previewOnly: '仅预览',
  fullscreen: '全屏',
  exitFullscreen: '退出全屏',
  help: '帮助'
}

// 处理内容变化
const handleChange = (value: string) => {
  content.value = value
  emit('update:modelValue', value)
}

// 监听外部值变化
watch(() => props.modelValue, (newValue) => {
  content.value = newValue || ''
}, { immediate: true })
</script>

<style scoped>
.markdown-editor {
  border: 1px solid var(--el-border-color);
  border-radius: 4px;
  overflow: hidden;
  min-height: 400px;
  max-height: 600px;
}

.markdown-editor :deep(.bytemd) {
  height: 100%;
  min-height: 400px;
}

.markdown-editor :deep(.bytemd-toolbar) {
  border-bottom: 1px solid var(--el-border-color);
  background-color: var(--el-bg-color-page);
  height: 40px;
  flex-shrink: 0;
}

.markdown-editor :deep(.bytemd-body) {
  height: calc(100% - 40px);
  min-height: 360px;
  display: flex;
}

.markdown-editor :deep(.bytemd-editor),
.markdown-editor :deep(.bytemd-preview) {
  height: 100%;
  overflow-y: auto;
}

.markdown-editor :deep(.bytemd-editor) {
  font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
  font-size: 14px;
  line-height: 1.5;
}

.markdown-editor :deep(.bytemd-preview) {
  font-size: 14px;
  line-height: 1.6;
}

.markdown-editor :deep(.CodeMirror) {
  height: 100% !important;
}

.markdown-editor :deep(.CodeMirror-scroll) {
  height: 100% !important;
}
</style>