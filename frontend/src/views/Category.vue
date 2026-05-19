<template>
  <div class="category-page">
    <header class="header">
      <div class="container">
        <router-link to="/" class="logo">砾石阅读</router-link>
        <nav class="nav">
          <router-link to="/">首页</router-link>
          <router-link to="/bookshelf" v-if="userStore.isLoggedIn()">书架</router-link>
          <router-link to="/search">搜索</router-link>
        </nav>
      </div>
    </header>

    <main class="main">
      <div class="category-header">
        <h1>{{ categoryName }}</h1>
        <p>共 {{ total }} 本书籍</p>
      </div>

      <div class="book-grid" v-if="books.length">
        <div v-for="book in books" :key="book.id" class="book-card" @click="goToBook(book.id)">
          <div class="cover">
            <img :src="book.coverUrl || '/placeholder.png'" :alt="book.title" />
            <span v-if="book.isPaid" class="vip-badge">VIP</span>
          </div>
          <div class="info">
            <h3>{{ book.title }}</h3>
            <p class="author">{{ book.authorName }}</p>
            <div class="tags">
              <span class="views">{{ book.viewCount || 0 }}阅读</span>
            </div>
          </div>
        </div>
      </div>

      <el-empty v-else description="该分类暂无书籍" />

      <div class="pagination" v-if="total > pageSize">
        <el-pagination
          v-model:current-page="pageNum"
          :page-size="pageSize"
          :total="total"
          layout="prev, pager, next"
          @current-change="loadBooks"
        />
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getBookList } from '@/api/book'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const books = ref([])
const pageNum = ref(1)
const pageSize = ref(20)
const total = ref(0)

const categoryName = computed(() => route.params.name || '全部分类')

const loadBooks = async () => {
  try {
    const res = await getBookList({
      category: categoryName.value,
      pageNum: pageNum.value,
      pageSize: pageSize.value
    })
    books.value = res.data.records || res.data || []
    total.value = res.data.total || 0
  } catch (e) {
    console.error(e)
  }
}

const goToBook = (id) => {
  router.push(`/book/${id}`)
}

watch(() => route.params.name, () => {
  pageNum.value = 1
  loadBooks()
})

onMounted(() => {
  loadBooks()
})
</script>

<style lang="scss" scoped>
.category-page {
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
    text-decoration: none;
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

.category-header {
  margin-bottom: 30px;

  h1 {
    font-size: 32px;
    margin-bottom: 8px;
  }

  p {
    color: #999;
  }
}

.book-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 20px;
}

.book-card {
  background: white;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  transition: transform 0.3s, box-shadow 0.3s;

  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
  }

  .cover {
    position: relative;
    height: 200px;
    background: #eee;

    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }

    .vip-badge {
      position: absolute;
      top: 8px;
      right: 8px;
      background: linear-gradient(135deg, #ffd700, #ff8c00);
      color: white;
      padding: 2px 8px;
      border-radius: 4px;
      font-size: 12px;
      font-weight: bold;
    }
  }

  .info {
    padding: 16px;

    h3 {
      font-size: 16px;
      margin-bottom: 8px;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }

    .author {
      font-size: 14px;
      color: #999;
      margin-bottom: 8px;
    }

    .tags {
      .views {
        font-size: 12px;
        color: #999;
      }
    }
  }
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 30px;
}

@media (max-width: 768px) {
  .book-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>