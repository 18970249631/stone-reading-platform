<template>
  <div class="market-detail">
    <el-container>
      <el-main>
        <div v-if="product" class="product-detail">
          <el-page-header @back="goBack" :content="product.title"></el-page-header>
          
          <el-row :gutter="40" class="detail-row">
            <el-col :span="10">
              <div class="product-cover">
                <img :src="product.coverUrl || '/placeholder.png'" :alt="product.title">
              </div>
            </el-col>
            
            <el-col :span="14">
              <h1 class="product-title">{{ product.title }}</h1>
              
              <div class="product-meta">
                <el-tag type="info">{{ product.productType === 'SCRIPT' ? '剧本' : '书籍' }}</el-tag>
                <el-tag :type="product.transactionType === 'BUYOUT' ? 'danger' : 'warning'">
                  {{ product.transactionType === 'BUYOUT' ? '买断' : '分成' }}
                </el-tag>
              </div>
              
              <div class="seller-info">
                <el-avatar :size="40" :src="product.sellerAvatar"></el-avatar>
                <div class="info">
                  <div class="name">{{ product.sellerName }}</div>
                  <div class="desc">卖家</div>
                </div>
              </div>
              
              <p class="product-desc">{{ product.description }}</p>
              
              <div class="product-tags">
                <el-tag v-for="tag in productTags" :key="tag" size="small">{{ tag }}</el-tag>
              </div>
              
              <div class="price-section">
                <div class="price">¥{{ product.price }}</div>
                <el-button type="primary" size="large" @click="handleBuy">立即购买</el-button>
              </div>
              
              <div class="stats-info">
                <div class="stat">
                  <span class="label">浏览量</span>
                  <span class="value">{{ product.viewCount }}</span>
                </div>
                <div class="stat">
                  <span class="label">成交量</span>
                  <span class="value">{{ product.salesCount }}</span>
                </div>
              </div>
            </el-col>
          </el-row>
        </div>
        
        <div v-else class="loading">
          <el-skeleton :rows="10" animated />
        </div>
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import * as marketApi from '../api/market'
import { ElMessage, ElMessageBox } from 'element-plus'

const route = useRoute()
const router = useRouter()
const product = ref(null)

const productTags = computed(() => {
  if (!product.value || !product.value.tags) return []
  return product.value.tags.split(',')
})

const loadProduct = async () => {
  try {
    const res = await marketApi.getProductDetail(route.params.id)
    if (res.success) {
      product.value = res.data
    }
  } catch (e) {
    console.error(e)
  }
}

const handleBuy = async () => {
  try {
    await ElMessageBox.confirm(`确定要购买《${product.value.title}》吗？`, '确认购买', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const res = await marketApi.buyProduct(product.value.id)
    if (res.success) {
      ElMessage.success('购买成功')
    }
  } catch (e) {
    if (e !== 'cancel') {
      console.error(e)
    }
  }
}

const goBack = () => {
  router.back()
}

onMounted(() => {
  loadProduct()
})
</script>

<style lang="scss" scoped>
.market-detail {
  min-height: 100vh;
  background: #f5f7fa;
  
  .detail-row {
    margin-top: 30px;
    
    .product-cover {
      border-radius: 8px;
      overflow: hidden;
      box-shadow: 0 4px 16px rgba(0,0,0,0.08);
      
      img {
        width: 100%;
        display: block;
      }
    }
    
    .product-title {
      font-size: 28px;
      margin-bottom: 20px;
    }
    
    .product-meta {
      display: flex;
      gap: 10px;
      margin-bottom: 20px;
    }
    
    .seller-info {
      display: flex;
      align-items: center;
      gap: 12px;
      margin-bottom: 24px;
      
      .info {
        .name {
          font-weight: bold;
        }
        .desc {
          font-size: 12px;
          color: #909399;
        }
      }
    }
    
    .product-desc {
      color: #606266;
      line-height: 1.8;
      margin-bottom: 24px;
    }
    
    .product-tags {
      display: flex;
      gap: 8px;
      margin-bottom: 30px;
    }
    
    .price-section {
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: 20px;
      background: white;
      border-radius: 8px;
      box-shadow: 0 2px 8px rgba(0,0,0,0.05);
      
      .price {
        font-size: 32px;
        font-weight: bold;
        color: #f56c6c;
      }
    }
    
    .stats-info {
      display: flex;
      gap: 40px;
      margin-top: 30px;
      
      .stat {
        .label {
          display: block;
          color: #909399;
          font-size: 13px;
        }
        .value {
          display: block;
          font-size: 20px;
          font-weight: bold;
          margin-top: 4px;
        }
      }
    }
  }
}
</style>
