package com.myth.earth.restful.model;

import com.myth.earth.restful.enums.HttpMethod;
import lombok.Data;

import java.util.Map;

/**
 * http 请求上下问引用
 *
 * @author zhouchao
 * @date 2024-06-15 下午3:12
 */
@Data
public class HttpRequestRef {

    /**
     * 唯一坐标
     */
    private String              unique;
    /**
     * 请求类型
     */
    private HttpMethod          httpMethod;
    /**
     * 请求地址
     */
    private String              url;
    /**
     * 请求头
     */
    private Map<String, String> headerMap;
    /**
     * 请求表单参数
     */
    private Map<String, Object> formParams;
    /**
     * 其他参数（json/text）
     */
    private String              otherParams;
}
