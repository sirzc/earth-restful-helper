package com.myth.earth.restful.helper;

import com.myth.earth.restful.enums.HttpMethod;
import com.myth.earth.restful.model.HttpRequestRef;
import org.junit.jupiter.api.Test;

/**
 * http 请求助手 测试
 *
 * @author zhouchao
 * @date 2024-06-15 下午4:10
 */
public class HttpRequestHelperTest {

    @Test
    public void get() {
        HttpRequestRef httpRequestRef = new HttpRequestRef();
        httpRequestRef.setHttpMethod(HttpMethod.GET);
        httpRequestRef.setUrl("https://api.codecopy.cn/api/tag/popular");
        String response = HttpRequestHelper.execute(httpRequestRef);
        System.out.println(response);
    }

    @Test
    public void post() {
        HttpRequestRef httpRequestRef = new HttpRequestRef();
        httpRequestRef.setHttpMethod(HttpMethod.POST);
        httpRequestRef.setUrl("https://api.codecopy.cn/api/post/list/page/vo");
        httpRequestRef.setOtherParams(
                "{\n" + "    \"searchText\": \"\",\n" + "    \"current\": 1,\n" + "    \"codeLanguage\": \"java\",\n" + "    \"descSortField\": [\n"
                        + "        \"priority\",\n" + "        \"createTime\"\n" + "    ],\n" + "    \"pageSize\": 12,\n" + "    \"reviewStatus\": 1\n" + "}");
        String response = HttpRequestHelper.execute(httpRequestRef);
        System.out.println(response);
    }

    @Test
    public void error() {
        try {
            HttpRequestRef httpRequestRef = new HttpRequestRef();
            httpRequestRef.setHttpMethod(HttpMethod.HEAD);
            httpRequestRef.setUrl("https://api.xxxxx.cn/api/tag/popular");
            String response = HttpRequestHelper.execute(httpRequestRef);
            System.out.println(response);
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
