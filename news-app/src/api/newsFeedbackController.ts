// @ts-ignore
/* eslint-disable */
import request from "../utils/http/index";

/** deleteFeedback POST /api/news/feedback/delete/${param0} */
export async function deleteFeedbackUsingPost(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.deleteFeedbackUsingPOSTParams,
  options?: { [key: string]: any }
) {
  const { id: param0, ...queryParams } = params;
  return request<API.BaseResponseBoolean_>(
    `/api/news/feedback/delete/${param0}`,
    {
      method: "POST",
      params: { ...queryParams },
      ...(options || {})
    }
  );
}

/** getFeedbackDetail GET /api/news/feedback/detail/${param0} */
export async function getFeedbackDetailUsingGet(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getFeedbackDetailUsingGETParams,
  options?: { [key: string]: any }
) {
  const { id: param0, ...queryParams } = params;
  return request<API.BaseResponseNewsFeedback_>(
    `/api/news/feedback/detail/${param0}`,
    {
      method: "GET",
      params: { ...queryParams },
      ...(options || {})
    }
  );
}

/** getMyFeedbackList GET /api/news/feedback/list */
export async function getMyFeedbackListUsingGet(options?: {
  [key: string]: any;
}) {
  return request<API.BaseResponseListNewsFeedback_>("/api/news/feedback/list", {
    method: "GET",
    ...(options || {})
  });
}

/** getPendingFeedbackList GET /api/news/feedback/pending */
export async function getPendingFeedbackListUsingGet(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getPendingFeedbackListUsingGETParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePageNewsFeedbackVO_>(
    "/api/news/feedback/pending",
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

/** reviewFeedback POST /api/news/feedback/review */
export async function reviewFeedbackUsingPost(
  body: API.NewsFeedbackReviewRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean_>("/api/news/feedback/review", {
    method: "POST",
    headers: {
      "Content-Type": "application/json"
    },
    data: body,
    ...(options || {})
  });
}

/** submitFeedback POST /api/news/feedback/submit */
export async function submitFeedbackUsingPost(
  body: API.NewsFeedbackRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean_>("/api/news/feedback/submit", {
    method: "POST",
    headers: {
      "Content-Type": "application/json"
    },
    data: body,
    ...(options || {})
  });
}
