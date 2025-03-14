import { defineStore } from 'pinia'
import request from '@/utils/request'

export const useNewsStore = defineStore('news', {
  actions: {
    async importNews(news) {
      await request.post('/news', news)
    }
  }
}) 