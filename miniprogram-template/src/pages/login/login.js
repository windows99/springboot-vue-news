import { toast } from '../../utils/extendApi'
import { LoginByWxMiniApp } from '../../api/user'
Page({

  // 使用微信号进行一键登录进行登录
  login() {
    wx.login({
      success: async ({ code }) => {
        if (code) {
          // 请求后端进行登录
          await LoginByWxMiniApp(code)
          wx.navigateBack()
        } else {
          toast({ title: '登录失败，请重试' })
        }
      },
    })
  },
})