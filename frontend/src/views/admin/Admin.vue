<template>
  <div class="admin-dashboard">
    <el-container>
      <el-aside width="240px" class="sidebar">
        <div class="logo">砾石阅读管理后台</div>
        <el-menu :default-active="activeTab" @select="handleTabSelect">
          <el-menu-item index="review">
            <el-icon><document-check /></el-icon>
            <span>内容审核</span>
          </el-menu-item>
          <el-menu-item index="users">
            <el-icon><user /></el-icon>
            <span>用户管理</span>
          </el-menu-item>
          <el-menu-item index="books">
            <el-icon><notebook /></el-icon>
            <span>书籍管理</span>
          </el-menu-item>
          <el-menu-item index="market">
            <el-icon><shopping-cart /></el-icon>
            <span>交易管理</span>
          </el-menu-item>
          <el-menu-item index="stats">
            <el-icon><trend-charts /></el-icon>
            <span>数据大屏</span>
          </el-menu-item>
        </el-menu>
      </el-aside>

      <el-main class="main-content">
        <div v-if="activeTab === 'review'" class="review-section">
          <h3>🔍 内容审核</h3>
          
          <el-row :gutter="20" class="filter-row">
            <el-col :span="6">
              <el-select v-model="reviewFilter.contentType" placeholder="内容类型" clearable style="width:100%">
                <el-option label="全部" value="" />
                <el-option label="书籍" value="BOOK" />
                <el-option label="章节" value="CHAPTER" />
                <el-option label="剧本" value="SCRIPT" />
              </el-select>
            </el-col>
            <el-col :span="6">
              <el-select v-model="reviewFilter.status" placeholder="审核状态" clearable style="width:100%">
                <el-option label="待审核" value="PENDING" />
                <el-option label="AI已过" value="AI_PASS" />
                <el-option label="AI可疑" value="AI_SUSPECT" />
              </el-select>
            </el-col>
          </el-row>

          <el-table :data="reviewRecords" style="width:100%;margin-top:20px">
            <el-table-column prop="contentType" label="类型" width="100">
              <template #default="scope">
                <el-tag v-if="scope.row.contentType === 'BOOK'" type="info">书籍</el-tag>
                <el-tag v-else-if="scope.row.contentType === 'CHAPTER'" type="warning">章节</el-tag>
                <el-tag v-else type="danger">剧本</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="contentId" label="内容ID" width="100" />
            <el-table-column prop="reviewLevel" label="审核层级" width="100">
              <template #default="scope">
                <el-tag :type="scope.row.reviewLevel === 1 ? 'success' : scope.row.reviewLevel === 2 ? 'warning' : 'danger'">
                  {{ scope.row.reviewLevel === 1 ? 'AI初审' : scope.row.reviewLevel === 2 ? 'AI复核' : '人工' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createdAt" label="提交时间" width="180" />
            <el-table-column label="操作">
              <template #default="scope">
                <el-button type="primary" size="small" @click="handleReview(scope.row, 'PASS')">通过</el-button>
                <el-button type="danger" size="small" @click="handleReview(scope.row, 'REJECT')">拒绝</el-button>
              </template>
            </el-table-column>
          </el-table>

          <div class="pagination">
            <el-pagination 
              v-model:current-page="reviewPage" 
              :page-size="pageSize" 
              :total="reviewTotal"
              layout="prev, pager, next"
            />
          </div>
        </div>

        <div v-if="activeTab === 'users'" class="users-section">
          <h3>👥 用户管理</h3>
          <el-empty description="用户管理功能开发中" />
        </div>

        <div v-if="activeTab === 'books'" class="books-section">
          <h3>📖 书籍管理</h3>
          <el-empty description="书籍管理功能开发中" />
        </div>

        <div v-if="activeTab === 'market'" class="market-section">
          <h3>🛒 交易管理</h3>
          <el-empty description="交易管理功能开发中" />
        </div>

        <div v-if="activeTab === 'stats'" class="stats-section">
          <h3>📊 数据大屏</h3>
          
          <el-row :gutter="20" class="stats-cards">
            <el-col :span="6">
              <el-card shadow="hover" class="stat-card">
                <div class="stat-icon users-icon">👥</div>
                <div class="stat-content">
                  <div class="stat-value">{{ dashboard.totalUsers || 0 }}</div>
                  <div class="stat-label">总用户数</div>
                </div>
              </el-card>
            </el-col>
            <el-col :span="6">
              <el-card shadow="hover" class="stat-card">
                <div class="stat-icon books-icon">📚</div>
                <div class="stat-content">
                  <div class="stat-value">{{ dashboard.totalBooks || 0 }}</div>
                  <div class="stat-label">书籍数量</div>
                </div>
              </el-card>
            </el-col>
            <el-col :span="6">
              <el-card shadow="hover" class="stat-card">
                <div class="stat-icon revenue-icon">💰</div>
                <div class="stat-content">
                  <div class="stat-value">¥{{ dashboard.todayRevenue || '0.00' }}</div>
                  <div class="stat-label">今日流水</div>
                </div>
              </el-card>
            </el-col>
            <el-col :span="6">
              <el-card shadow="hover" class="stat-card">
                <div class="stat-icon review-icon">🔍</div>
                <div class="stat-content">
                  <div class="stat-value">{{ dashboard.pendingReviews || 0 }}</div>
                  <div class="stat-label">待审核</div>
                </div>
              </el-card>
            </el-col>
          </el-row>

          <el-row :gutter="20" class="charts-row">
            <el-col :span="12">
              <el-card title="用户分布" shadow="hover">
                <div class="chart-container">
                  <el-progress type="circle" :percentage="userStats.vipPercent || 0" :size="120" />
                  <div class="progress-info">
                    <div class="progress-item">
                      <span class="vip">VIP用户</span>
                      <span>{{ userStats.vip || 0 }}</span>
                    </div>
                    <div class="progress-item">
                      <span class="author">作家</span>
                      <span>{{ userStats.authors || 0 }}</span>
                    </div>
                    <div class="progress-item">
                      <span class="normal">普通用户</span>
                      <span>{{ userStats.normal || 0 }}</span>
                    </div>
                  </div>
                </div>
              </el-card>
            </el-col>
            <el-col :span="12">
              <el-card title="书籍统计" shadow="hover">
                <div class="chart-container">
                  <el-progress type="circle" :percentage="bookStats.freePercent || 0" :size="120" />
                  <div class="progress-info">
                    <div class="progress-item">
                      <span class="free">免费书</span>
                      <span>{{ bookStats.free || 0 }}</span>
                    </div>
                    <div class="progress-item">
                      <span class="paid">付费书</span>
                      <span>{{ bookStats.paid || 0 }}</span>
                    </div>
                    <div class="progress-item">
                      <span class="total">总字数</span>
                      <span>{{ formatWords(bookStats.totalWords) }}</span>
                    </div>
                  </div>
                </div>
              </el-card>
            </el-col>
          </el-row>

          <el-row :gutter="20" class="charts-row">
            <el-col :span="12">
              <el-card title="审核统计" shadow="hover">
                <div class="review-stats">
                  <div class="review-item pass">
                    <div class="review-value">{{ reviewStats.pass || 0 }}</div>
                    <div class="review-label">通过</div>
                  </div>
                  <div class="review-item reject">
                    <div class="review-value">{{ reviewStats.reject || 0 }}</div>
                    <div class="review-label">拒绝</div>
                  </div>
                  <div class="review-item pending">
                    <div class="review-value">{{ reviewStats.pending || 0 }}</div>
                    <div class="review-label">待审核</div>
                  </div>
                </div>
              </el-card>
            </el-col>
            <el-col :span="12">
              <el-card title="收益统计" shadow="hover">
                <div class="revenue-stats">
                  <div class="revenue-item">
                    <div class="revenue-label">总收入</div>
                    <div class="revenue-value">¥{{ revenueStats.totalRevenue || '0.00' }}</div>
                  </div>
                  <div class="revenue-item">
                    <div class="revenue-label">平台收入</div>
                    <div class="revenue-value">¥{{ revenueStats.platformFee || '0.00' }}</div>
                  </div>
                  <div class="revenue-item">
                    <div class="revenue-label">订单数</div>
                    <div class="revenue-value">{{ revenueStats.orderCount || 0 }}</div>
                  </div>
                </div>
              </el-card>
            </el-col>
          </el-row>
        </div>
      </el-main>
    </el-container>
  </div>
</template>

<script setup>import { ref, onMounted } from 'vue';
import { DocumentCheck, User, Notebook, ShoppingCart, TrendCharts } from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';
import * as statsApi from '../api/stats';
const activeTab = ref('stats');
const reviewFilter = ref({
 contentType: '',
 status: ''
});
const reviewPage = ref(1);
const pageSize = ref(10);
const reviewTotal = ref(0);
const reviewRecords = ref([
 { id: 1, contentType: 'BOOK', contentId: 101, reviewLevel: 1, createdAt: '2024-01-15 10:30', reviewResult: null },
 { id: 2, contentType: 'CHAPTER', contentId: 202, reviewLevel: 2, createdAt: '2024-01-15 11:20', reviewResult: null },
 { id: 3, contentType: 'SCRIPT', contentId: 303, reviewLevel: 1, createdAt: '2024-01-15 12:10', reviewResult: null }
]);
const dashboard = ref({});
const userStats = ref({});
const bookStats = ref({});
const revenueStats = ref({});
const reviewStats = ref({});
const handleTabSelect = (key) => {
 activeTab.value = key;
 if (key === 'stats') {
 loadStats();
 }
};
const handleReview = (record, result) => {
 ElMessage.success(result === 'PASS' ? '内容已通过审核' : '内容已拒绝');
};
const loadStats = async () => {
 try {
 const [dashboardRes, userRes, bookRes, revenueRes, reviewRes] = await Promise.all([
 statsApi.getDashboardStats(),
 statsApi.getUserStats(),
 statsApi.getBookStats(),
 statsApi.getRevenueStats('month'),
 statsApi.getReviewStats()
 ]);
 if (dashboardRes.success) {
 dashboard.value = dashboardRes.data;
 }
 if (userRes.success) {
 userStats.value = userRes.data;
 const total = userStats.value.total || 1;
 userStats.value.vipPercent = Math.round((userStats.value.vip || 0) / total * 100);
 }
 if (bookRes.success) {
 bookStats.value = bookRes.data;
 const total = (bookStats.value.free || 0) + (bookStats.value.paid || 0);
 bookStats.value.freePercent = total > 0 ? Math.round((bookStats.value.free || 0) / total * 100) : 0;
 }
 if (revenueRes.success) {
 revenueStats.value = revenueRes.data;
 }
 if (reviewRes.success) {
 reviewStats.value = reviewRes.data;
 }
 }
 catch (e) {
 console.error('加载统计数据失败', e);
 }
};
const formatWords = (words) => {
 if (!words)
 return '0';
 if (words >= 10000)
 return (words / 10000).toFixed(1) + '万';
 return words;
};
onMounted(() => {
 loadStats();
});
</script>

<style lang="scss" scoped>
.admin-dashboard {
  min-height: 100vh;
  
  .sidebar {
    background: #304156;
    
    .logo {
      color: white;
      font-size: 18px;
      font-weight: bold;
      padding: 20px;
      text-align: center;
      border-bottom: 1px solid #1f2d3d;
    }
    
    .el-menu {
      border-right: none;
      background: #304156;
      color: #bfcbd9;
    }
  }
  
  .main-content {
    background: #f0f2f5;
    padding: 30px;
    
    .filter-row {
      margin-bottom: 20px;
    }
    
    .pagination {
      display: flex;
      justify-content: center;
      margin-top: 30px;
    }
    
    .stats-cards {
      margin-bottom: 20px;
      
      .stat-card {
        display: flex;
        align-items: center;
        padding: 20px;
        
        .stat-icon {
          font-size: 48px;
          margin-right: 20px;
        }
        
        .stat-content {
          .stat-value {
            font-size: 32px;
            font-weight: bold;
            color: #409eff;
          }
          
          .stat-label {
            color: #909399;
            margin-top: 4px;
          }
        }
      }
    }
    
    .charts-row {
      margin-bottom: 20px;
      
      .chart-container {
        display: flex;
        align-items: center;
        justify-content: center;
        gap: 30px;
        
        .progress-info {
          .progress-item {
            display: flex;
            justify-content: space-between;
            gap: 30px;
            margin-bottom: 10px;
            
            .vip { color: #409eff; }
            .author { color: #67c23a; }
            .normal { color: #909399; }
            .free { color: #67c23a; }
            .paid { color: #f56c6c; }
            .total { color: #409eff; }
          }
        }
      }
      
      .review-stats {
        display: flex;
        justify-content: space-around;
        
        .review-item {
          text-align: center;
          
          .review-value {
            font-size: 36px;
            font-weight: bold;
          }
          
          .review-label {
            color: #909399;
            margin-top: 8px;
          }
          
          &.pass .review-value { color: #67c23a; }
          &.reject .review-value { color: #f56c6c; }
          &.pending .review-value { color: #e6a23c; }
        }
      }
      
      .revenue-stats {
        .revenue-item {
          margin-bottom: 20px;
          
          .revenue-label {
            color: #909399;
            margin-bottom: 4px;
          }
          
          .revenue-value {
            font-size: 24px;
            font-weight: bold;
            color: #409eff;
          }
        }
      }
    }
  }
}
</style>
