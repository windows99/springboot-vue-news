// 发送验证码
import { ref } from "vue";
import { clone } from "@pureadmin/utils";
import { ElMessage } from "element-plus";
import { LockType } from "@/model/comon";
import { getCodeUsingPost } from "@/api/commonController";

export const start = async (mail: string, time = 60) => {
  const isDisabled = ref(false);
  const timer = ref(null);
  const text = ref("");

  const initTime = clone(time, true);
  clearInterval(timer.value);
  timer.value = setInterval(() => {
    if (time > 0) {
      text.value = `${time}`;
      isDisabled.value = true;
      time -= 1;
    } else {
      text.value = "";
      isDisabled.value = false;
      clearInterval(timer.value);
      time = initTime;
    }
  }, 1000);
  // 发送验证码
  const res = await getCodeUsingPost(mail);
  if (res.code === 0) {
    ElMessage({
      message: res.data,
      type: "success"
    });
  } else {
    ElMessage.error("系统繁忙");
  }
  const lockType: LockType = { isDisabled: isDisabled.value, timer, text: text.value };
  return lockType;
};
