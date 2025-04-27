// @ts-ignore
/* eslint-disable */
import request from "../utils/http/index";

/** addComment POST /api/comment */
export async function addCommentUsingPost(
  body: API.Comment,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean_>("/api/comment", {
    method: "POST",
    headers: {
      "Content-Type": "application/json"
    },
    data: body,
    ...(options || {})
  });
}

/** getCommentById GET /api/comment/${param0} */
export async function getCommentByIdUsingGet(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getCommentByIdUsingGETParams,
  options?: { [key: string]: any }
) {
  const { id: param0, ...queryParams } = params;
  return request<API.BaseResponseComment_>(`/api/comment/${param0}`, {
    method: "GET",
    params: { ...queryParams },
    ...(options || {})
  });
}

/** deleteCommentById DELETE /api/comment/${param0} */
export async function deleteCommentByIdUsingDelete(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.deleteCommentByIdUsingDELETEParams,
  options?: { [key: string]: any }
) {
  const { id: param0, ...queryParams } = params;
  return request<API.BaseResponseBoolean_>(`/api/comment/${param0}`, {
    method: "DELETE" as "GET" | "POST",
    params: { ...queryParams },
    ...(options || {})
  });
}

/** getCommentList GET /api/comment/list */
export async function getCommentListUsingGet(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getCommentListUsingGETParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseListComment_>("/api/comment/list", {
    method: "GET",
    params: {
      // page has a default value: 1
      page: "1",
      // pageSize has a default value: 10
      pageSize: "10",
      ...params
    },
    ...(options || {})
  });
}
