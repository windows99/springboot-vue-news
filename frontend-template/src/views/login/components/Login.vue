<script setup lang="ts">
import { onBeforeUnmount, onMounted, ref } from "vue";
import Motion from "../utils/motion";
import { useRenderIcon } from "@/components/ReIcon/src/hooks";
import Lock from "@iconify-icons/ri/lock-fill";
import { ElMessage, FormInstance } from "element-plus";
import { usePermissionStoreHook } from "@/store/modules/permission";
import { addPathMatch } from "@/router/utils";
import { setToken } from "@/utils/auth";
import { message } from "@/utils/message";
import router from "@/router";
import { formRules } from "../utils/rule";
import { userType } from "@/store/modules/types";
import CryptoJS from "crypto-js";
import { userLoginUsingPost } from "@/api/userController";
import Mail from "@iconify-icons/material-symbols/mail-outline-rounded";

// 页面切换
// eslint-disable-next-line @typescript-eslint/no-unused-vars,no-unused-vars
const props = defineProps(["currentPage"]);
const $emit = defineEmits(["update:currentPage"]);

// 切换显示不同的表单
const changePage = pageName => {
  $emit("update:currentPage", pageName);
};

const loginForm = ref({
  email: "",
  userPassword: ""
});
const loading = ref(false);
const ruleFormRef = ref<FormInstance>();

// 记住密码
const check = ref(false);

const rememberPassword = async () => {
  if (check.value) {
    // 加密
    const encrypted = CryptoJS.AES.encrypt(
      JSON.stringify(loginForm.value),
      String(check.value)
    );
    localStorage.setItem("user", encrypted);
    localStorage.setItem("remember", "true");
  } else {
    localStorage.removeItem("user");
    localStorage.removeItem("remember");
  }
};

const onLogin = async (formEl: FormInstance | undefined) => {
  loading.value = true;
  if (!formEl) return;
  await formEl.validate(async (valid, fields) => {
    if (valid) {
      const res = await userLoginUsingPost(loginForm.value);
      if (res.code !== 0) {
        ElMessage.error(res.message);
        loading.value = false;
        return;
      }
      // 是否记住密码
      rememberPassword();
      setToken({
        user: res.data,
        roles: [res.data.userRole]
      } as userType);
      router.push({ name: "Home" });
      // 全部采取静态路由模式
      usePermissionStoreHook().handleWholeMenus([]);
      addPathMatch();
      message("登录成功", { type: "success" });
    } else {
      loading.value = false;
      return fields;
    }
  });
};

/** 使用公共函数，避免`removeEventListener`失效 */
function onkeypress({ code }: KeyboardEvent) {
  if (code === "Enter") {
    onLogin(loginForm.value);
  }
}

onMounted(() => {
  window.document.addEventListener("keypress", onkeypress);
  const remember = localStorage.getItem("remember");
  if (remember) {
    // 解密
    const encrypted = localStorage.getItem("user");
    const decrypted = CryptoJS.AES.decrypt(encrypted, remember);
    loginForm.value = JSON.parse(decrypted.toString(CryptoJS.enc.Utf8));
    check.value = true;
  }
});
onBeforeUnmount(() => {
  window.document.removeEventListener("keypress", onkeypress);
});
</script>

<template>
  <el-form
    ref="ruleFormRef"
    :model="loginForm"
    :rules="formRules"
    size="large"
    v-if="currentPage === 'login'"
  >
    <Motion :delay="100">
      <el-form-item prop="email">
        <el-input
          clearable
          v-model="loginForm.email"
          placeholder="邮箱号"
          :prefix-icon="useRenderIcon(Mail)"
        />
      </el-form-item>
    </Motion>

    <Motion :delay="150">
      <el-form-item prop="userPassword" style="margin-bottom: 5px !important">
        <el-input
          clearable
          show-password
          v-model="loginForm.userPassword"
          placeholder="密码"
          :prefix-icon="useRenderIcon(Lock)"
        />
      </el-form-item>
    </Motion>
    <Motion :delay="200">
      <el-checkbox
        style="display: flex"
        v-model="check"
        label="记住密码"
        size="large"
      />
    </Motion>
    <Motion :delay="250">
      <el-button
        class="w-full"
        size="default"
        type="primary"
        :loading="loading"
        @click="onLogin(ruleFormRef)"
      >
        登录
      </el-button>
      <div
        style="margin-top: 10px"
        class="w-full h-[20px] flex justify-between items-center"
      >
        <el-button link type="primary" @click="changePage('forget')">
          忘记密码?
        </el-button>
        <el-button link type="primary" @click="changePage('register')">
          没有账号？立即注册
        </el-button>
      </div>
    </Motion>
  </el-form>
</template>
