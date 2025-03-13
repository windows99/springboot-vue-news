<script setup lang="ts">
import { ref } from "vue";
import Motion from "../utils/motion";
import { ElMessage, FormInstance } from "element-plus";
import { useRenderIcon } from "@/components/ReIcon/src/hooks";
import Lock from "@iconify-icons/ri/lock-fill";
import Mail from "@iconify-icons/material-symbols/mail-outline-rounded";
import { formRules, REGEXP_PWD } from "../utils/rule";
import { message } from "@/utils/message";
import { LockType } from "@/model/comon";
import { start } from "@/views/login/utils/mailUtils";
import { userRegisterUsingPost } from "@/api/userController";

// 页面切换
const props = defineProps(["currentPage"]);
const $emit = defineEmits(["update:currentPage"]);

const onBack = () => {
  $emit("update:currentPage", "login");
};

const lock = ref<LockType>({
  isDisabled: false,
  timer: {},
  text: ""
});

const getCode = async () => {
  if (!registerForm.value.email) {
    return ElMessage.error("请输入邮箱");
  }
  lock.value = await start(registerForm.value.email);
};

const loading = ref(false);
const registerForm = ref({
  email: "",
  checkCode: "",
  userPassword: "",
  checkPassword: ""
});

const ruleFormRef = ref<FormInstance>();

const onRegister = async (formEl: FormInstance | undefined) => {
  loading.value = true;
  if (!formEl) return;
  await formEl.validate(async (valid, fields) => {
    if (valid) {
      // 注册
      const res = await userRegisterUsingPost(registerForm.value);
      if (res.code === 0) {
        message("注册成功", { type: "success" });
        onBack();
      } else {
        message(res.message, { type: "warning" });
      }
      loading.value = false;
    } else {
      loading.value = false;
      return fields;
    }
  });
};

// 确认密码
const checkPasswordRule = [
  {
    validator: (rule, value, callback) => {
      if (value === "") {
        callback(new Error("请再次输入密码"));
      } else if (!REGEXP_PWD.test(value)) {
        callback(new Error("密码格式应为6-16位数字或字母"));
      } else if (registerForm.value.userPassword !== value) {
        callback(new Error("两次密码不一致"));
      } else {
        callback();
      }
    },
    trigger: "blur"
  }
];
</script>

<template>
  <el-form ref="ruleFormRef" :model="registerForm" :rules="formRules" size="large">

    <el-form-item prop="email">
      <el-input clearable v-model="registerForm.email" placeholder="请输入邮箱" :prefix-icon="useRenderIcon(Mail)" />
    </el-form-item>


    <el-form-item prop="checkCode">
      <el-row :gutter="5">
        <el-col :span="16">
          <el-input clearable v-model="registerForm.checkCode" placeholder="请输入验证码"
            :prefix-icon="useRenderIcon('ri:shield-keyhole-line')" /></el-col>
        <el-col :span="4">
          <el-button :disabled="lock.isDisabled" class="ml-2" @click="getCode">
            {{
              lock.text.length > 0 ? lock.text + "秒后重新获取" : "获取验证码"
            }}
          </el-button></el-col>
      </el-row>
    </el-form-item>

    <el-form-item prop="userPassword">
      <el-input clearable show-password v-model="registerForm.userPassword" placeholder="请输入密码"
        :prefix-icon="useRenderIcon(Lock)" />
    </el-form-item>

    <el-form-item :rules="checkPasswordRule" prop="checkPassword">
      <el-input clearable show-password v-model="registerForm.checkPassword" placeholder="请再次确认密码"
        :prefix-icon="useRenderIcon(Lock)" />
    </el-form-item>

    <el-form-item>
      <el-button class="w-full" size="default" type="primary" :loading="loading" @click="onRegister(ruleFormRef)">
        注册
      </el-button>
    </el-form-item>

    <el-form-item>
      <el-button class="w-full" size="default" @click="onBack">
        返回
      </el-button>
    </el-form-item>
  </el-form>
</template>
