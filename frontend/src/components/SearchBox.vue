<template>
  <div class="search-box">
    <el-input
      v-model="keyword"
      :placeholder="placeholder"
      :size="size"
      @keyup.enter="handleSearch"
    >
      <template #append>
        <el-button :icon="Search" @click="handleSearch" />
      </template>
    </el-input>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { Search } from '@element-plus/icons-vue'

const router = useRouter()

defineProps({
  placeholder: {
    type: String,
    default: '搜索书名、作者'
  },
  size: {
    type: String,
    default: 'large'
  }
})

const keyword = ref('')

const handleSearch = () => {
  if (!keyword.value.trim()) return
  router.push(`/search?k=${encodeURIComponent(keyword.value)}`)
}
</script>

<style lang="scss" scoped>
.search-box {
  width: 100%;
  max-width: 600px;
}
</style>
