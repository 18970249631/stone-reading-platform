<template>
  <el-drawer v-model="visible" title="阅读设置" direction="rtl" size="300px">
    <div class="settings-content">
      <div class="setting-item">
        <label>字体大小</label>
        <el-slider v-model="settings.fontSize" :min="14" :max="28" />
        <span class="preview">{{ settings.fontSize }}px</span>
      </div>

      <div class="setting-item">
        <label>行间距</label>
        <el-slider v-model="settings.lineHeight" :min="1" :max="3" :step="0.1" />
        <span class="preview">{{ settings.lineHeight }}倍</span>
      </div>

      <div class="setting-item">
        <label>翻页模式</label>
        <el-select v-model="settings.pageMode">
          <el-option label="仿真翻页" value="仿真" />
          <el-option label="滑动翻页" value="滑动" />
          <el-option label="淡入淡出" value="淡入淡出" />
          <el-option label="垂直滚动" value="滚动" />
        </el-select>
      </div>

      <div class="setting-item">
        <label>背景主题</label>
        <div class="theme-options">
          <div
            v-for="theme in themes"
            :key="theme.value"
            class="theme-option"
            :class="{ active: settings.theme === theme.value }"
            :style="{ background: theme.bg, color: theme.color }"
            @click="settings.theme = theme.value"
          >
            Aa
          </div>
        </div>
      </div>

      <div class="setting-item">
        <label>阅读进度</label>
        <el-slider v-model="progress" :min="0" :max="100" />
        <span class="preview">{{ progress }}%</span>
      </div>
    </div>
  </el-drawer>
</template>

<script setup>
import { reactive } from 'vue'

defineProps({
  visible: {
    type: Boolean,
    default: false
  }
})

const settings = reactive({
  fontSize: 18,
  lineHeight: 1.8,
  pageMode: '仿真',
  theme: 'day'
})

const progress = 35

const themes = [
  { value: 'day', bg: '#f5f5f5', color: '#333' },
  { value: 'night', bg: '#1a1a1a', color: '#999' },
  { value: 'sepia', bg: '#f4ecd8', color: '#5b4636' },
  { value: 'eye', bg: '#c7edcc', color: '#333' }
]
</script>

<style lang="scss" scoped>
.settings-content {
  .setting-item {
    margin-bottom: 24px;

    label {
      display: block;
      margin-bottom: 8px;
      font-weight: 500;
    }

    .preview {
      display: block;
      text-align: right;
      color: #999;
      font-size: 14px;
      margin-top: 4px;
    }

    .theme-options {
      display: flex;
      gap: 12px;

      .theme-option {
        width: 50px;
        height: 50px;
        border-radius: 8px;
        display: flex;
        align-items: center;
        justify-content: center;
        cursor: pointer;
        border: 2px solid transparent;

        &.active {
          border-color: #409eff;
        }
      }
    }
  }
}
</style>
