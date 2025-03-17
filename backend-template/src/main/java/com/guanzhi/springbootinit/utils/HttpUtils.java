package com.guanzhi.springbootinit.utils;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.Map;

public class HttpUtils {

    private static CloseableHttpClient httpClient;

    static {
        httpClient = HttpClientBuilder.create().build();
    }

    public static String doGet(String URL, Map<String, String> headers) throws Exception {
        HttpGet request = new HttpGet(URL);

        // 设置请求头
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            request.addHeader(entry.getKey(), entry.getValue());
        }

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            int status = response.getStatusLine().getStatusCode();
            if (status >= 200 && status < 300) {
                HttpEntity entity = response.getEntity();
                return EntityUtils.toString(entity, "UTF-8");
            } else {
                throw new Exception("HTTP Request Failed with status: " + status);
            }
        } catch (IOException e) {
            throw new Exception("HTTP Request Failed", e);
        }
    }

//    public static void main(String[] args) throws Exception {
//        // 测试调用
//        String host = "https://jisunews.market.alicloudapi.com";
//        String path = "/news/get";
//        String appcode = "71f6a5c816cb4e33b91615b08b10f392";
//        Map<String, String> headers = Map.of(
//                "Authorization", "APPCODE " + appcode,
//                "Content-Type", "application/json; charset=UTF-8"
//        );
//        String url = host + path + "?channel=gn";
//        String response = HttpUtils.doGet(url, headers);
//        System.out.println("Response: " + response);
//    }
}