// @ts-ignore
/* eslint-disable */
import request from "../utils/http/index";

/** getOperationHistory GET /api/user/operation/history */
export async function getOperationHistoryUsingGet(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getOperationHistoryUsingGETParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePageUserOperationLog_>(
    "/api/user/operation/history",
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
