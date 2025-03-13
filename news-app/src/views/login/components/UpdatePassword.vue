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
import { updatePasswordUsingPost } from "@/api/userController";

const lock = ref<LockType>({
  isDisabled: false,
  timer: {},
  text: ""
});

const getCode = async () => {
  if (!updateForm.value.mail) {
    return ElMessage.error("请输入邮箱");
  }
  lock.value = await start(updateForm.value.mail);
};

const loading = ref(false);
const updateForm = ref({
  oldPassword: "",
  mail: "",
  checkCode: "",
  userPassword: "",
  checkPassword: ""
});

// 确认密码
const repeatPasswordRule = [
  {
    validator: (rule, value, callback) => {
      if (value === "") {
        callback(new Error("请再次输入密码"));
      } else if (!REGEXP_PWD.test(value)) {
        callback(new Error("密码格式应为6-16位数字或字母"));
      } else if (updateForm.value.userPassword !== value) {
        callback(new Error("两次密码不一致"));
      } else {
        callback();
      }
    },
    trigger: "blur"
  }
];

// 隐藏修改框
const props = defineProps(["dialogFormVisible"]);
const $emit = defineEmits(["update:dialogFormVisible"]);

const ruleFormRef = ref<FormInstance>();

const onUpdate = async (formEl: FormInstance | undefined) => {
  loading.value = true;
  if (!formEl) return;
  await formEl.validate(async (valid, fields) => {
    if (valid) {
      // 注册
      const res = await updatePasswordUsingPost(updateForm.value);
      if (res.code === 0 && res.data) {
        message("修改成功", { type: "success" });
        $emit("update:dialogFormVisible", false);
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
</script>

<template>
  <el-form ref="ruleFormRef" :model="updateForm" :rules="formRules" size="large">
    <el-form-item prop="oldPassword">
      <el-input clearable show-password v-model="updateForm.oldPassword" placeholder="请输入旧密码"
        :prefix-icon="useRenderIcon(Lock)" />
    </el-form-item>

    <el-form-item prop="mail">
      <el-input clearable v-model="updateForm.mail" placeholder="请输入邮箱" :prefix-icon="useRenderIcon(Mail)" />
    </el-form-item>

    <el-form-item prop="checkCode">
      <el-row :gutter="5">
        <el-col :span="16">
          <el-input clearable v-model="updateForm.checkCode" placeholder="请输入验证码"
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

    <Motion :delay="200">
      <el-form-item prop="userPassword">
        <el-input clearable show-password v-model="updateForm.userPassword" placeholder="请输入新密码"
          :prefix-icon="useRenderIcon(Lock)" />
      </el-form-item>
    </Motion>

    <Motion :delay="250">
      <el-form-item :rules="repeatPasswordRule" prop="checkPassword">
        <el-input clearable show-password v-model="updateForm.checkPassword" placeholder="请再次确认密码"
          :prefix-icon="useRenderIcon(Lock)" />
      </el-form-item>
    </Motion>

    <Motion :delay="350">
      <el-form-item>
        <el-button class="w-full" size="default" type="primary" :loading="loading" @click="onUpdate(ruleFormRef)">
          确认修改
        </el-button>
      </el-form-item>
    </Motion>
  </el-form>
</template>
