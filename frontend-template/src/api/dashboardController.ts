// @ts-ignore
/* eslint-disable */
import request from "../utils/http/index";

/** getDashboardData GET /api/dashboard/data */
export async function getDashboardDataUsingGet(options?: {
  [key: string]: any;
}) {
  return request<API.BaseResponseDashboardVO_>("/api/dashboard/data", {
    method: "GET",
    ...(options || {})
  });
}
