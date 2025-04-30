// @ts-ignore
/* eslint-disable */
import request from "../utils/http/index";

/** addSubscription POST /api/user/subscription/add */
export async function addSubscriptionUsingPost(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.addSubscriptionUsingPOSTParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean_>("/api/user/subscription/add", {
    method: "POST",
    params: {
      ...params
    },
    ...(options || {})
  });
}

/** cancelSubscription POST /api/user/subscription/cancel */
export async function cancelSubscriptionUsingPost(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.cancelSubscriptionUsingPOSTParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean_>("/api/user/subscription/cancel", {
    method: "POST",
    params: {
      ...params
    },
    ...(options || {})
  });
}

/** isSubscribed GET /api/user/subscription/check */
export async function isSubscribedUsingGet(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.isSubscribedUsingGETParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean_>("/api/user/subscription/check", {
    method: "GET",
    params: {
      ...params
    },
    ...(options || {})
  });
}

/** getUserSubscriptions GET /api/user/subscription/list */
export async function getUserSubscriptionsUsingGet(options?: {
  [key: string]: any;
}) {
  return request<API.BaseResponseListString_>("/api/user/subscription/list", {
    method: "GET",
    ...(options || {})
  });
}
