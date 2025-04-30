// @ts-ignore
/* eslint-disable */
import request from "../utils/http/index";

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
  return request<API.BaseResponseListMapStringObject_>(
    "/api/user/subscription/list",
    {
      method: "GET",
      ...(options || {})
    }
  );
}

/** saveSubscriptions POST /api/user/subscription/save */
export async function saveSubscriptionsUsingPost(
  body: number[],
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean_>("/api/user/subscription/save", {
    method: "POST",
    headers: {
      "Content-Type": "application/json"
    },
    data: body,
    ...(options || {})
  });
}
