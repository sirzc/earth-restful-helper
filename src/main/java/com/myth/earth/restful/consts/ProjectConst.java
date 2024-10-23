package com.myth.earth.restful.consts;

import com.google.common.collect.Sets;

import java.util.Set;

/**
 * 项目中的固定常量
 *
 * @author changan
 * @date 2023-05-03 16:47
 */
public final class ProjectConst {

    /**
     * idea项目运行环境目录
     */
    public static final String      IDEA                     = ".idea";
    /**
     * 项目输出目录名称
     */
    public static final String      PROJECT_OUTPUT_DIRECTORY = "earth-restful-helper";
    /**
     * 默认地址
     */
    public static final String      DEFAULT_HOST             = "http://localhost:8080";
    /**
     * 项目根路径
     */
    public static final String      API_URL_DELIMITER        = "/";
    /**
     * 请求头
     */
    public static final String      CONTENT_TYPE             = "Content-Type";
    /**
     * 自定义方法上的API描述注解
     */
    public static final Set<String> METHOD_API_NAME          = Sets.newHashSet("description");
    /**
     * curl 命令模版
     */
    public static final String      CURL_FORMAT              = "curl --location --request %s '%s' \\\n--header 'Content-Type: %s' \\\n";
}
