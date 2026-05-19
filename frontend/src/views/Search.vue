<template>
  <div class="search-page">
    <header class="header">
      <div class="container">
        <router-link to="/" class="logo">砾石阅读</router-link>
        <div class="search-box">
          <el-input
            v-model="keyword"
            placeholder="搜索书名、作者"
            size="large"
            @keyup.enter="handleSearch"
          >
            <template #append>
              <el-button :icon="Search" @click="handleSearch" />
            </template>
          </el-input>
        </div>
      </div>
    </header>

    <main class="main">
      <div class="filters">
        <el-select v-model="category" placeholder="全部分类" clearable @change="handleSearch">
          <el-option label="文学" value="文学" />
          <el-option label="科幻" value="科幻" />
          <el-option label="玄幻" value="玄幻" />
          <el-option label="武侠" value="武侠" />
          <el-option label="悬疑" value="悬疑" />
        </el-select>
      </div>

      <div class="results" v-if="results.length">
        <div class="book-list">
          <div v-for="book in results" :key="book.id" class="book-item" @click="goToBook(book.id)">
            <div class="cover">
              <img :src="book.coverUrl || '/placeholder.png'" :alt="book.title" />
            </div>
            <div class="info">
              <h3>{{ book.title }}</h3>
              <p class="author">{{ book.authorName }}</p>
              <p class="desc">{{ book.description }}</p>
              <div class="tags">
                <el-tag size="small">{{ book.category }}</el-tag>
                <span>{{ book.viewCount || 0 }}阅读</span>
              </div>
            </div>
          </div>
        </div>

        <div class="pagination">
          <el-pagination
            v-model:current-page="pageNum"
            :page-size="pageSize"
            :total="total"
            layout="prev, pager, next"
            @current-change="handleSearch"
          />
        </div>
      </div>

      <el-empty v-else-if="searched" description="未找到相关书籍" />
      <el-empty v-else description="输入关键词搜索书籍" />
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { Search } from '@element-plus/icons-vue'
import { getBookList } from '@/api/book'

const router = useRouter()
const route = useRoute()

const keyword = ref('')
const category = ref('')
const results = ref([])
const pageNum = ref(1)
const pageSize = ref(20)
const total = ref(0)
const searched = ref(false)

const handleSearch = async () => {
  if (!keyword.value.trim() && !category.value) return

  try {
    const res = await getBookList({
      keyword: keyword.value,
      category: category.value,
      pageNum: pageNum.value,
      pageSize: pageSize.value
    })
    results.value = res.data.records || res.data || []
    total.value = res.data.total || 0
    searched.value = true
  } catch (e) {
    console.error(e)
  }
}

const goToBook = (id) => {
  router.push(`/book/${id}`)
}

onMounted(() => {
  if (route.query.k) {
    keyword.value = route.query.k
    handleSearch()
  }
})
</script>

<style lang="scss" scoped>
.search-page {
  min-height: 100vh;
  background: #f5f5f5;
}

.header {
  background: white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);

  .container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 20px;
    display: flex;
    align-items: center;
    height: 80px;
    gap: 40px;
  }

  .logo {
    font-size: 28px;
    font-weight: bold;
    color: #409eff;
    text-decoration: none;
  }

  .search-box {
    flex: 1;
    max-width: 600px;
  }
}

.main {
  max-width: 1200px;
  margin: 0 auto;
  padding: 30px 20px;
}

.filters {
  margin-bottom: 20px;
}

.book-list {
  .book-item {
    display: flex;
    gap: 20px;
    background: white;
    padding: 20px;
    border-radius: 12px;
    margin-bottom: 16px;
    cursor: pointer;
    transition: box-shadow 0.3s;

    &:hover {
      box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
    }

    .cover {
      width: 100px;
      height: 140px;
      border-radius: 8px;
      overflow: hidden;
      flex-shrink: 0;

      img {
        width: 100%;
        height: 100%;
        object-fit: cover;
      }
    }

    .info {
      flex: 1;

      h3 {
        font-size: 20px;
        margin-bottom: 8px;
      }

      .author {
        color: #999;
        margin-bottom: 12px;
      }

      .desc {
        color: #666;
        font-size: 14px;
        line-height: 1.6;
        margin-bottom: 12px;
        display: -webkit-box;
        -webkit-line-clamp: 2;
        -webkit-box-orient: vertical;
        overflow: hidden;
      }

      .tags {
        display: flex;
        align-items: center;
        gap: 12px;

        span {
          color: #999;
          font-size: 14px;
        }
      }
    }
  }
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 30px;
}
</style>