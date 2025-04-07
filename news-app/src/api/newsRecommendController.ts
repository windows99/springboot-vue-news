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
  return request<API.BaseResponseListNewsRecommendDTO_>(
    `/api/news/recommend/forUser/${param0}`,
    {
      method: "GET",
      params: {
        // limit has a default value: 10
        limit: "10",
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
  return request<API.BaseResponseListNewsRecommendDTO_>(
    "/api/news/recommend/hot",
    {
      method: "GET",
      params: {
        // limit has a default value: 10
        limit: "10",
        ...params
      },
      ...(options || {})
    }
  );
}

/** getPersonalRecommend GET /api/news/recommend/personal */
export async function getPersonalRecommendUsingGet(options?: {
  [key: string]: any;
}) {
  return request<API.BaseResponseListNewsRecommendDTO_>(
    "/api/news/recommend/personal",
    {
      method: "GET",
      ...(options || {})
    }
  );
}

/** pushNews POST /api/news/recommend/push */
export async function pushNewsUsingPost(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.pushNewsUsingPOSTParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean_>("/api/news/recommend/push", {
    method: "POST",
    params: {
      ...params
    },
    ...(options || {})
  });
}

/** testWebSocket GET /api/news/recommend/test-ws/${param0} */
export async function testWebSocketUsingGet(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.testWebSocketUsingGETParams,
  options?: { [key: string]: any }
) {
  const { userId: param0, ...queryParams } = params;
  return request<API.BaseResponseBoolean_>(
    `/api/news/recommend/test-ws/${param0}`,
    {
      method: "GET",
      params: {
        ...queryParams
      },
      ...(options || {})
    }
  );
}
