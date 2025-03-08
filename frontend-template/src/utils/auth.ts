import Cookies from "js-cookie";
import { storageSession } from "@pureadmin/utils";
import { useUserStoreHook } from "@/store/modules/user";
import { userType } from "@/store/modules/types";

export interface DataInfo<T> {
  /** token */
  accessToken: string;
  /** `accessToken`的过期时间（时间戳） */
  expires: T;
  /** 用于调用刷新accessToken的接口时所需的token */
  refreshToken: string;
  /** 用户名 */
  username?: string;
  /** 当前登陆用户的角色 */
  roles?: Array<string>;
}

export const sessionKey = "user-info";
export const TokenKey = "authorized-token";

export function setToken(data: userType) {
  function setSessionKey(user: API.LoginUserVO, roles: Array<string>) {
    useUserStoreHook().SET_USER(user);
    useUserStoreHook().SET_ROLES(roles);
    storageSession().setItem<userType>(sessionKey, {
      user,
      roles
    });
  }

  if (data.user && data.roles) {
    const { user, roles } = data;
    setSessionKey(user, roles);
  } else {
    const user = storageSession().getItem<userType>(sessionKey)?.user ?? {};
    const roles = storageSession().getItem<userType>(sessionKey)?.roles ?? [];
    setSessionKey(user, roles);
  }
}

/** 删除`token`以及key值为`user-info`的session信息 */
export function removeToken() {
  Cookies.remove(TokenKey);
  sessionStorage.clear();
}
