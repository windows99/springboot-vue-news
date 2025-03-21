// @ts-ignore
/* eslint-disable */
import request from "../utils/http/index";

/** addTag POST /api/new-tag */
export async function addTagUsingPost(
  body: API.NewsTag,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseObject_>("/api/new-tag", {
    method: "POST",
    headers: {
      "Content-Type": "application/json"
    },
    data: body,
    ...(options || {})
  });
}

/** getTag GET /api/new-tag/${param0} */
export async function getTagUsingGet(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getTagUsingGETParams,
  options?: { [key: string]: any }
) {
  const { id: param0, ...queryParams } = params;
  return request<API.BaseResponseNewsTag_>(`/api/new-tag/${param0}`, {
    method: "GET",
    params: { ...queryParams },
    ...(options || {})
  });
}

/** updateTag PUT /api/new-tag/${param0} */
export async function updateTagUsingPut(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.updateTagUsingPUTParams,
  body: API.NewsTag,
  options?: { [key: string]: any }
) {
  const { id: param0, ...queryParams } = params;
  console.log(queryParams)
  return request<API.BaseResponseObject_>(`/api/new-tag/${param0}`, {
    method: "PUT",
    headers: {
      "Content-Type": "application/json"
    },
    params: { ...queryParams },
    data: body,
    ...(options || {})
  });
}

/** deleteTag DELETE /api/new-tag/${param0} */
export async function deleteTagUsingDelete(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.deleteTagUsingDELETEParams,
  options?: { [key: string]: any }
) {
  const { id: param0, ...queryParams } = params;
  return request<API.BaseResponseObject_>(`/api/new-tag/${param0}`, {
    method: "DELETE",
    params: { ...queryParams },
    ...(options || {})
  });
}

/** getTagList GET /api/new-tag/list */
export async function getTagListUsingGet(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getTagListUsingGETParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseListNewsTag_>("/api/new-tag/list", {
    method: "GET",
    params: {
      // page has a default value: 1
      page: "1",
      // pageSize has a default value: 100
      pageSize: "100",
      ...params
    },
    ...(options || {})
  });
}
