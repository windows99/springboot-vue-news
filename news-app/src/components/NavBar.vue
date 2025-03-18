<template>
  <el-header class="header-nav">
    <el-menu :default-active="activeIndex" class="el-menu-demo" mode="horizontal" :ellipsis="false"
      @select="handleSelect" active-text-color="#409EFF">
      <el-menu-item>
        <img style="width: 60px" src="/public/logo.svg" alt="Element logo" />
      </el-menu-item>

      <el-menu-item index="/">首页</el-menu-item>
      <el-menu-item index="/recommend">为您推荐</el-menu-item>
      <el-menu-item index="/hot">时事热点</el-menu-item>

      <div class="flex-grow"></div>

      <el-menu-item index="/login" v-if="loginText">登录</el-menu-item>
      <el-dropdown v-else style="display: flex;align-items: center">
        <span class="el-dropdown-link">
          <img style="width: 50px" fit="fill" src="../assets/default-avatar.png" alt="avatar" />
        </span>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item @click="goToProfile">个人信息</el-dropdown-item>
            <el-dropdown-item @click="goToHistory">浏览记录</el-dropdown-item>
            <el-dropdown-item divided @click="logout">注销</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </el-menu>
  </el-header>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStoreHook } from '../stores/modules/user'


const useUserStore = useUserStoreHook()
const loginText = ref(useUserStore.user.id ? false : true)

const route = useRouter()
const activeIndex = ref(route.currentRoute.value.fullPath)

function handleSelect(index) {
  route.push(index)
}
function goToProfile() {
  route.push('/profile')
}
function goToHistory() {
  route.push('/view-history')
}


/** 退出登录 */
function logout() {
  useUserStoreHook().logOut();
  route.push('/login')
}
</script>

<style scoped>
.header-nav {
  position: fixed;
  top: 0;
  width: 100%;
  z-index: 1000;
  background: #fff;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}
.el-menu-demo{
  max-width: 1200px;
  margin: 0 auto;
}

.flex-grow {
  flex-grow: 1;
}

.el-menu--horizontal {
  width: 100%;
}

.el-dropdown {
  margin-left: auto;
}

.el-dropdown-link {
  border: none;
  outline: none;
  border-style: none;
}

.el-dropdown-link:hover {
  border: none;
  outline: none;
  border-style: none;
}
</style>
