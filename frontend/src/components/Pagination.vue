<template>
  <div class="pagination">
    <el-pagination
      v-model:current-page="pageNum"
      :page-size="pageSize"
      :total="total"
      :layout="layout"
      @current-change="handleChange"
    />
  </div>
</template>

<script setup>
import { ref } from 'vue'

const props = defineProps({
  currentPage: {
    type: Number,
    default: 1
  },
  pageSize: {
    type: Number,
    default: 20
  },
  total: {
    type: Number,
    default: 0
  },
  layout: {
    type: String,
    default: 'prev, pager, next'
  }
})

const emit = defineEmits(['change'])

const pageNum = ref(props.currentPage)

const handleChange = (val) => {
  pageNum.value = val
  emit('change', val)
}
</script>

<style lang="scss" scoped>
.pagination {
  display: flex;
  justify-content: center;
  margin-top: 30px;
}
</style>
