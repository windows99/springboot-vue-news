<script setup lang="ts">
import { ref } from "vue";
import Motion from "../utils/motion";
import { ElMessage, FormInstance } from "element-plus";
import { useRenderIcon } from "@/components/ReIcon/src/hooks";
import Lock from "@iconify-icons/ri/lock-fill";
import Mail from "@iconify-icons/material-symbols/mail-outline-rounded";
import User from "@iconify-icons/ri/user-3-fill";
import { formRules, REGEXP_PWD } from "../utils/rule";
import { message } from "@/utils/message";
import { LockType } from "@/model/comon";
import { start } from "@/views/login/utils/mailUtils";
import { passwordResetUsingPost } from "@/api/userController";

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
  if (!resetForm.value.email) {
    return ElMessage.error("请输入邮箱");
  }
  lock.value = await start(resetForm.value.email);
};

const loading = ref(false);
const resetForm = ref({
  email: "",
  checkCode: "",
  userPassword: "",
  checkPassword: ""
});

const ruleFormRef = ref<FormInstance>();

const onReset = async (formEl: FormInstance | undefined) => {
  loading.value = true;
  if (!formEl) return;
  await formEl.validate(async (valid, fields) => {
    if (valid) {
      // 注册
      const res = await passwordResetUsingPost(resetForm.value);
      if (res.code === 0 && res.data) {
        message("重置成功", { type: "success" });
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
      } else if (resetForm.value.userPassword !== value) {
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
  <el-form ref="ruleFormRef" :model="resetForm" :rules="formRules" size="large">
    <Motion :delay="100">
      <el-form-item prop="email">
        <el-input clearable v-model="resetForm.email" placeholder="请输入邮箱号" :prefix-icon="useRenderIcon(Mail)" />
      </el-form-item>
    </Motion>

    <Motion :delay="150">
      <el-form-item prop="checkCode">
        <el-row :gutter="5">
          <el-col :span="16">
            <el-input clearable v-model="resetForm.checkCode" placeholder="请输入验证码"
              :prefix-icon="useRenderIcon('ri:shield-keyhole-line')" />
          </el-col>
          <el-col :span="4">
            <el-button :disabled="lock.isDisabled" class="ml-2" @click="getCode">
              {{
                lock.text.length > 0 ? lock.text + "秒后重新获取" : "获取验证码"
              }}
            </el-button>
          </el-col>
        </el-row>
      </el-form-item>
    </Motion>

    <Motion :delay="200">
      <el-form-item prop="userPassword">
        <el-input clearable show-password v-model="resetForm.userPassword" placeholder="请输入新密码"
          :prefix-icon="useRenderIcon(Lock)" />
      </el-form-item>
    </Motion>

    <Motion :delay="250">
      <el-form-item :rules="checkPasswordRule" prop="checkPassword">
        <el-input clearable show-password v-model="resetForm.checkPassword" placeholder="请再次确认密码"
          :prefix-icon="useRenderIcon(Lock)" />
      </el-form-item>
    </Motion>

    <Motion :delay="350">
      <el-form-item>
        <el-button class="w-full" size="default" type="primary" :loading="loading" @click="onReset(ruleFormRef)">
          重置密码
        </el-button>
      </el-form-item>
    </Motion>

    <Motion :delay="400">
      <el-form-item>
        <el-button class="w-full" size="default" @click="onBack">
          返回
        </el-button>
      </el-form-item>
    </Motion>
  </el-form>
</template>
