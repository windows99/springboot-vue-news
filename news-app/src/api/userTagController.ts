// @ts-ignore
/* eslint-disable */
import request from "../utils/http/index";

/** 获取用户标签 GET /api/user-tag/get */
export async function getUserTagsUsingGet(
  // 用户ID参数
  params: API.getUserTagsUsingGETParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseListInteger_>("/api/user-tag/get", {
    method: "GET",
    params: {
      ...params
    },
    ...(options || {})
  });
}

/** 更新用户标签 POST /api/user-tag/update */
export async function updateUserTagsUsingPost(
  body: API.UserTagUpdateRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean_>("/api/user-tag/update", {
    method: "POST",
    headers: {
      "Content-Type": "application/json"
    },
    data: body,
    ...(options || {})
  });
} 