// pages/me/me.js
import Dialog from '@vant/weapp/dialog/dialog';
import { GetLoginUser } from '../../api/user'
Page({
  data: {
    user: null
  },
  onShow: function () {
    this.getLoginUser()
    // 初始化tabbar
    const tabbar = this.selectComponent('#footer-nav-bar');
    tabbar.init("me", "user")
  },
  async getLoginUser() {
    const res = await GetLoginUser()
    this.setData({
      user: res.data
    })
  },
  /**
   * 跳转到类目详情界面（需要登录）
   */
  toDetailNeedLogin(options) {
    // 减小服务器压力，未登录则拦截后端请求
    if (!this.data.user) {
      return wx.showToast({
        title: '请先登录 ~',
        icon: 'none'
      })
    }
    // 跳转到详情界面
    const page = options.currentTarget.dataset.page;
    if (page === 'myFeedback') {
      // 意见反馈
      wx.navigateTo({
        url: '../../mePages/pages/my-feedback/my-feedback',
      })
    } else if (page === 'myInfo') {
      // 个人信息页
      wx.navigateTo({
        url: '/mePages/pages/my-info/my-info',
      })
    } else if (page === 'vipPay') {
      // vip开通界面
      wx.navigateTo({
        url: '/mePages/pages/vip-pay/vip-pay',
      })
    } else if (page === 'support') {
      // 充值积分
      wx.navigateTo({
        url: '../../mePages/pages/support-platform/support-platform',
      })
    } else if (page === 'articleReview') {
      // 代码审核
      wx.navigateTo({
        url: '../../interactPages/pages/article-review/article-review',
      })
    }
  },
  /**
   * 跳转到类目详情界面（不需要登录）
   */
  toDetailNotLogin(options) {
    // 跳转到详情界面
    const page = options.currentTarget.dataset.page;
    if (page === 'eatCoupon') {
      wx.navigateTo({
        url: '/toolsPages/pages/eat-coupon/eat-coupon',
      });
    } else if (page === "pointCalculate") {
      wx.navigateTo({
        url: '/schedulePages/pages/schedule-point/schedule-point',
      });
    } else if (page === "cycle-clock") {
      wx.navigateTo({
        url: '/toolsPages/pages/cycle-clock/cycle-clock',
      });
    } else {
      throw new Error("错误的导航名");
    }
  },
  clearTemp() {
    Dialog.confirm({
      title: '是否清除缓存？',
      message: '清除缓存能解决大部分问题',
    }).then(() => {
      wx.clearStorageSync()
      wx.showToast({
        title: '清除成功',
        icon: 'success',
      })
    });
  },
  onShareAppMessage() {
    return {
      title: '快速开发模板',
      path: '/pages/index/index',
    }
  },
  onShareTimeline() {
    return {
      title: '快速开发模板',
      path: '/pages/index/index',
    }
  },
  gotoMyInfo() {
    wx.navigateTo({
      url: '../../mePages/pages/my-info/my-info',
    })
  }
})