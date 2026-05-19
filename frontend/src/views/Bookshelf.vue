<template>
  <div class="bookshelf-page">
    <header class="header">
      <div class="container">
        <div class="logo">砾石阅读</div>
        <nav class="nav">
          <router-link to="/">首页</router-link>
          <router-link to="/bookshelf">书架</router-link>
          <router-link to="/search">搜索</router-link>
        </nav>
      </div>
    </header>

    <main class="main">
      <div class="section-header">
        <h2>我的书架</h2>
        <el-button type="primary" @click="showAddBook = true">添加书籍</el-button>
      </div>

      <div class="book-list" v-if="books.length">
        <div v-for="book in books" :key="book.id" class="book-item">
          <div class="book-cover" @click="goToBook(book.id)">
            <img :src="book.coverUrl || '/placeholder.png'" :alt="book.title" />
          </div>
          <div class="book-info">
            <h3 @click="goToBook(book.id)">{{ book.title }}</h3>
            <p class="author">{{ book.authorName }}</p>
            <div class="progress">
              <el-progress :percentage="getProgress(book.id)" :show-text="false" />
              <span>{{ getProgress(book.id) }}%</span>
            </div>
          </div>
          <div class="actions">
            <el-button type="primary" @click="continueReading(book.id)">继续阅读</el-button>
            <el-button @click="removeFromShelf(book.id)">移出</el-button>
          </div>
        </div>
      </div>

      <el-empty v-else description="书架空空如也，快去添加书籍吧">
        <el-button type="primary" @click="$router.push('/')">去首页</el-button>
      </el-empty>
    </main>

    <el-dialog v-model="showAddBook" title="添加书籍" width="600px">
      <div class="search-box">
        <el-input v-model="searchKeyword" placeholder="搜索书籍名称或作者" @keyup.enter="searchBooks">
          <template #append>
            <el-button :icon="Search" @click="searchBooks">搜索</el-button>
          </template>
        </el-input>
      </div>
      <div class="search-results">
        <div v-for="book in searchResults" :key="book.id" class="search-item">
          <img :src="book.coverUrl || '/placeholder.png'" class="cover" />
          <div class="info">
            <h4>{{ book.title }}</h4>
            <p>{{ book.authorName }}</p>
          </div>
          <el-button size="small" @click="addBook(book.id)">添加</el-button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Search } from '@element-plus/icons-vue'
import { getMyBookshelf, removeFromBookshelf as apiRemoveFromShelf, getBookList, addToBookshelf } from '@/api/book'
import { ElMessage } from 'element-plus'

const router = useRouter()

const books = ref([])
const showAddBook = ref(false)
const searchKeyword = ref('')
const searchResults = ref([])
const readingProgress = ref({})

const loadBookshelf = async () => {
  try {
    const res = await getMyBookshelf()
    books.value = res.data || []

    books.value.forEach(book => {
      readingProgress.value[book.id] = Math.floor(Math.random() * 100)
    })
  } catch (e) {
    console.error(e)
  }
}

const getProgress = (bookId) => {
  return readingProgress.value[bookId] || 0
}

const goToBook = (id) => {
  router.push(`/book/${id}`)
}

const continueReading = async (id) => {
  router.push(`/book/${id}`)
}

const removeFromShelf = async (id) => {
  try {
    await apiRemoveFromShelf(id)
    books.value = books.value.filter(b => b.id !== id)
    ElMessage.success('已移出书架')
  } catch (e) {
    ElMessage.error('操作失败')
  }
}

const searchBooks = async () => {
  if (!searchKeyword.value.trim()) return
  try {
    const res = await getBookList({ keyword: searchKeyword.value, pageNum: 1, pageSize: 10 })
    searchResults.value = res.data.records || res.data || []
  } catch (e) {
    console.error(e)
  }
}

const addBook = async (id) => {
  try {
    await addToBookshelf(id)
    ElMessage.success('添加成功')
    await loadBookshelf()
  } catch (e) {
    ElMessage.error(e.message || '添加失败')
  }
}

onMounted(() => {
  loadBookshelf()
})
</script>

<style lang="scss" scoped>
.bookshelf-page {
  min-height: 100vh;
  background: #f5f5f5;
}

.header {
  background: white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);

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

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;

  h2 {
    font-size: 24px;
  }
}

.book-list {
  .book-item {
    display: flex;
    align-items: center;
    background: white;
    padding: 20px;
    border-radius: 12px;
    margin-bottom: 16px;
    gap: 20px;

    .book-cover {
      width: 80px;
      height: 110px;
      border-radius: 6px;
      overflow: hidden;
      cursor: pointer;

      img {
        width: 100%;
        height: 100%;
        object-fit: cover;
      }
    }

    .book-info {
      flex: 1;

      h3 {
        font-size: 18px;
        margin-bottom: 8px;
        cursor: pointer;

        &:hover {
          color: #409eff;
        }
      }

      .author {
        color: #999;
        font-size: 14px;
        margin-bottom: 12px;
      }

      .progress {
        display: flex;
        align-items: center;
        gap: 12px;

        .el-progress {
          flex: 1;
        }

        span {
          color: #666;
          font-size: 14px;
          min-width: 40px;
        }
      }
    }

    .actions {
      display: flex;
      gap: 12px;
    }
  }
}

.search-box {
  margin-bottom: 20px;
}

.search-results {
  max-height: 400px;
  overflow-y: auto;

  .search-item {
    display: flex;
    align-items: center;
    padding: 12px 0;
    border-bottom: 1px solid #eee;
    gap: 16px;

    .cover {
      width: 50px;
      height: 70px;
      object-fit: cover;
      border-radius: 4px;
    }

    .info {
      flex: 1;

      h4 {
        margin: 0 0 4px;
        font-size: 16px;
      }

      p {
        margin: 0;
        font-size: 14px;
        color: #999;
      }
    }
  }
}
</style>