// @ts-ignore
/* eslint-disable */
import request from "../utils/http/index";

/** pushBatch POST /api/api/news/push/batch */
export async function pushBatchUsingPost(
  body: Record<string, any>,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseInt_>("/api/api/news/push/batch", {
    method: "POST",
    headers: {
      "Content-Type": "application/json"
    },
    data: body,
    ...(options || {})
  });
}

/** pushImmediate POST /api/api/news/push/immediate */
export async function pushImmediateUsingPost(
  body: Record<string, any>,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean_>("/api/api/news/push/immediate", {
    method: "POST",
    headers: {
      "Content-Type": "application/json"
    },
    data: body,
    ...(options || {})
  });
}

/** pushPersonalized POST /api/api/news/push/personalized */
export async function pushPersonalizedUsingPost(
  body: Record<string, any>,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseInt_>("/api/api/news/push/personalized", {
    method: "POST",
    headers: {
      "Content-Type": "application/json"
    },
    data: body,
    ...(options || {})
  });
}

/** markAsRead POST /api/api/news/push/read */
export async function markAsReadUsingPost(
  body: Record<string, any>,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean_>("/api/api/news/push/read", {
    method: "POST",
    headers: {
      "Content-Type": "application/json"
    },
    data: body,
    ...(options || {})
  });
}

/** getPushRecordDetail GET /api/api/news/push/record/${param0} */
export async function getPushRecordDetailUsingGet(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getPushRecordDetailUsingGETParams,
  options?: { [key: string]: any }
) {
  const { recordId: param0, ...queryParams } = params;
  return request<API.BaseResponseNewsPush_>(
    `/api/api/news/push/record/${param0}`,
    {
      method: "GET",
      params: { ...queryParams },
      ...(options || {})
    }
  );
}

/** listPushRecords GET /api/api/news/push/record/list */
export async function listPushRecordsUsingGet(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.listPushRecordsUsingGETParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePageNewsPushVO_>(
    "/api/api/news/push/record/list",
    {
      method: "GET",
      params: {
        // current has a default value: 1
        current: "1",

        // pageSize has a default value: 10
        pageSize: "10",
        ...params
      },
      ...(options || {})
    }
  );
}

/** getUnreadPushes GET /api/api/news/push/unread */
export async function getUnreadPushesUsingGet(options?: {
  [key: string]: any;
}) {
  return request<API.BaseResponseListNewsPushDTO_>(
    "/api/api/news/push/unread",
    {
      method: "GET",
      ...(options || {})
    }
  );
}
