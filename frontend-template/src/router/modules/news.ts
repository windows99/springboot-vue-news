

export default {
  path: "/news",
  redirect: "/news/list",
  meta: {
    title: "新闻管理",
    icon: "memo",
    roles: ["admin", "editor"],
    keepAlive: true
  },
  children: [
    {
      path: "/news/list",
      name: "NewsList",
      component: () => import("@/views/news/list.vue"),
      meta: {
        title: "新闻列表",
        roles: ["admin", "editor", "manage"],
        showParent: true,
        keepAlive: true,
      }
    },
    {
      path: "/news/create",
      name: "NewsCreate",
      component: () => import("@/views/news/create.vue"),
      meta: {
        title: "创建新闻",
        hiddenTag: true,
        keepAlive: true,
      }
    },
    {
      path: "/news/import",
      name: "NewsImport",
      component: () => import("@/views/news/import.vue"),
      meta: {
        title: "导入新闻",
        roles: ["admin"],
        hiddenTag: true
      }
    },
    {
      path: '/news/detail/:id',
      name: 'NewsDetail',
      component: () => import('@/views/news/detail.vue'),
      meta: {
        title: '新闻详情',
        keepAlive: true,
        showLink: false,
      }
    },
    {
      path: '/news/review',
      name: 'NewsReview',
      component: () => import('@/views/news/review.vue'),
      meta: {
        title: '新闻审核',
        roles: ["admin", "manage"],
        keepAlive: true
      }
    },
    {
      path: '/news/publish',
      name: 'NewsPublish',
      component: () => import('@/views/news/publish.vue'),
      meta: {
        title: '新闻发布',
        roles: ["admin", "manage"],
        keepAlive: true
      }
    }
  ]
}
