<template>
  <div class="book-card" @click="goToBook">
    <div class="cover">
      <img :src="book.coverUrl || '/placeholder.png'" :alt="book.title" />
      <span v-if="book.isPaid" class="vip-badge">VIP</span>
    </div>
    <div class="info">
      <h3>{{ book.title }}</h3>
      <p class="author">{{ book.authorName }}</p>
      <p class="desc">{{ book.description }}</p>
      <div class="tags">
        <el-tag size="small">{{ book.category }}</el-tag>
        <span class="views">{{ formatViews(book.viewCount) }}阅读</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

defineProps({
  book: {
    type: Object,
    required: true
  }
})

const formatViews = (count) => {
  if (!count) return '0'
  if (count >= 10000) return (count / 10000).toFixed(1) + '万'
  return count
}

const goToBook = () => {
  router.push(`/book/${book.id}`)
}
</script>

<style lang="scss" scoped>
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

    .desc {
      font-size: 14px;
      color: #666;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
      overflow: hidden;
      height: 40px;
    }

    .tags {
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
</style>
