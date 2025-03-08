Component({
  data: {
    active: "index",
    tabbarList: [
      {
        icon: "home-o",
        name: "index",
        text: "主页",
        roles: ["user"]
      },
      {
        icon: "friends-o",
        name: "me",
        text: "我的",
        roles: ["user", "admin"]
      }
    ]
  },
  methods: {
    /**
     * 
     * @param {active} 默认激活tab 
     * @param {role} 当前用户角色 
     */
    init(active = "index", role = "user") {
      this.setData({
        active,
        tabbarList: this.data.tabbarList.filter(tabbarList => tabbarList.roles.includes(role))
      })
    },
    onChange(event) {
      const page = event.detail;
      wx.switchTab({
        url: `/pages/${page}/${page}`,
      })
    }
  }
});
