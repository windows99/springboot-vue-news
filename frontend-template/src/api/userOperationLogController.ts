// @ts-ignore
/* eslint-disable */
import request from "../utils/http/index";

/** getOperationHistory GET /api/user/operation/history */
export async function getOperationHistoryUsingGet(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getOperationHistoryUsingGETParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseListUserOperationLog_>(
    "/api/user/operation/history",
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
