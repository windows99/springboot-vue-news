const { VITE_HIDE_HOME } = import.meta.env;
const Layout = () => import("@/layout/index.vue");

export default {
  path: "/push",
  name: "Push",
  component: Layout,
  redirect: "/push",
  meta: {
    icon: "bell",
    title: "推送管理",
    rank: 0
  },
  children: [
    {
      path: "/push",
      name: "Push",
      component: () => import("@/views/push/index.vue"),
      meta: {
        title: "推送管理",
        showLink: VITE_HIDE_HOME !== "true"
      }
    }
  ]
} as RouteConfigsTable;
