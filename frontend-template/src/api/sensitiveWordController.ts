// @ts-ignore
/* eslint-disable */
import request from "../utils/http/index";

/** updateSensitiveWord PUT /api/sensitive-word/${param0} */
export async function updateSensitiveWordUsingPut(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.updateSensitiveWordUsingPUTParams,
  body: API.SensitiveWord,
  options?: { [key: string]: any }
) {
  const { id: param0, ...queryParams } = params;
  return request<API.BaseResponseObject_>(`/api/sensitive-word/${param0}`, {
    method: "PUT",
    headers: {
      "Content-Type": "application/json"
    },
    params: { ...queryParams },
    data: body,
    ...(options || {})
  });
}

/** addSensitiveWord POST /api/sensitive-word/add */
export async function addSensitiveWordUsingPost(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.addSensitiveWordUsingPOSTParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseObject_>("/api/sensitive-word/add", {
    method: "POST",
    params: {
      ...params
    },
    ...(options || {})
  });
}

/** deleteSensitiveWord DELETE /api/sensitive-word/delete/${param0} */
export async function deleteSensitiveWordUsingDelete(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.deleteSensitiveWordUsingDELETEParams,
  options?: { [key: string]: any }
) {
  const { id: param0, ...queryParams } = params;
  return request<API.BaseResponseObject_>(
    `/api/sensitive-word/delete/${param0}`,
    {
      method: "DELETE",
      params: { ...queryParams },
      ...(options || {})
    }
  );
}

/** getSensitiveWord GET /api/sensitive-word/list */
export async function getSensitiveWordUsingGet(options?: {
  [key: string]: any;
}) {
  return request<API.BaseResponseListSensitiveWord_>(
    "/api/sensitive-word/list",
    {
      method: "GET",
      ...(options || {})
    }
  );
}
