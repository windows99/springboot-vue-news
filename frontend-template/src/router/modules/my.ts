const Layout = () => import("@/layout/index.vue");

export default {
  path: "/user",
  name: "User",
  component: Layout,
  redirect: "/user/myInfo",
  meta: {
    icon: "homeFilled",
    title: "我的",
    showLink: false,
    rank: 0
  },
  children: [
    {
      path: "/user/myInfo",
      name: "MyInfo",
      component: () => import("@/views/my/myInfo.vue"),
      meta: {
        title: "个人信息"
      }
    }
  ]
} as RouteConfigsTable;
