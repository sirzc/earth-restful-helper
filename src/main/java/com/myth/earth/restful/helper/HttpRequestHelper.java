package com.myth.earth.restful.helper;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.myth.earth.restful.model.HttpRequestRef;
import org.jetbrains.annotations.NotNull;

/**
 * http 请求助手
 *
 * @author zhouchao
 * @date 2024-06-15 下午3:53
 */
public class HttpRequestHelper {

    public static String execute(@NotNull HttpRequestRef ref) {
        // 执行GET请求
        HttpRequest httpRequest;
        switch (ref.getHttpMethod()){
            case GET:
                httpRequest = HttpRequest.get(ref.getUrl());
                break;
            case POST:
                httpRequest = HttpRequest.post(ref.getUrl());
                break;
            case PUT:
                httpRequest = HttpRequest.put(ref.getUrl());
                break;
            case DELETE:
                httpRequest = HttpRequest.delete(ref.getUrl());
                break;
            default:
                throw new IllegalArgumentException("不支持的请求类型：" + ref.getHttpMethod());
        }
        httpRequest.addHeaders(ref.getHeaderMap()).form(ref.getFormParams()).body(ref.getOtherParams());
        try (HttpResponse httpResponse = httpRequest.execute()) {
            return httpResponse.body();
        }
    }
}
