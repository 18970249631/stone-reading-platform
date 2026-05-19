<template>
  <div class="reader-page" :class="{ fullscreen: isFullscreen }">
    <header class="reader-header" v-if="!isFullscreen">
      <div class="header-left">
        <el-button :icon="ArrowLeft" @click="goBack">返回</el-button>
      </div>
      <div class="header-center">
        <h2>{{ bookInfo.title }}</h2>
        <span class="chapter-title">{{ currentChapter?.title }}</span>
      </div>
      <div class="header-right">
        <el-button :icon="Setting" @click="showSettings = true">设置</el-button>
      </div>
    </header>

    <div class="reader-content" ref="contentRef" @click="handleTap">
      <div class="page" :class="{ flipping: isFlipping }" @transitionend="onFlipEnd">
        <div class="page-inner" v-html="displayContent"></div>
      </div>
    </div>

    <footer class="reader-footer" v-if="!isFullscreen">
      <div class="progress-bar">
        <el-slider v-model="readProgress" :step="1" :min="0" :max="100" />
        <span class="progress-text">{{ readProgress }}%</span>
      </div>
      <div class="chapter-nav">
        <el-button :disabled="!hasPrevChapter" @click="prevChapter">上一章</el-button>
        <el-button @click="toggleFullscreen">{{ isFullscreen ? '退出全屏' : '全屏' }}</el-button>
        <el-button :disabled="!hasNextChapter" @click="nextChapter">下一章</el-button>
      </div>
    </footer>

    <div class="catalog-panel" :class="{ open: showCatalog }">
      <div class="catalog-header">
        <h3>目录</h3>
        <el-button :icon="Close" @click="showCatalog = false" />
      </div>
      <div class="catalog-list">
        <div
          v-for="chapter in chapterList"
          :key="chapter.id"
          class="catalog-item"
          :class="{ active: chapter.id === currentChapterId }"
          @click="jumpToChapter(chapter.id)"
        >
          {{ chapter.title }}
        </div>
      </div>
    </div>

    <el-drawer v-model="showSettings" title="阅读设置" direction="rtl" size="300px">
      <div class="settings-content">
        <div class="setting-item">
          <label>字体大小</label>
          <el-slider v-model="settings.fontSize" :min="14" :max="28" />
        </div>
        <div class="setting-item">
          <label>行间距</label>
          <el-slider v-model="settings.lineHeight" :min="1" :max="3" :step="0.1" />
        </div>
        <div class="setting-item">
          <label>翻页模式</label>
          <el-select v-model="settings.pageMode">
            <el-option label="仿真翻页" value="仿真" />
            <el-option label="滑动" value="滑动" />
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
      </div>
    </el-drawer>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ArrowLeft, Setting, Close } from '@element-plus/icons-vue'
import { getBookDetail, getChapterList, getChapterContent } from '@/api/book'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()

const contentRef = ref()
const bookInfo = ref({})
const chapterList = ref([])
const currentChapterId = ref(Number(route.params.chapterId) || null)
const currentChapter = ref(null)
const chapterContent = ref('')
const isFlipping = ref(false)
const isFullscreen = ref(false)
const readProgress = ref(0)
const showCatalog = ref(false)
const showSettings = ref(false)

const settings = reactive({
  fontSize: 18,
  lineHeight: 1.8,
  pageMode: '仿真',
  theme: 'day'
})

const themes = [
  { value: 'day', bg: '#f5f5f5', color: '#333' },
  { value: 'night', bg: '#1a1a1a', color: '#999' },
  { value: 'sepia', bg: '#f4ecd8', color: '#5b4636' },
  { value: 'eye', bg: '#c7edcc', color: '#333' }
]

const hasPrevChapter = computed(() => {
  if (!currentChapter.value || !chapterList.value.length) return false
  const idx = chapterList.value.findIndex(c => c.id === currentChapterId.value)
  return idx > 0
})

const hasNextChapter = computed(() => {
  if (!currentChapter.value || !chapterList.value.length) return false
  const idx = chapterList.value.findIndex(c => c.id === currentChapterId.value)
  return idx < chapterList.value.length - 1
})

const displayContent = computed(() => {
  if (!chapterContent.value) return ''
  const paragraphs = chapterContent.value.split('\n')
  return paragraphs.map(p => `<p>${p}</p>`).join('')
})

const loadBookInfo = async () => {
  try {
    const res = await getBookDetail(route.params.bookId)
    bookInfo.value = res.data
  } catch (e) {
    console.error(e)
  }
}

const loadChapterList = async () => {
  try {
    const res = await getChapterList(route.params.bookId)
    chapterList.value = res.data || []

    if (!currentChapterId.value && chapterList.value.length > 0) {
      currentChapterId.value = chapterList.value[0].id
    }
  } catch (e) {
    console.error(e)
  }
}

