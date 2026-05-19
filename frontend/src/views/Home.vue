<template>
  <div class="home">
    <header class="header">
      <div class="container">
        <div class="logo">砾石阅读</div>
        <nav class="nav">
          <router-link to="/">首页</router-link>
          <router-link to="/category/文学">文学</router-link>
          <router-link to="/category/科幻">科幻</router-link>
          <router-link to="/category/玄幻">玄幻</router-link>
          <router-link to="/search">搜索</router-link>
        </nav>
        <div class="user-actions">
          <template v-if="userStore.isLoggedIn()">
            <el-dropdown @command="handleCommand">
              <span class="user-name">
                <el-avatar :size="32" :src="userStore.userInfo.avatar">{{ userStore.userInfo.nickname || userStore.userInfo.username }}</el-avatar>
                <span>{{ userStore.userInfo.nickname || userStore.userInfo.username }}</span>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="bookshelf">我的书架</el-dropdown-item>
                  <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                  <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
          <template v-else>
            <el-button @click="$router.push('/login')">登录</el-button>
            <el-button type="primary" @click="$router.push('/register')">注册</el-button>
          </template>
        </div>
      </div>
    </header>

    <main class="main">
      <section class="banner">
        <el-carousel height="400px">
          <el-carousel-item v-for="(item, index) in banners" :key="index">
            <div class="banner-item" :style="{ background: item.color }">
              <h2>{{ item.title }}</h2>
              <p>{{ item.subtitle }}</p>
            </div>
          </el-carousel-item>
        </el-carousel>
      </section>

      <section class="book-section">
        <div class="section-header">
          <h2>免费好书</h2>
          <router-link to="/category/免费">查看更多</router-link>
        </div>
        <div class="book-grid">
          <div v-for="book in freeBooks" :key="book.id" class="book-card" @click="goToBook(book.id)">
            <div class="book-cover">
              <img :src="book.coverUrl || '/placeholder.png'" :alt="book.title" />
            </div>
            <div class="book-info">
              <h3>{{ book.title }}</h3>
              <p class="author">{{ book.authorName }}</p>
              <p class="desc">{{ book.description }}</p>
              <div class="book-tags">
                <el-tag size="small">{{ book.category }}</el-tag>
                <span class="views">{{ formatViews(book.viewCount) }}阅读</span>
              </div>
            </div>
          </div>
        </div>
      </section>

      <section class="book-section">
        <div class="section-header">
          <h2>编辑推荐</h2>
          <router-link to="/category/推荐">查看更多</router-link>
        </div>
        <div class="book-grid">
          <div v-for="book in recommendBooks" :key="book.id" class="book-card" @click="goToBook(book.id)">
            <div class="book-cover">
              <img :src="book.coverUrl || '/placeholder.png'" :alt="book.title" />
              <span v-if="book.isPaid" class="vip-badge">VIP</span>
            </div>
            <div class="book-info">
              <h3>{{ book.title }}</h3>
              <p class="author">{{ book.authorName }}</p>
              <p class="desc">{{ book.description }}</p>
              <div class="book-tags">
                <el-tag size="small" type="warning">{{ book.category }}</el-tag>
                <span class="views">{{ formatViews(book.viewCount) }}阅读</span>
              </div>
            </div>
          </div>
        </div>
      </section>
    </main>

    <footer class="footer">
      <p>&copy; 2024 砾石阅读 - 发现你的阅读世界</p>
    </footer>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getFreeBooks, getRecommendBooks } from '@/api/book'

const router = useRouter()
const userStore = useUserStore()

const freeBooks = ref([])
const recommendBooks = ref([])

const banners = [
  { title: '免费阅读', subtitle: '海量公版书免费读', color: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)' },
  { title: '会员专享', subtitle: '畅享无限阅读', color: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)' },
  { title: '原创小说', subtitle: '发现优质原创内容', color: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)' }
]

const loadBooks = async () => {
  try {
    const [freeRes, recommendRes] = await Promise.all([
      getFreeBooks(8),
      getRecommendBooks(8)
    ])
    freeBooks.value = freeRes.data.records || freeRes.data || []
    recommendBooks.value = recommendRes.data.records || recommendRes.data || []
  } catch (e) {
    console.error('加载书籍失败', e)
  }
}

const formatViews = (count) => {
  if (!count) return '0'
  if (count >= 10000) return (count / 10000).toFixed(1) + '万'
  return count
}

const goToBook = (id) => {
  router.push(`/book/${id}`)
}

const handleCommand = (command) => {
  switch (command) {
    case 'bookshelf':
      router.push('/bookshelf')
      break
    case 'profile':
      router.push('/user/profile')
      break
    case 'logout':
      userStore.logout()
      router.push('/')
      break
  }
}

onMounted(() => {
  loadBooks()
})
</script>

<style lang="scss" scoped>
.home {
  min-height: 100vh;
  background: #f5f5f5;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
}

.header {
  background: white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 100;

  .container {
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
      font-size: 16px;

      &:hover {
        color: #409eff;
      }
    }
  }

  .user-actions {
    display: flex;
    align-items: center;
    gap: 12px;

    .user-name {
      display: flex;
      align-items: center;
      gap: 8px;
      cursor: pointer;
    }
  }
}

.main {
  .banner {
    max-width: 1200px;
    margin: 20px auto;
    padding: 0 20px;

    .banner-item {
      height: 400px;
      display: flex;
      flex-direction: column;
      justify-content: center;
      align-items: center;
      color: white;
      text-align: center;

      h2 {
        font-size: 48px;
        margin-bottom: 16px;
      }

      p {
        font-size: 20px;
      }
    }
  }
}

.book-section {
  max-width: 1200px;
  margin: 40px auto;
  padding: 0 20px;

  .section-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;

    h2 {
      font-size: 24px;
      color: #333;
    }

    a {
      color: #409eff;
      text-decoration: none;
    }
  }
}

.book-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
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

  .book-cover {
    position: relative;
    height: 200px;
    background: #eee;
    overflow: hidden;

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

  .book-info {
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

    .desc {
      font-size: 14px;
      color: #666;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
      overflow: hidden;
      height: 40px;
    }

    .book-tags {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-top: 12px;

      .views {
        font-size: 12px;
        color: #999;
      }
    }
  }
}

.footer {
  background: #333;
  color: white;
  text-align: center;
  padding: 20px;
  margin-top: 60px;
}

@media (max-width: 768px) {
  .book-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>