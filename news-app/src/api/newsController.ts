// @ts-ignore
/* eslint-disable */
import request from "../utils/http/index";

/** getNewsById GET /api/news/${param0} */
export async function getNewsByIdUsingGet(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getNewsByIdUsingGETParams,
  options?: { [key: string]: any }
) {
  const { id: param0, ...queryParams } = params;
  return request<API.BaseResponseNews_>(`/api/news/${param0}`, {
    method: "GET",
    params: {
      ...queryParams
    },
    ...(options || {})
  });
}

/** addNews POST /api/news/add */
export async function addNewsUsingPost(
  body: API.News,
  options?: { [key: string]: any }
) {
  return request<Record<string, any>>("/api/news/add", {
    method: "POST",
    headers: {
      "Content-Type": "application/json"
    },
    data: body,
    ...(options || {})
  });
}

/** deleteNews DELETE /api/news/delete/${param0} */
export async function deleteNewsUsingDelete(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.deleteNewsUsingDELETEParams,
  options?: { [key: string]: any }
) {
  const { id: param0, ...queryParams } = params;
  return request<Record<string, any>>(`/api/news/delete/${param0}`, {
    method: "DELETE",
    params: { ...queryParams },
    ...(options || {})
  });
}

/** getJisunews GET /api/news/jisunews/list */
export async function getJisunewsUsingGet(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getJisunewsUsingGETParams,
  options?: { [key: string]: any }
) {
  return request<Record<string, any>>("/api/news/jisunews/list", {
    method: "GET",
    params: {
      ...params
    },
    ...(options || {})
  });
}

/** getNewsLists POST /api/news/list */
export async function getNewsListsUsingPost(
  body: API.NewsQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePageNews_>("/api/news/list", {
    method: "POST",
    headers: {
      "Content-Type": "application/json"
    },
    data: body,
    ...(options || {})
  });
}

/** publishNews PUT /api/news/publish/${param0} */
export async function publishNewsUsingPut(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.publishNewsUsingPUTParams,
  options?: { [key: string]: any }
) {
  const { id: param0, ...queryParams } = params;
  return request<Record<string, any>>(`/api/news/publish/${param0}`, {
    method: "PUT",
    params: { ...queryParams },
    ...(options || {})
  });
}

/** shelfNews PUT /api/news/shelf/${param0} */
export async function shelfNewsUsingPut(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.shelfNewsUsingPUTParams,
  options?: { [key: string]: any }
) {
  const { id: param0, ...queryParams } = params;
  return request<Record<string, any>>(`/api/news/shelf/${param0}`, {
    method: "PUT",
    params: { ...queryParams },
    ...(options || {})
  });
}

/** getTop3NewsByViewCount GET /api/news/top3 */
export async function getTop3NewsByViewCountUsingGet(options?: {
  [key: string]: any;
}) {
  return request<API.BaseResponseListNews_>("/api/news/top3", {
    method: "GET",
    ...(options || {})
  });
}

/** updateNews PUT /api/news/update/${param0} */
export async function updateNewsUsingPut(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.updateNewsUsingPUTParams,
  body: API.News,
  options?: { [key: string]: any }
) {
  const { id: param0, ...queryParams } = params;
  return request<Record<string, any>>(`/api/news/update/${param0}`, {
    method: "PUT",
    headers: {
      "Content-Type": "application/json"
    },
    params: { ...queryParams },
    data: body,
    ...(options || {})
  });
}
