// @ts-ignore
/* eslint-disable */
import request from "../utils/http/index";

/** getViewHistory GET /api/user/views */
export async function getViewHistoryUsingGet(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getViewHistoryUsingGETParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseListUserNewsView_>("/api/user/views", {
    method: "GET",
    params: {
      ...params,
      params: undefined,
      ...params["params"]
    },
    ...(options || {})
  });
}

/** recordView POST /api/user/views */
export async function recordViewUsingPost(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.recordViewUsingPOSTParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean_>("/api/user/views", {
    method: "POST",
    params: {
      ...params
    },
    ...(options || {})
  });
}

/** deleteViewById POST /api/user/views/${param0} */
export async function deleteViewByIdUsingPost(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.deleteViewByIdUsingPOSTParams,
  options?: { [key: string]: any }
) {
  const { viewId: param0, ...queryParams } = params;
  return request<API.BaseResponseString_>(`/api/user/views/${param0}`, {
    method: "POST",
    params: {
      ...queryParams
    },
    ...(options || {})
  });
}

/** deleteAllViews POST /api/user/views/admin/all */
export async function deleteAllViewsUsingPost(options?: {
  [key: string]: any;
}) {
  return request<API.BaseResponseString_>("/api/user/views/admin/all", {
    method: "POST",
    ...(options || {})
  });
}

/** deleteAllViewsByUserId POST /api/user/views/all */
export async function deleteAllViewsByUserIdUsingPost(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.deleteAllViewsByUserIdUsingPOSTParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseString_>("/api/user/views/all", {
    method: "POST",
    params: {
      ...params
    },
    ...(options || {})
  });
}
