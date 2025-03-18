import { defineStore } from "pinia";
import {
  getTagListUsingGet,
} from '@/api/newsTagController'
import { getNewsByIdUsingGet } from '@/api/newsController'

interface NewsState {
  newsTagList: any[];
  newsCache: Map<number, any>;
}

export const useNewsStore = defineStore('pure-news', {
  state: (): NewsState => ({
    newsTagList: [{ id: 1 }],
    newsCache: new Map()
  }),
  actions: {
    async newsTagListChange() {
      const queryRequest = {
        current: 1,
        pageSize: 100
      };
      const res = await getTagListUsingGet(queryRequest);
      if (res && res.data) {
        this.newsTagList = res.data;
      }
    },
    async getNews(id: number) {
      if (this.newsCache.has(id)) {
        return this.newsCache.get(id);
      }
      const res = await getNewsByIdUsingGet({ id });
      if (res && res.data) {
        this.newsCache.set(id, res.data);
        return res.data;
      }
      return null;
    },
    cacheNews(id: number, data: any) {
      this.newsCache.set(id, data);
    }
  }
});

export const initNewsStore = () => {
  const store = useNewsStore();
  store.newsTagListChange();
  return store;
};
