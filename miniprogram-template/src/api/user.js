import http from '../utils/http'

/**
 * 微信号一键登录
 * @param {code} 微信小程序code
 */
export const LoginByWxMiniApp = (code) => http.get("/user/login/wx_mini", {
  code
})

/**
 * 获取登录用户信息
 */
export const GetLoginUser = () => http.get("/user/get/login")