const loadChapterContent = async () => {
  if (!currentChapterId.value) return

  try {
    const res = await getChapterContent(currentChapterId.value)
    currentChapter.value = res.data
    chapterContent.value = res.data.content || ''

    const idx = chapterList.value.findIndex(c => c.id === currentChapterId.value)
    if (idx >= 0) {
      readProgress.value = Math.round((idx / chapterList.value.length) * 100)
    }
  } catch (e) {
    ElMessage.error('加载章节失败')
  }
}

const prevChapter = () => {
  if (!hasPrevChapter.value) return
  const idx = chapterList.value.findIndex(c => c.id === currentChapterId.value)
  currentChapterId.value = chapterList.value[idx - 1].id
}

const nextChapter = () => {
  if (!hasNextChapter.value) return
  const idx = chapterList.value.findIndex(c => c.id === currentChapterId.value)
  currentChapterId.value = chapterList.value[idx + 1].id
}

const jumpToChapter = (chapterId) => {
  currentChapterId.value = chapterId
  showCatalog.value = false
}

const goBack = () => {
  router.push(`/book/${route.params.bookId}`)
}

const toggleFullscreen = () => {
  isFullscreen.value = !isFullscreen.value
}

const handleTap = (e) => {
  const rect = contentRef.value.getBoundingClientRect()
  const x = e.clientX - rect.left
  const width = rect.width

  if (x < width * 0.3) {
    prevChapter()
  } else if (x > width * 0.7) {
    nextChapter()
  }
}

const onFlipEnd = () => {
  isFlipping.value = false
}

watch(currentChapterId, () => {
  loadChapterContent()
})

watch(() => settings.theme, (val) => {
  document.body.className = `theme-${val}`
})

onMounted(() => {
  loadBookInfo()
  loadChapterList()
  loadChapterContent()
})
</script>

<style lang="scss" scoped>
.reader-page {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: #f5f5f5;

  &.fullscreen {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    z-index: 9999;
  }
}

.reader-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 20px;
  background: white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);

  .header-center {
    text-align: center;

    h2 {
      font-size: 16px;
      margin: 0;
    }

    .chapter-title {
      font-size: 12px;
      color: #999;
    }
  }
}

.reader-content {
  flex: 1;
  overflow-y: auto;
  padding: 20px;

  .page {
    max-width: 800px;
    margin: 0 auto;
    background: white;
    border-radius: 8px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
    padding: 40px;
    min-height: calc(100vh - 200px);

    &.flipping {
      animation: flip 0.3s ease-out;
    }
  }

  .page-inner {
    font-size: v-bind('settings.fontSize + "px"');
    line-height: v-bind('settings.lineHeight');
    color: #333;

    :deep(p) {
      margin-bottom: 1em;
      text-indent: 2em;
    }
  }
}

@keyframes flip {
  0% { opacity: 0; transform: translateX(20px); }
  100% { opacity: 1; transform: translateX(0); }
}

.reader-footer {
  padding: 16px 20px;
  background: white;
  box-shadow: 0 -2px 8px rgba(0, 0, 0, 0.1);

  .progress-bar {
    display: flex;
    align-items: center;
    gap: 16px;
    margin-bottom: 12px;

    .progress-text {
      font-size: 14px;
      color: #666;
      min-width: 40px;
    }
  }

  .chapter-nav {
    display: flex;
    justify-content: center;
    gap: 16px;
  }
}

.catalog-panel {
  position: fixed;
  top: 0;
  left: 0;
  width: 300px;
  height: 100vh;
  background: white;
  box-shadow: 2px 0 12px rgba(0, 0, 0, 0.1);
  transform: translateX(-100%);
  transition: transform 0.3s;
  z-index: 1000;

  &.open {
    transform: translateX(0);
  }

  .catalog-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 16px;
    border-bottom: 1px solid #eee;

    h3 {
      margin: 0;
    }
  }

  .catalog-list {
    overflow-y: auto;
    height: calc(100vh - 60px);

    .catalog-item {
      padding: 14px 16px;
      cursor: pointer;
      border-bottom: 1px solid #f5f5f5;

      &:hover {
        background: #f5f5f5;
      }

      &.active {
        color: #409eff;
        background: #ecf5ff;
      }
    }
  }
}

.settings-content {
  .setting-item {
    margin-bottom: 24px;

    label {
      display: block;
      margin-bottom: 8px;
      font-weight: 500;
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

.theme-night {
  background: #1a1a1a;
}

.theme-sepia {
  background: #f4ecd8;
}

.theme-eye {
  background: #c7edcc;
}
</style>