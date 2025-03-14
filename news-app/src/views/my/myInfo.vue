<template>
  <NavBar />
  <div class="content">
    <el-row class="el-row" :gutter="20">
      <el-col>
        <div class="grid-content">
          <el-card class="box-card">
            <template #header>
              <div class="card-header">
                <el-row style="margin-bottom: 0">
                  <el-col :span="16">
                    <el-avatar
                      shape="square"
                      :size="140"
                      :src="user.userAvatar"
                    />
                    <el-space alignment="flex-start" direction="vertical">
                      <span class="avatarName">{{ user.userName }}</span>
                      <span style="margin: 15px">{{
                        user.userProfile ? user.userProfile : "暂无个人简介"
                      }}</span>
                    </el-space>
                  </el-col>
                  <el-col :span="8">
                    <span style="float: right">
                      <el-button
                        size="large"
                        @click="toEditInfo"
                        plain
                        type="primary"
                        >编辑个人资料</el-button
                      >
                    </span>
                  </el-col>
                </el-row>
              </div>
            </template>
          </el-card>
        </div>
      </el-col>
    </el-row>
    <el-row class="el-row" :gutter="20">
      <el-col>
        <el-card>
          <h3 class="title">基本信息</h3>
          <div>
            <div class="item">简介：{{ user.userProfile }}</div>
            <div class="item">性别：{{ getUserGender(user.gender) }}</div>
            <div class="item">邮箱：{{ user.email }}</div>
          </div>
          <el-divider />
          <h3 class="title">其他信息</h3>
          <div>
            <div class="item">用户角色：{{ user.userRole }}</div>
            <div class="item">注册时间：{{ user.createTime }}</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!--  编辑信息对话框-->
    <el-dialog
      v-model="editFormVisible"
      title="编辑用户信息"
      style="width: 580px"
      draggable
    >
      <el-form :model="currentUser">
        <el-form-item label="用户名" :label-width="formLabelWidth">
          <el-input v-model="currentUser.userName" autocomplete="off" />
        </el-form-item>
        <!--      头像修改-->
        <el-form-item label="头像" :label-width="formLabelWidth">
          <!--          头像上传组件-->
          <upload-avatar
            v-model:img-url="currentUser.userAvatar"
            biz="user_avatar"
          />
        </el-form-item>
        <el-form-item label="简介" :label-width="formLabelWidth">
          <el-input
            v-model="currentUser.userProfile"
            type="textarea"
            autocomplete="off"
          />
        </el-form-item>
        <!--      性别选项-->
        <el-form-item label="性别" :label-width="formLabelWidth">
          <el-radio-group v-model="currentUser.gender">
            <el-radio :label="0">女</el-radio>
            <el-radio :label="1">男</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="cancelEdit">取消</el-button>
          <el-button type="primary" @click="confirmEdit"> 确认 </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
  <FooterBar />
</template>

<script lang="ts" setup>
import { ElInput, ElMessage } from "element-plus";
import router from "@/router";
import NavBar from '@/components/NavBar.vue'
import FooterBar from '@/components/FooterBar.vue'
import { computed, onMounted, ref } from "vue";
import { useUserStore } from "@/stores/modules/user";
import { message } from "@/utils/message";
import { setToken } from "@/utils/auth";
import { updateMyUserUsingPost } from "@/api/userController";
import UploadAvatar from "@/components/ImageUploader/UploaderAvatar.vue";

defineOptions({
  name: "UserDetail"
});

// 当前登录用户
const user = ref<API.User>({});

// 获取当前登录用户信息
const getCurrentUser = () => {
  const userLogin = useUserStore().$state.user;
  // 未登录,禁止访问
  if (userLogin == null) {
    ElMessage.error("请先登录");
    return router.push("/error/403");
  }
  // 已登录
  user.value = userLogin;
};

// 用户性别
const getUserGender = computed(() => (gender: number): string => {
  if (gender === 0) {
    return "女";
  } else if (gender === 1) {
    return "男";
  } else if (gender === 2) {
    return "未知";
  } else {
    return "未填写";
  }
});

onMounted(() => {
  getCurrentUser();
});

// 编辑用户信息
const editFormVisible = ref(false);
const formLabelWidth = "100px";
// 当前修改用户
const currentUser = ref<API.LoginUserVO>({});
// 修改用户对话框
const toEditInfo = () => {
  editFormVisible.value = true;
  // 浅拷贝修改
  currentUser.value = Object.assign({}, user.value);
};
// 取消修改
const cancelEdit = () => {
  editFormVisible.value = false;
  currentUser.value = {};
};
// 确认修改
const confirmEdit = async () => {
  const res = await updateMyUserUsingPost(currentUser.value);
  if (res.code === 0) {
    message("修改成功", { type: "success" });
    setToken({
      user: currentUser.value,
      roles: [currentUser.value.userRole]
    } as any);
    // 拷贝回去
    user.value = Object.assign({}, currentUser.value);
    cancelEdit();
  } else {
    message(res.message, { type: "error" });
  }
};
</script>

<style scoped>
.el-row {
  margin-bottom: 20px;
}

.content {
  margin-top: 60px;
  padding: 10px 100px;
}

.grid-content {
  border-radius: 4px;
  min-height: 36px;
}

.item {
  margin-left: 12px;
  margin-bottom: 15px;
  margin-top: 10px;
  font-size: 16px;
}

.title {
  margin-left: 12px;
  margin-bottom: 15px;
  margin-top: 10px;
}

.dialog-footer button:first-child {
  margin-right: 10px;
}

.avatarName {
  font-size: 18px;
  font-weight: 600;
  margin-left: 15px;
  vertical-align: top;
}
</style>
