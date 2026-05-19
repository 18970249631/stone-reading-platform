<template>
  <div class="market-home">
    <el-container>
      <el-header class="page-header">
        <h2>📚 交易区</h2>
        <el-radio-group v-model="productType" @change="loadProducts" size="large">
          <el-radio-button label="">全部</el-radio-button>
          <el-radio-button label="SCRIPT">剧本</el-radio-button>
          <el-radio-button label="BOOK">书籍</el-radio-button>
        </el-radio-group>
      </el-header>
      
      <el-main>
        <el-row :gutter="20">
          <el-col v-for="product in products" :key="product.id" :xs="24" :sm="12" :md="8" :lg="6">
            <el-card class="product-card" shadow="hover" @click="goDetail(product.id)">
              <div class="product-cover">
                <img :src="product.coverUrl || '/placeholder.png'" :alt="product.title">
                <el-tag v-if="product.transactionType === 'BUYOUT'" type="danger" size="small">买断</el-tag>
                <el-tag v-else type="warning" size="small">分成</el-tag>
              </div>
              <div class="product-info">
                <h3>{{ product.title }}</h3>
                <p class="seller">@{{ product.sellerName }}</p>
                <p class="desc">{{ product.description }}</p>
                <div class="meta">
                  <span class="price">¥{{ product.price }}</span>
                  <span class="sales">{{ product.salesCount }}次成交</span>
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>

        <div class="pagination">
          <el-pagination
            v-model:current-page="page"
            :page-size="pageSize"
            :total="total"
            layout="prev, pager, next"
            @current-change="loadProducts"
          />
        </div>
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import * as marketApi from '../api/market'

const router = useRouter()
const products = ref([])
const productType = ref('')
const page = ref(1)
const pageSize = ref(12)
const total = ref(0)

const loadProducts = async () => {
  try {
    const res = await marketApi.getProducts({
      page: page.value,
      size: pageSize.value,
      type: productType.value
    })
    if (res.success) {
      products.value = res.data.records || []
      total.value = res.data.total || 0
    }
  } catch (e) {
    console.error(e)
  }
}

const goDetail = (id) => {
  router.push(`/market/${id}`)
}

onMounted(() => {
  loadProducts()
})
</script>

<style lang="scss" scoped>
.market-home {
  min-height: 100vh;
  background: #f5f7fa;
  
  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    background: white;
    padding: 0 40px;
    box-shadow: 0 2px 8px rgba(0,0,0,0.05);
  }
  
  .product-card {
    margin-bottom: 20px;
    cursor: pointer;
    
    .product-cover {
      height: 180px;
      overflow: hidden;
      border-radius: 8px;
      position: relative;
      
      img {
        width: 100%;
        height: 100%;
        object-fit: cover;
      }
      
      .el-tag {
        position: absolute;
        top: 10px;
        right: 10px;
      }
    }
    
    .product-info {
      margin-top: 12px;
      
      h3 {
        font-size: 16px;
        margin-bottom: 6px;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }
      
      .seller {
        color: #909399;
        font-size: 13px;
        margin-bottom: 8px;
      }
      
      .desc {
        color: #606266;
        font-size: 13px;
        height: 36px;
        overflow: hidden;
        display: -webkit-box;
        -webkit-line-clamp: 2;
        -webkit-box-orient: vertical;
      }
      
      .meta {
        display: flex;
        justify-content: space-between;
        margin-top: 12px;
        
        .price {
          color: #f56c6c;
          font-weight: bold;
          font-size: 18px;
        }
        
        .sales {
          color: #909399;
          font-size: 13px;
        }
      }
    }
  }
  
  .pagination {
    display: flex;
    justify-content: center;
    margin-top: 30px;
  }
}
</style>
