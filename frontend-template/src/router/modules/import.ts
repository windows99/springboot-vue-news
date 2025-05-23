const { VITE_HIDE_HOME } = import.meta.env;
const Layout = () => import("@/layout/index.vue");

export default {
  path: "/import",
  name: "Import",
  component: Layout,
  redirect: "/import/news",
  meta: {
    icon: "documentAdd",
    title: "导入新闻",
    rank: 3,
    roles: ["admin"],
  },
  children: [
    {
      path: "/import/news",
      name: "ImportNews",
      component: () => import("@/views/import/index.vue"),
      meta: {
        title: "导入新闻",
        roles: ["admin"],
        keepAlive: true
      }
    }
  ]
} as RouteConfigsTable; 