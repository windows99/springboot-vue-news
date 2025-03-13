// @ts-ignore
/* eslint-disable */
import request from "../utils/http/index";

/** getCode POST /api/common/getMailCode */
export async function getCodeUsingPost(
  body: string,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseString_>("/api/common/getMailCode", {
    method: "POST",
    headers: {
      "Content-Type": "application/json"
    },
    data: body,
    ...(options || {})
  });
}
