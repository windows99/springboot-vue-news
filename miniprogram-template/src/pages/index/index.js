// index.js
Page({
  async handler() {
    console.log("hello");
  },

  /**
   * 生命周期函数--监听页面显示
   */
  onShow() {
    // 初始化tabbar
    const tabbar = this.selectComponent('#footer-nav-bar');
    tabbar.init("index","user")
  }
})
