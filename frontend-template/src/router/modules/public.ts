export default {
  path: "/public",
  redirect: "/public/tags",
  meta: {
    title: "公共配置",
    icon: "setting",
    roles: ["admin"],
    keepAlive: true,
    rank: 5
  },
  children: [
    {
      path: '/public/tags',
      name: 'NewsTags',
      component: () => import('@/views/public/tags.vue'),
      meta: {
        title: '新闻标签管理',
        keepAlive: true
      }
    },
    {
      path: '/public/sensitive-words',
      name: 'NewsSensitiveWords',
      component: () => import('@/views/public/sensitive-words.vue'),
      meta: {
        title: '敏感词维护',
        keepAlive: true
      }
    }
  ]
}
