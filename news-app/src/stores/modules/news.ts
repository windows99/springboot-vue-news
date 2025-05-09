import { defineStore } from "pinia";
import {
  getTagListUsingGet,
} from '@/api/newsTagController'
import { getNewsByIdUsingGet } from '@/api/newsController'
import { ref } from 'vue'

interface NewsItem {
  id?: number;
  newsId?: number;
  title: string;
  content: string;
  coverImage: string;
  [key: string]: any;
}

interface NewsState {
  newsTagList: any[];
  newsCache: Map<number, any>;
  pushedNews: any[];
}

export const useNewsStore = defineStore('news', () => {
  // 存储WebSocket推送的新闻
  const pushedNews = ref<NewsItem[]>([])
  // 存储当前页面的新闻列表
  const currentNewsList = ref<NewsItem[]>([])
  // 存储当前页码
  const currentPage = ref<number>(1)
  // 存储总页数
  const totalPages = ref<number>(0)

  // 添加推送的新闻
  const addPushedNews = (news: NewsItem[]) => {
    pushedNews.value = [...news, ...pushedNews.value]
    // 同时更新当前列表
    updateCurrentList()
  }

  // 更新当前列表
  const updateCurrentList = () => {
    currentNewsList.value = [...pushedNews.value, ...currentNewsList.value]
  }

  // 设置新闻列表
  const setNewsList = (news: NewsItem[], page: number, total: number) => {
    if (page === 1) {
      // 如果是第一页，直接设置
      currentNewsList.value = [...pushedNews.value, ...news]
    } else {
      // 如果是加载更多，追加到现有列表
      currentNewsList.value = [...currentNewsList.value, ...news]
    }
    currentPage.value = page
    totalPages.value = total
  }

  // 清空推送的新闻
  const clearPushedNews = () => {
    pushedNews.value = []
  }

  return {
    pushedNews,
    currentNewsList,
    currentPage,
    totalPages,
    addPushedNews,
    updateCurrentList,
    setNewsList,
    clearPushedNews
  }
})
