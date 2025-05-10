/**
 * roles：页面级别权限，这里模拟二种 "admin"、"common"
 * admin：管理员角色
 * common：普通角色
 */
import Layout from "@/layout/index.vue";

export default {
  path: "/admin",
  component: Layout,
  redirect: "/admin/user/userManage",
  meta: {
    title: "用户管理",
    icon: "user",
    rank: 1,
    roles: ["admin"]
  },
  children: [
    {
      path: "/admin/user/userManage",
      name: "UserManage",
      component: () => import("@/views/permission/user/userManage.vue"),
      meta: {
        title: "用户管理",
        icon: "User"
      }
    },
    {
      path: "/admin/user/commentManage",
      name: "CommentManage",
      component: () => import("@/views/permission/user/commentManage.vue"),
      meta: {
        title: "评论管理",
        icon: "ChatDotRound",
        roles: ["admin", "editor"]
      }
    }
  ]
} as RouteConfigsTable;
