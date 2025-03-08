import { defineStore } from "pinia";
import {
  getTagListUsingGet,
} from '@/api/newsTagController'


export const useNewsStore = defineStore({
  id: "pure-news",
  state: () => ({
    newsTagList: [{ id: 1 }]
  }),
  actions: {
    async newsTagListChange() {
      const queryRequest = ({
        current: 1,
        pageSize: 100
      });
      const res = await getTagListUsingGet(queryRequest)
      this.newsTagList = res.data;
      console.log(this.newsTagList)
    }
  }

});
useNewsStore().newsTagListChange()
