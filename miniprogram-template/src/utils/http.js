import WxRequest from './request'
import { modal, toast } from './extendApi'
import { env } from './env'

// 本地缓存cookie key名称
const cookie_key = "cookie"

// 实例化对象
const instance = new WxRequest({
  baseURL: env.baseURL
})

// 配置请求拦截器
instance.interceptors.request = (config) => {
  // 请求之前，判断本地是否存在cookie
  const cookie = wx.getStorageSync(cookie_key)
  // 存在，在请求头中添加
  if (cookie) {
    config.header['cookie'] = cookie
  }
  return config;
}

// 配置响应拦截器
instance.interceptors.response = async (response) => {
  const { isSuccess, data } = response
  const cookie = response.header["Set-Cookie"]
  if (cookie) {
    wx.setStorageSync(cookie_key, cookie)
  }
  // 网络异常，执行fail函数
  if (!isSuccess) {
    toast({ title: '网络异常', icon: 'error' })
    return response;
  }
  // 判断服务器响应业务的状态码
  switch (data.code) {
    // 请求正常，服务器响应了数据
    case 0:
      return data;
    // 未登录或者登录失效，前往登录
    case 40100:
      const res = await modal({
        content: '登录过期，请重新登录',
        showCancel: false
      })
      if (res) {
        wx.clearStorageSync()
        wx.navigateTo({
          url: '/pages/login/login',
        })
      }
      return Promise.reject(response)
    default:
      toast({ title: data.message })
      return Promise.reject(response)
  }
}

export default instance;