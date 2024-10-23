package com.myth.earth.restful.model;

import com.myth.earth.restful.enums.HttpMethod;
import com.myth.earth.restful.enums.RequestType;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ApiDetail implements Serializable {
    private static final long            serialVersionUID = 1L;
    /**
     * API 所属范围名称
     */
    private              String          apiModelName;
    /**
     * API 名称
     */
    private              String          apiName;
    /**
     * API 路径 （class + method）
     */
    private              String          apiPath;
    /**
     * API 示例路径 ： class + method + params
     */
    private              String          apiExamplePath;
    /**
     * 请求类型：GET、POST、PUT、DELETE、HEAD、PATCH
     */
    private              HttpMethod      httpMethod;
    /**
     * PATH 参数
     */
    private              List<ParamInfo> pathParams;
    /**
     * Query 参数
     */
    private              List<ParamInfo> queryParams;
    /**
     * 请求参数类型：JSON、RAW_TEXT、FORM_URLENCODED
     */
    private              RequestType     requestType;
    /**
     * 请求参数
     */
    private              List<ParamInfo> requestParams;
    /**
     * 响应参数
     */
    private              ParamInfo       responseParam;
}