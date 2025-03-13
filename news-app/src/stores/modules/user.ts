import { defineStore } from "pinia";
import { store } from "@/stores";
import { storageSession } from "@pureadmin/utils";
import { useMultiTagsStoreHook } from "@/stores/modules/multiTags";
import { removeToken, sessionKey } from "@/utils/auth";
import { userType } from "@/stores/modules/types";
import { userLogoutUsingPost } from "@/api/userController";

export const useUserStore = defineStore("pure-user", {

  state: () => ({
    // 用户
    user: storageSession().getItem<userType>(sessionKey)?.user ?? {},
    // 页面级别权限
    roles: storageSession().getItem<userType>(sessionKey)?.roles ?? []
  }),
  actions: {
    /** 存储用户 */
    SET_USER(user: API.LoginUserVO) {
      this.user = user;
    },
    /** 存储用户 */
    SET_ROLES(roles: Array<string>) {
      this.roles = roles;
    },
    /** 前端登出（不调用接口） */
    async logOut() {
      await userLogoutUsingPost();
      this.user = {};
      this.username = "";
      this.roles = [];
      removeToken();
    }
  }
});

export function useUserStoreHook() {
  return useUserStore(store);
}
