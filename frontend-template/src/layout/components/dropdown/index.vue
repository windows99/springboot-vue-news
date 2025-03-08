<template>
  <el-dropdown trigger="click">
    <span class="el-dropdown-link">
      <img :src="user?.userAvatar" :style="avatarsStyle" />
      <p style="color: black !important">{{ user?.userName + ",欢迎您！" }}</p>
    </span>
    <template #dropdown>
      <!--          个人中心-->
      <el-dropdown-menu class="nav_item">
        <el-dropdown-item @click="toMyInfoPage">
          <IconifyIconOffline :icon="avatarIcon" style="margin: 5px" />
          个人中心
        </el-dropdown-item>
      </el-dropdown-menu>
      <!--          修改密码-->
      <el-dropdown-menu class="nav_item">
        <el-dropdown-item @click="toUpdatePassword">
          <IconifyIconOffline :icon="triangleAlert" style="margin: 5px" />
          修改密码
        </el-dropdown-item>
      </el-dropdown-menu>
      <!--          退出登录-->
      <el-dropdown-menu class="nav_item">
        <el-dropdown-item @click="logout">
          <IconifyIconOffline :icon="LogoutCircleRLine" style="margin: 5px" />
          退出登录
        </el-dropdown-item>
      </el-dropdown-menu>

      <!--    修改密码-->
      <el-dialog
        width="30%"
        draggable
        style="z-index: 999"
        center
        append-to-body
        destroy-on-close
        v-model="dialogFormVisible"
        title="修改密码"
      >
        <UpdatePassword v-model:dialogFormVisible="dialogFormVisible" />
      </el-dialog>
    </template>
  </el-dropdown>
</template>
<script setup lang="ts">
import avatarIcon from "@iconify-icons/ep/avatar";
import triangleAlert from "@iconify-icons/akar-icons/triangle-alert";
import LogoutCircleRLine from "@iconify-icons/ri/logout-circle-r-line";
import UpdatePassword from "@/views/login/components/UpdatePassword.vue";
import { ref } from "vue";
import { useRouter } from "vue-router";
import { useNav } from "@/layout/hooks/useNav";

const { logout, user, avatarsStyle } = useNav();

// 修改密码
const dialogFormVisible = ref(false);
const toUpdatePassword = () => {
  dialogFormVisible.value = true;
};

// 跳转到用户登录页
const router = useRouter();
const toMyInfoPage = () => {
  router.push("/user/myInfo");
};
</script>

<style scoped lang="scss">
::v-deep(.el-dropdown-menu__item) {
  display: inline-flex;
  flex-wrap: wrap;
  min-width: 100%;
}

.el-dropdown-link {
  display: flex;
  align-items: center;
  justify-content: space-around;
  height: 48px;
  padding: 10px;
  color: #000000d9;
  cursor: pointer;

  p {
    font-size: 14px;
  }

  img {
    width: 22px;
    height: 22px;
    border-radius: 50%;
  }
}

.nav_item {
  max-width: 120px;

  ::v-deep(.el-dropdown-menu__item) {
    display: inline-flex;
    flex-wrap: wrap;
    min-width: 100%;
  }
}
</style>
