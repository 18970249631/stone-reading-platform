<template>
  <div class="book-detail">
    <header class="header">
      <div class="container">
        <div class="logo">砾石阅读</div>
        <nav class="nav">
          <router-link to="/">首页</router-link>
          <router-link to="/bookshelf" v-if="userStore.isLoggedIn()">书架</router-link>
          <router-link to="/search">搜索</router-link>
        </nav>
      </div>
    </header>

    <main class="main" v-if="bookInfo.id">
      <div class="book-info">
        <div class="cover">
          <img :src="bookInfo.coverUrl || '/placeholder.png'" :alt="bookInfo.title" />
        </div>
        <div class="info">
          <h1>{{ bookInfo.title }}</h1>
          <p class="author">作者：{{ bookInfo.authorName }}</p>
          <div class="tags">
            <el-tag size="small">{{ bookInfo.category }}</el-tag>
            <el-tag v-if="bookInfo.isPaid" size="small" type="warning">付费</el-tag>
            <el-tag v-else size="small" type="success">免费</el-tag>
            <span class="stats">{{ bookInfo.viewCount || 0 }}阅读 | {{ bookInfo.likeCount || 0 }}收藏</span>
          </div>
          <p class="desc">{{ bookInfo.description }}</p>
          <div class="actions">
            <el-button type="primary" size="large" @click="startReading">开始阅读</el-button>
            <el-button size="large" @click="toggleBookshelf">
              {{ isInShelf ? '移出书架' : '加入书架' }}
            </el-button>
            <el-button size="large" @click="likeBook">点赞</el-button>
          </div>
        </div>
      </div>

      <div class="chapter-section">
        <div class="section-header">
          <h2>目录</h2>
          <span class="chapter-count">共{{ chapterList.length }}章</span>
        </div>
        <div class="chapter-list">
          <div
            v-for="chapter in chapterList"
            :key="chapter.id"
            class="chapter-item"
            @click="readChapter(chapter.id)"
          >
            <span class="chapter-num">第{{ chapter.chapterNum }}章</span>
            <span class="chapter-title">{{ chapter.title }}</span>
            <span v-if="chapter.isVip && !chapter.isFree" class="vip-tag">VIP</span>
            <span class="word-count">{{ chapter.wordCount || 0 }}字</span>
          </div>
        </div>
      </div>
    </main>

    <div v-else class="loading">
      <el-skeleton :rows="10" animated />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getBookDetail, getChapterList, addToBookshelf, removeFromBookshelf } from '@/api/book'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const bookInfo = ref({})
const chapterList = ref([])
const isInShelf = ref(false)

const loadBookDetail = async () => {
  try {
    const res = await getBookDetail(route.params.id)
    bookInfo.value = res.data
  } catch (e) {
    ElMessage.error('加载书籍详情失败')
  }
}

const loadChapterList = async () => {
  try {
    const res = await getChapterList(route.params.id)
    chapterList.value = res.data || []
  } catch (e) {
    console.error(e)
  }
}

const startReading = () => {
  if (!chapterList.value.length) {
    ElMessage.warning('暂无章节')
    return
  }

  const firstChapter = chapterList.value[0]
  router.push(`/read/${route.params.id}/${firstChapter.id}`)
}

const readChapter = (chapterId) => {
  router.push(`/read/${route.params.id}/${chapterId}`)
}

const toggleBookshelf = async () => {
  if (!userStore.isLoggedIn()) {
    router.push('/login')
    return
  }

  try {
    if (isInShelf.value) {
      await removeFromBookshelf(route.params.id)
      isInShelf.value = false
      ElMessage.success('已移出书架')
    } else {
      await addToBookshelf(route.params.id)
      isInShelf.value = true
      ElMessage.success('已加入书架')
    }
  } catch (e) {
    ElMessage.error(e.message || '操作失败')
  }
}

const likeBook = () => {
  ElMessage.success('点赞成功')
}

onMounted(async () => {
  await loadBookDetail()
  await loadChapterList()
})
</script>

<style lang="scss" scoped>
.book-detail {
  min-height: 100vh;
  background: #f5f5f5;
}

.header {
  background: white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 100;

  .container {
    max-width: 1200px;
    margin: 0 auto;
    display: flex;
    align-items: center;
    height: 64px;
    padding: 0 20px;
  }

  .logo {
    font-size: 24px;
    font-weight: bold;
    color: #409eff;
    margin-right: 40px;
  }

  .nav {
    flex: 1;
    display: flex;
    gap: 24px;

    a {
      color: #333;
      text-decoration: none;

      &:hover {
        color: #409eff;
      }
    }
  }
}

.main {
  max-width: 1200px;
  margin: 0 auto;
  padding: 40px 20px;
}

.book-info {
  display: flex;
  gap: 40px;
  background: white;
  padding: 40px;
  border-radius: 12px;
  margin-bottom: 30px;

  .cover {
    width: 200px;
    height: 280px;
    border-radius: 8px;
    overflow: hidden;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);

    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }
  }

  .info {
    flex: 1;

    h1 {
      font-size: 32px;
      margin-bottom: 16px;
    }

    .author {
      font-size: 16px;
      color: #666;
      margin-bottom: 16px;
    }

    .tags {
      display: flex;
      align-items: center;
      gap: 12px;
      margin-bottom: 20px;

      .stats {
        color: #999;
        font-size: 14px;
      }
    }

    .desc {
      font-size: 16px;
      color: #666;
      line-height: 1.8;
      margin-bottom: 30px;
    }

    .actions {
      display: flex;
      gap: 16px;
    }
  }
}

.chapter-section {
  background: white;
  padding: 30px;
  border-radius: 12px;

  .section-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
    padding-bottom: 16px;
    border-bottom: 1px solid #eee;

    h2 {
      font-size: 20px;
    }

    .chapter-count {
      color: #999;
    }
  }
}

.chapter-list {
  .chapter-item {
    display: flex;
    align-items: center;
    padding: 14px 0;
    border-bottom: 1px solid #f5f5f5;
    cursor: pointer;
    transition: background 0.2s;

    &:hover {
      background: #f9f9f9;
    }

    .chapter-num {
      width: 80px;
      color: #999;
      font-size: 14px;
    }

    .chapter-title {
      flex: 1;
      font-size: 15px;
    }

    .vip-tag {
      background: linear-gradient(135deg, #ffd700, #ff8c00);
      color: white;
      padding: 2px 8px;
      border-radius: 4px;
      font-size: 12px;
      margin-right: 12px;
    }

    .word-count {
      color: #999;
      font-size: 14px;
      min-width: 80px;
      text-align: right;
    }
  }
}

.loading {
  max-width: 1200px;
  margin: 40px auto;
  padding: 0 20px;
}
</style>