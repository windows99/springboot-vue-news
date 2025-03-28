import { defineStore } from "pinia";
import {
  getTagListUsingGet,
} from '@/api/newsTagController'


export const useNewsStore = defineStore({
  id: "pure-news",
  state: () => ({
    newsTagList: [{ id: 1, tagname: '' }],
    newsStatusList: [
      {
        id: 0, name: '草稿'
      },
      {
        id: 1, name: '已提交'
      },
      {
        id: 2, name: '审核通过'
      },
      {
        id: 3, name: '已发布'
      },
      {
        id: 4, name: '审核失败'
      },
      {
        id: 5, name: '已下架'
      },
      {
        id: 6, name: '反馈新闻'
      },
    ]
  }),
  actions: {
    async newsTagListChange() {
      const queryRequest = ({
        current: 1,
        pageSize: 100
      });
      const res = await getTagListUsingGet(queryRequest)
      this.newsTagList = res.data;
    }
  }

});
useNewsStore().newsTagListChange()
