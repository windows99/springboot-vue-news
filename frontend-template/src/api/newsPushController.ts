// @ts-ignore
/* eslint-disable */
import request from "../utils/http/index";

/** pushNewsToAllUsers POST /api/news/push/all */
export async function pushNewsToAllUsersUsingPost(options?: {
  [key: string]: any;
}) {
  return request<API.BaseResponseInt_>("/api/news/push/all", {
    method: "POST",
    ...(options || {})
  });
}

/** pushMultipleNewsToUser POST /api/news/push/batch */
export async function pushMultipleNewsToUserUsingPost(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.pushMultipleNewsToUserUsingPOSTParams,
  body: number[],
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseInt_>("/api/news/push/batch", {
    method: "POST",
    headers: {
      "Content-Type": "application/json"
    },
    params: {
      ...params
    },
    data: body,
    ...(options || {})
  });
}

/** pushMultipleNewsToAllUsers POST /api/news/push/batch/all */
export async function pushMultipleNewsToAllUsersUsingPost(
  body: number[],
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseInt_>("/api/news/push/batch/all", {
    method: "POST",
    headers: {
      "Content-Type": "application/json"
    },
    data: body,
    ...(options || {})
  });
}

/** deleteConfig DELETE /api/news/push/config/${param0} */
export async function deleteConfigUsingDelete(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.deleteConfigUsingDELETEParams,
  options?: { [key: string]: any }
) {
  const { id: param0, ...queryParams } = params;
  return request<API.BaseResponseBoolean_>(`/api/news/push/config/${param0}`, {
    method: "DELETE",
    params: { ...queryParams },
    ...(options || {})
  });
}

/** getConfigList GET /api/news/push/config/list */
export async function getConfigListUsingGet(options?: { [key: string]: any }) {
  return request<API.BaseResponseListNewsPushConfigDTO_>(
    "/api/news/push/config/list",
    {
      method: "GET",
      ...(options || {})
    }
  );
}

/** saveConfig POST /api/news/push/config/save */
export async function saveConfigUsingPost(
  body: API.NewsPushConfigDTO,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean_>("/api/news/push/config/save", {
    method: "POST",
    headers: {
      "Content-Type": "application/json"
    },
    data: body,
    ...(options || {})
  });
}

/** updateConfigStatus POST /api/news/push/config/status/${param0} */
export async function updateConfigStatusUsingPost(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.updateConfigStatusUsingPOSTParams,
  options?: { [key: string]: any }
) {
  const { id: param0, ...queryParams } = params;
  return request<API.BaseResponseBoolean_>(
    `/api/news/push/config/status/${param0}`,
    {
      method: "POST",
      params: {
        ...queryParams
      },
      ...(options || {})
    }
  );
}

/** getUserPushHistory GET /api/news/push/history/${param0} */
export async function getUserPushHistoryUsingGet(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getUserPushHistoryUsingGETParams,
  options?: { [key: string]: any }
) {
  const { userId: param0, ...queryParams } = params;
  return request<API.BaseResponseListNewsPushDTO_>(
    `/api/news/push/history/${param0}`,
    {
      method: "GET",
      params: {
        // limit has a default value: 20
        limit: "20",
        ...queryParams
      },
      ...(options || {})
    }
  );
}

/** markPushAsRead POST /api/news/push/read/${param0} */
export async function markPushAsReadUsingPost(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.markPushAsReadUsingPOSTParams,
  options?: { [key: string]: any }
) {
  const { pushId: param0, ...queryParams } = params;
  return request<API.BaseResponseBoolean_>(`/api/news/push/read/${param0}`, {
    method: "POST",
    params: { ...queryParams },
    ...(options || {})
  });
}

/** schedulePushToAllUsers POST /api/news/push/schedule/all */
export async function schedulePushToAllUsersUsingPost(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.schedulePushToAllUsersUsingPOSTParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseInt_>("/api/news/push/schedule/all", {
    method: "POST",
    params: {
      ...params
    },
    ...(options || {})
  });
}

/** schedulePushToUser POST /api/news/push/schedule/user/${param0} */
export async function schedulePushToUserUsingPost(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.schedulePushToUserUsingPOSTParams,
  options?: { [key: string]: any }
) {
  const { userId: param0, ...queryParams } = params;
  return request<API.BaseResponseBoolean_>(
    `/api/news/push/schedule/user/${param0}`,
    {
      method: "POST",
      params: {
        ...queryParams
      },
      ...(options || {})
    }
  );
}

/** pushSpecificNews POST /api/news/push/specific */
export async function pushSpecificNewsUsingPost(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.pushSpecificNewsUsingPOSTParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean_>("/api/news/push/specific", {
    method: "POST",
    params: {
      ...params
    },
    ...(options || {})
  });
}

/** pushSpecificNewsToAll POST /api/news/push/specific/all */
export async function pushSpecificNewsToAllUsingPost(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.pushSpecificNewsToAllUsingPOSTParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseInt_>("/api/news/push/specific/all", {
    method: "POST",
    params: {
      ...params
    },
    ...(options || {})
  });
}

/** pushNewsToUser POST /api/news/push/user/${param0} */
export async function pushNewsToUserUsingPost(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.pushNewsToUserUsingPOSTParams,
  options?: { [key: string]: any }
) {
  const { userId: param0, ...queryParams } = params;
  return request<API.BaseResponseBoolean_>(`/api/news/push/user/${param0}`, {
    method: "POST",
    params: { ...queryParams },
    ...(options || {})
  });
}
