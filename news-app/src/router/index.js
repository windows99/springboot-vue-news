import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import RecommendView from '../views/RecommendView.vue'
import HotView from '../views/HotView.vue'
import NewsDetailView from '../views/NewsDetailView.vue'
// import ProfileView from '../views/ProfileView.vue'
import ProfileCenter from '../views/my/ProfileCenter.vue'
import LoginView from '../views/login/index.vue'

const routes = [
  {
    path: '/',
    name: 'home',
    component: HomeView,
    meta: {
      keepAlive: true
    }
  },
  {
    path: '/recommend',
    name: 'recommend',
    component: RecommendView
  },
  {
    path: '/hot',
    name: 'hot',
    component: HotView
  },
  {
    path: '/news/:id',
    name: 'news-detail',
    component: NewsDetailView
  },
  {
    path: '/profile',
    name: 'profile',
    component: ProfileCenter,
    meta: {
      keepAlive: true
    }
  },
  {
    path: '/login',
    name: 'login',
    component: LoginView
  }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
})

export default router
