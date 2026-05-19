<template>
  <div class="author-center">
    <el-container>
      <el-aside width="240px" class="sidebar">
        <el-menu :default-active="activeTab" @select="handleTabSelect" class="sidebar-menu">
          <el-menu-item index="dashboard">
            <el-icon><trend-charts /></el-icon>
            <span>数据看板</span>
          </el-menu-item>
          <el-menu-item index="profile">
            <el-icon><user /></el-icon>
            <span>作家认证</span>
          </el-menu-item>
          <el-menu-item index="my-books">
            <el-icon><document /></el-icon>
            <span>我的作品</span>
          </el-menu-item>
          <el-menu-item index="earnings">
            <el-icon><money /></el-icon>
            <span>收益中心</span>
          </el-menu-item>
        </el-menu>
      </el-aside>

      <el-main class="main-content">
        <div v-if="activeTab === 'dashboard'" class="dashboard">
          <h3>📊 数据看板</h3>
          
          <el-row :gutter="20" class="stats-row">
            <el-col :xs="24" :sm="12" :md="6">
              <el-card shadow="hover" class="stat-card">
                <div class="stat-value">{{ dashboard.bookCount || 0 }}</div>
                <div class="stat-label">发布书籍</div>
              </el-card>
            </el-col>
            <el-col :xs="24" :sm="12" :md="6">
              <el-card shadow="hover" class="stat-card">
                <div class="stat-value">{{ dashboard.totalWordCount || 0 }}</div>
                <div class="stat-label">总字数</div>
              </el-card>
            </el-col>
            <el-col :xs="24" :sm="12" :md="6">
              <el-card shadow="hover" class="stat-card">
                <div class="stat-value">¥{{ dashboard.totalEarnings || '0.00' }}</div>
                <div class="stat-label">累计收益</div>
              </el-card>
            </el-col>
            <el-col :xs="24" :sm="12" :md="6">
              <el-card shadow="hover" class="stat-card">
                <div class="stat-value">{{ todayReads }}</div>
                <div class="stat-label">今日阅读</div>
              </el-card>
            </el-col>
          </el-row>
        </div>

        <div v-if="activeTab === 'profile'" class="profile-section">
          <h3>✍️ 作家认证</h3>
          
          <el-card>
            <el-form :model="profileForm" label-width="100px">
              <el-form-item label="笔名">
                <el-input v-model="profileForm.penName" placeholder="请输入笔名" />
              </el-form-item>
              <el-form-item label="真实姓名">
                <el-input v-model="profileForm.realName" placeholder="请输入真实姓名" />
              </el-form-item>
              <el-form-item label="身份证号">
                <el-input v-model="profileForm.idCard" placeholder="请输入身份证号" />
              </el-form-item>
              <el-form-item label="个人简介">
                <el-input v-model="profileForm.bio" type="textarea" :rows="4" placeholder="请输入个人简介" />
              </el-form-item>
              <el-form-item label="擅长领域">
                <el-input v-model="profileForm.skills" placeholder="请输入擅长领域" />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="submitApply">提交认证</el-button>
              </el-form-item>
            </el-form>

            <div v-if="profile" class="verify-status">
              <el-tag v-if="profile.verifyStatus === 1" type="success">已认证</el-tag>
              <el-tag v-else-if="profile.verifyStatus === 0" type="info">审核中</el-tag>
              <el-tag v-else type="danger">认证失败</el-tag>
              <p v-if="profile.verifyReason" class="reason">审核说明: {{ profile.verifyReason }}</p>
            </div>
          </el-card>
        </div>

        <div v-if="activeTab === 'my-books'" class="my-books">
          <div class="section-header">
            <h3>📖 我的作品</h3>
            <el-button type="primary" @click="showBookDialog = true">
              <el-icon><plus /></el-icon>
              发布新书
            </el-button>
          </div>

          <el-empty description="暂无作品，快去发布你的第一本书吧" />
        </div>

        <div v-if="activeTab === 'earnings'" class="earnings">
          <h3>💰 收益中心</h3>
          <el-card>
            <el-empty description="暂无收益数据" />
          </el-card>
        </div>
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import * as authorApi from '../api/author'
import { TrendCharts, User, Document, Money, Plus } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const activeTab = ref('dashboard')
const dashboard = ref({})
const profile = ref(null)
const todayReads = ref(0)
const showBookDialog = ref(false)

const profileForm = ref({
  penName: '',
  realName: '',
  idCard: '',
  bio: '',
  skills: ''
})

const handleTabSelect = (key) => {
  activeTab.value = key
  if (key === 'dashboard') loadDashboard()
  if (key === 'profile') loadProfile()
}

const loadDashboard = async () => {
  try {
    const res = await authorApi.getAuthorDashboard()
    if (res.success) {
      dashboard.value = res.data.stats || {}
      profile.value = res.data.profile || null
      todayReads.value = res.data.stats.todayReads || 0
    }
  } catch (e) {
    console.error(e)
  }
}

const loadProfile = async () => {
  try {
    const res = await authorApi.getAuthorProfile()
    if (res.success) {
      profile.value = res.data
      if (res.data) {
        profileForm.value = { ...res.data }
      }
    }
  } catch (e) {
    console.error(e)
  }
}

const submitApply = async () => {
  try {
    const res = await authorApi.applyAuthor(profileForm.value)
    if (res.success) {
      ElMessage.success(res.data.message)
      loadProfile()
    }
  } catch (e) {
    console.error(e)
  }
}

onMounted(() => {
  loadDashboard()
})
</script>

<style lang="scss" scoped>
.author-center {
  min-height: 100vh;
  
  .sidebar {
    background: white;
    border-right: 1px solid #e4e7ed;
    
    .sidebar-menu {
      border-right: none;
    }
  }
  
  .main-content {
    background: #f5f7fa;
    padding: 30px;
  }
  
  .stats-row {
    margin-top: 20px;
    
    .stat-card {
      text-align: center;
      
      .stat-value {
        font-size: 32px;
        font-weight: bold;
        color: #409eff;
      }
      
      .stat-label {
        color: #909399;
        margin-top: 8px;
      }
    }
  }
  
  .section-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
  }
  
  .verify-status {
    margin-top: 20px;
    padding-top: 20px;
    border-top: 1px solid #ebeef5;
    
    .reason {
      margin-top: 8px;
      color: #909399;
    }
  }
}
</style>
