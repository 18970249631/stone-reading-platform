<template>
  <router-view />
</template>

<script setup>
import { onMounted } from 'vue'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

onMounted(() => {
  userStore.checkLogin()
})
</script>

<style>
#app {
  width: 100%;
  min-height: 100vh;
}
</style>