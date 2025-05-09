export default {
  path: "/news",
  redirect: "/news/list",
  meta: {
    title: "新闻管理",
    icon: "files",
    roles: ["admin", "editor", "manage"],
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
        roles: ["admin", "editor",],
        hiddenTag: true,
        keepAlive: true,
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
        roles: ["admin", "editor"],
        keepAlive: true
      }
    },
    {
      path: '/news/feedback',
      name: 'NewsFeedback',
      component: () => import('@/views/news/feedback.vue'),
      meta: {
        title: '反馈新闻',
        roles: ["admin", "manage"],
        keepAlive: true
      }
    }
  ]
}
