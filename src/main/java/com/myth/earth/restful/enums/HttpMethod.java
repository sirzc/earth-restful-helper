package com.myth.earth.restful.enums;

import com.google.common.collect.Lists;
import com.myth.earth.restful.utils.TextIconUtil;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.Arrays;
import java.util.List;

/**
 * 方法类型枚举
 *
 * @author zhouchao
 * @date 2022-03-31 17:41
 */
public enum HttpMethod {
    /**
     * Request
     */
    REQUEST,
    /**
     * GET
     */
    GET,
    /**
     * OPTIONS
     */
    OPTIONS,
    /**
     * POST
     */
    POST,
    /**
     * PUT
     */
    PUT,
    /**
     * DELETE
     */
    DELETE,
    /**
     * PATCH
     */
    PATCH,
    /**
     * HEAD
     */
    HEAD,
    /**
     * TRACE
     */
    TRACE;

    @NotNull
    public static HttpMethod[] getValues() {
        return Arrays.stream(HttpMethod.values()).filter(method -> !method.equals(HttpMethod.REQUEST)).toArray(HttpMethod[]::new);
    }

    @NotNull
    public static HttpMethod parse(@NotNull Object method) {
        try {
            if (method instanceof HttpMethod) {
                return (HttpMethod) method;
            }
            return HttpMethod.valueOf(method.toString());
        } catch (Exception exception) {
            return REQUEST;
        }
    }

    @NotNull
    public static HttpMethod getHttpMethod(String method) {
        if (StringUtils.isBlank(method)) {
            return REQUEST;
        }

        for (HttpMethod value : HttpMethod.values()) {
            if (value.equals(REQUEST)) {
                continue;
            }

            if (StringUtils.containsIgnoreCase(method, value.name())) {
                return value;
            }
        }
        return REQUEST;
    }

    public Icon getIcon() {
        switch (this) {
            case GET:
                return GET_ICON;
            case PUT:
                return PUT_ICON;
            case POST:
                return POST_ICON;
            case DELETE:
                return DELETE_ICON;
            default:
                return DEFAULT_ICON;
        }
    }

    public static final List<HttpMethod> TYPES = Lists.newArrayList(HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT, HttpMethod.DELETE);

    @NotNull
    private static final Icon POST_ICON    = TextIconUtil.createTextIcon("POST", TextIconUtil.WARNING_COLOR);
    @NotNull
    private static final Icon GET_ICON     = TextIconUtil.createTextIcon("GET", TextIconUtil.NORMAL_COLOR);
    @NotNull
    private static final Icon DELETE_ICON  = TextIconUtil.createTextIcon("DEL", TextIconUtil.ERROR_COLOR);
    @NotNull
    private static final Icon PUT_ICON     = TextIconUtil.createTextIcon("PUT", TextIconUtil.BLUE_COLOR);
    @NotNull
    private static final Icon DEFAULT_ICON = TextIconUtil.createTextIcon("DEF", TextIconUtil.GRAY_COLOR);

}
