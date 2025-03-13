import { reactive } from "vue";
import type { FormRules } from "element-plus";

/** 密码正则（密码格式应为8-16位数字或字母） */
export const REGEXP_PWD = /^[0-9a-zA-Z]{8,16}$/;
/** 邮箱正则*/
export const REGEXP_EMAIL =
  /^([a-zA-Z\d][\w-]{2,})@(\w{2,})\.([a-z]{2,})(\.[a-z]{2,})?$/;
/** 验证码正则（密码格式应为6-16位数字或字母） */
export const REGEXP_CHECK_CODE = /^[0-9]{6}$/;

/** 表单校验 */
const formRules = reactive(<FormRules>{
  userPassword: [
    {
      validator: (rule, value, callback) => {
        if (value === "") {
          callback(new Error("请输入密码"));
        } else if (!REGEXP_PWD.test(value)) {
          callback(new Error("密码格式应为8-16位数字或字母"));
        } else {
          callback();
        }
      },
      trigger: "blur"
    }
  ],
  email: [
    {
      validator: (rule, value, callback) => {
        if (value === "") {
          callback(new Error("请输入邮箱"));
        } else if (!REGEXP_EMAIL.test(value)) {
          callback(new Error("请输入正确邮箱"));
        } else {
          callback();
        }
      },
      trigger: "blur"
    }
  ],
  checkCode: [
    {
      validator: (rule, value, callback) => {
        if (value === "") {
          callback(new Error("请输入验证码"));
        } else if (!REGEXP_CHECK_CODE.test(value)) {
          callback(new Error("请输入正确验证码"));
        } else {
          callback();
        }
      },
      trigger: "blur"
    }
  ],
  oldPassword: [
    {
      validator: (rule, value, callback) => {
        if (value === "") {
          callback(new Error("请输入密码"));
        } else if (!REGEXP_PWD.test(value)) {
          callback(new Error("密码格式应为8-16位数字或字母"));
        } else {
          callback();
        }
      },
      trigger: "blur"
    }
  ]
});

export { formRules };
