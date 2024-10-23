package com.myth.earth.restful.enums;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * javax.ws.rs 对应的 http method
 *
 * @author zhouchao
 * @date 2022/4/12 17:58
 **/
public enum JaxRsHttpMethod {

    /**
     * GET
     */
    GET("javax.ws.rs.GET", HttpMethod.GET),
    /**
     * POST
     */
    POST("javax.ws.rs.POST", HttpMethod.POST),
    /**
     * PUT
     */
    PUT("javax.ws.rs.PUT", HttpMethod.PUT),
    /**
     * DELETE
     */
    DELETE("javax.ws.rs.DELETE", HttpMethod.DELETE),
    /**
     * HEAD
     */
    HEAD("javax.ws.rs.HEAD", HttpMethod.HEAD),
    /**
     * PATCH
     */
    PATCH("javax.ws.rs.PATCH", HttpMethod.PATCH);

    private String     qualifiedName;
    private HttpMethod method;

    JaxRsHttpMethod(String qualifiedName, HttpMethod method) {
        this.qualifiedName = qualifiedName;
        this.method = method;
    }

    @Nullable
    public static JaxRsHttpMethod getMethodByQualifiedName(String qualifiedName) {
        for (JaxRsHttpMethod httpMethod : JaxRsHttpMethod.values()) {
            if (httpMethod.getQualifiedName().equals(qualifiedName)) {
                return httpMethod;
            }
        }
        return null;
    }

    public HttpMethod getMethod() {
        return this.method;
    }

    public String getQualifiedName() {
        return qualifiedName;
    }

    @NotNull
    public String getShortName() {
        return qualifiedName.substring(qualifiedName.lastIndexOf(".") - 1);
    }

    @NotNull
    public static List<String> getAllSupportMethod() {
        return Arrays.stream(JaxRsHttpMethod.values()).map(JaxRsHttpMethod::getQualifiedName).collect(Collectors.toList());
    }
}