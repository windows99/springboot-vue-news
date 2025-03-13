<template>
  <div class="main">
    <div class="flex-c absolute right-5 top-3">
      <!-- 主题 -->
      <el-switch v-model="dataTheme" inline-prompt :active-icon="dayIcon" :inactive-icon="darkIcon"
        @change="dataThemeChange" />
    </div>
    <div class="box">
      <Motion>
        <div class="top">
          <div class="title">
            <img src="/logo.svg" alt="logo" />
            <span>{{ title }}</span>
          </div>
          <div class="description">
            要是追不上光，那就变成光吧。
          </div>
        </div>
      </Motion>
      <!--          登录表单-->
      <Login v-model:current-page="currentPage" v-if="currentPage === 'login'" />
      <!--          注册表单-->
      <Register v-model:current-page="currentPage" v-if="currentPage === 'register'" />
      <!--          找回密码表单-->
      <Forget v-model:current-page="currentPage" v-if="currentPage === 'forget'" />
      <!--      底部备案-->
      <div />
    </div>
  </div>
</template>
<script setup lang="ts">
import Motion from "./utils/motion";
import { useNav } from "@/layout/hooks/useNav";
import { useLayout } from "@/layout/hooks/useLayout";
import { onBeforeUnmount, onMounted, ref } from "vue";
import { useDataThemeChange } from "@/layout/hooks/useDataThemeChange";
import dayIcon from "@/assets/svg/day.svg?component";
import darkIcon from "@/assets/svg/dark.svg?component";
import Forget from "@/views/login/components/Forget.vue";
import Register from "@/views/login/components/Register.vue";
import Login from "@/views/login/components/Login.vue";

defineOptions({
  name: "Login"
});

const { initStorage } = useLayout();
initStorage();

const { dataTheme, dataThemeChange } = useDataThemeChange();
dataThemeChange();
const { title } = useNav();

// 切换显示不同的表单
const currentPage = ref("login");

onMounted(() => {
  window.document.addEventListener("keypress", onkeypress);
});

onBeforeUnmount(() => {
  window.document.removeEventListener("keypress", onkeypress);
});
</script>

<style scoped>
@import url("@/style/login.css");

.main {
  display: flex;
  justify-content: center;
  background-image: url("/public/index.png");
  background-size: cover;
  height: 100vh;
}

.box {
  margin-top: 70px;
  width: 300px;
  text-align: center;
}

:deep(.el-input-group__append, .el-input-group__prepend) {
  padding: 0;
}

.top {
  margin-bottom: 50px;

  .title {
    display: flex;
    align-items: center;
    justify-content: center;
    width: auto;
    min-width: 200px;
    cursor: pointer;
    transition: all var(--pure-transition-duration) ease;
    height: 44px;
    line-height: 44px;

    img {
      display: inline-block;
      height: 100%;
    }

    span {
      display: inline-block;
      margin: 2px 0 0 12px;
      overflow: hidden;
      font-weight: 600;
      line-height: 44px;
      color: black;
      text-overflow: ellipsis;
      white-space: nowrap;
      font-size: 33px;
    }
  }

  .description {
    color: rgba(0, 0, 0, 0.65);
    font-size: 14px;
    margin-top: 12px;
  }
}
</style>
