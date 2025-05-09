// @ts-ignore
/* eslint-disable */
import request from "../utils/http/index";

/** getRecommendForUser GET /api/news/recommend/forUser/${param0} */
export async function getRecommendForUserUsingGet(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getRecommendForUserUsingGETParams,
  options?: { [key: string]: any }
) {
  const { userId: param0, ...queryParams } = params;
  return request<API.BaseResponsePageNews_>(
    `/api/news/recommend/forUser/${param0}`,
    {
      method: "GET",
      params: {
        // current has a default value: 1
        current: "1",
        // pageSize has a default value: 10
        pageSize: "10",
        ...queryParams
      },
      ...(options || {})
    }
  );
}

/** getHotNews GET /api/news/recommend/hot */
export async function getHotNewsUsingGet(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getHotNewsUsingGETParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePageNews_>("/api/news/recommend/hot", {
    method: "GET",
    params: {
      // current has a default value: 1
      current: "1",
      // pageSize has a default value: 10
      pageSize: "10",
      ...params
    },
    ...(options || {})
  });
}
