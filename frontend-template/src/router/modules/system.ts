const { VITE_HIDE_HOME } = import.meta.env;
const Layout = () => import("@/layout/index.vue");

export default {
  path: "/system",
  name: "System",
  component: Layout,
  redirect: "/system",
  meta: {
    icon: "bell",
    title: "操作记录",
    roles: ["admin"],
    rank: 7
  },
  children: [
    {
      path: "/operation-log",
      name: "OperationLog",
      component: () => import("@/views/system/operation-log.vue"),
      meta: {
        title: "操作记录",
        showLink: VITE_HIDE_HOME !== "true"
      }
    }
   
  ]
} as RouteConfigsTable;
