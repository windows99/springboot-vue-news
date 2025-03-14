<template>
  <NavBar />
  <div class="profile-container">
    <main class="main-content">
      <h2>个人信息</h2>
      <el-form @submit.prevent="updateProfile">
        <el-form-item label="用户名">
          <el-input v-model="user.username" required />
        </el-form-item>

        <el-form-item label="邮箱">
          <el-input type="email" v-model="user.email" required />
        </el-form-item>

        <el-form-item label="个人简介">
          <el-input type="textarea" v-model="user.profile" :rows="3" />
        </el-form-item>

        <el-form-item label="性别">
          <el-radio-group v-model="user.gender">
            <el-radio label="male">男</el-radio>
            <el-radio label="female">女</el-radio>
            <el-radio label="other">其他</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="头像">
          <el-upload
            action="#"
            :on-change="handleAvatarChange"
            :auto-upload="false"
            :show-file-list="false"
          >
            <el-button type="primary">点击上传</el-button>
          </el-upload>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" native-type="submit">保存修改</el-button>
        </el-form-item>
      </el-form>
    </main>
  </div>
  <FooterBar />
</template>

<script setup>

import { ref, onMounted } from 'vue'
import NavBar from '../components/NavBar.vue'
import FooterBar from '../components/FooterBar.vue'
// import { useUserStore } from '@/stores'

// const userStore = useUserStore()
const user = ref({
  username: '',
  email: '',
  avatar: '',
  profile: '',
  gender: ''
})

onMounted(async () => {
  // await userStore.getUserProfile()
  // user.value = { ...userStore.userProfile }
})

const handleAvatarChange = (event) => {
  const file = event.target.files[0]
  if (file) {
    const reader = new FileReader()
    reader.onload = (e) => {
      user.value.avatar = e.target.result
    }
    reader.readAsDataURL(file)
  }
}

const updateProfile = async () => {
  // await userStore.updateUserProfile(user.value)
}
</script>

<style scoped>
.profile-container {
  max-width: 1200px;
  margin: 0 auto;
}

.main-content {
  padding: 20px;
}

h2 {
  margin-bottom: 20px;
}

.profile-container {
  max-width: 1200px;
  margin: 0 auto;
}

.main-content {
  padding: 20px;
}

h2 {
  margin-bottom: 20px;
}
</style>