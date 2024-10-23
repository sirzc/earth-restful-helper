package com.myth.earth.restful.consts;

import com.google.common.collect.Sets;

import java.util.Set;

/**
 * spring package 常量
 *
 * @author zhouchao
 * @date 2022/3/8 8:59
 **/
public final class SpringPackageConst {

    private SpringPackageConst() {
    }

    /**
     * controller
     */
    public static final String CLASS_CONTROLLER      = "org.springframework.stereotype.Controller";
    public static final String CLASS_REST_CONTROLLER = "org.springframework.web.bind.annotation.RestController";

    /**
     * mapping
     */
    public static final String METHOD_GET_MAPPING  = "org.springframework.web.bind.annotation.GetMapping";
    public static final String METHOD_POST_MAPPING = "org.springframework.web.bind.annotation.PostMapping";
    public static final String METHOD_PUT_MAPPING    = "org.springframework.web.bind.annotation.PutMapping";
    public static final String METHOD_DELETE_MAPPING = "org.springframework.web.bind.annotation.DeleteMapping";
    public static final String METHOD_PATCH_MAPPING  = "org.springframework.web.bind.annotation.PatchMapping";
    public static final String METHOD_REQUEST_MAPPING = "org.springframework.web.bind.annotation.RequestMapping";

    /**
     * param
     */
    public static final String PATH_VARIABLE_NAME      = "org.springframework.web.bind.annotation.PathVariable";
    public static final String REQUEST_PARAM_NAME  = "org.springframework.web.bind.annotation.RequestParam";
    public static final String REQUEST_BODY_NAME       = "org.springframework.web.bind.annotation.RequestBody";

    /**
     * RequestAttribute("name") String name会获取request作用范围中名为"name"的属性的值赋给方法的参数name
     * SessionAttribute("sex") String  sex会获取session作用范围中名为"sex"的属性的值赋给方法的参数sex
     */
    public static final String REQUEST_ATTRIBUTE_NAME  = "org.springframework.web.bind.annotation.RequestAttribute";
    public static final String SESSION_ATTRIBUTE_NAME  = "org.springframework.web.bind.annotation.SessionAttribute";
    public static final String SESSION_ATTRIBUTES_NAME = "org.springframework.web.bind.annotation.SessionAttributes";

    /**
     * 以下暂不处理
     */
    public static final String CLASS_FEIGN_CLIENT    = "org.springframework.cloud.openfeign.FeignClient";
    public static final String REQUEST_PART_NAME   = "org.springframework.web.bind.annotation.RequestPart";
    public static final String MAPPING_NAME           = "org.springframework.web.bind.annotation.Mapping";

    /**
     * header
     */
    public static final String REQUEST_HEADER_NAME = "org.springframework.web.bind.annotation.RequestHeader";
    public static final String RESPONSE_BODY_NAME   = "org.springframework.web.bind.annotation.ResponseBody";
    public static final String RESPONSE_STATUS_NAME = "org.springframework.web.bind.annotation.ResponseStatus";

    /**
     * 校验
     */
    public static final String VALIDATED_NAME = "org.springframework.validation.annotation.Validated";

    /**
     * other
     */
    public static final String CONTROLLER_ADVICE_NAME      = "org.springframework.web.bind.annotation.ControllerAdvice";
    public static final String REST_CONTROLLER_ADVICE_NAME = "org.springframework.web.bind.annotation.RestControllerAdvice";
    public static final String COOKIE_VALUE_NAME           = "org.springframework.web.bind.annotation.CookieValue";
    public static final String CROSS_ORIGIN_NAME           = "org.springframework.web.bind.annotation.CrossOrigin";
    public static final String EXCEPTION_HANDLER_NAME = "org.springframework.web.bind.annotation.ExceptionHandler";
    public static final String INIT_BINDER_NAME       = "org.springframework.web.bind.annotation.InitBinder";
    public static final String MATRIX_VARIABLE_NAME   = "org.springframework.web.bind.annotation.MatrixVariable";
    public static final String MODEL_ATTRIBUTE_NAME   = "org.springframework.web.bind.annotation.ModelAttribute";

    /**
     * 支持的类注解
     */
    public static final Set<String> SUPPORT_CLASS_ANNOTATIONS = Sets.newHashSet(SpringPackageConst.CLASS_CONTROLLER,
                                                                                SpringPackageConst.CLASS_REST_CONTROLLER,
                                                                                SpringPackageConst.METHOD_REQUEST_MAPPING);

    /**
     * 支持的方法注解
     */
    public static final Set<String> SUPPORT_METHOD_ANNOTATIONS = Sets.newHashSet(SpringPackageConst.METHOD_GET_MAPPING,
                                                                                 SpringPackageConst.METHOD_POST_MAPPING,
                                                                                 SpringPackageConst.METHOD_PUT_MAPPING,
                                                                                 SpringPackageConst.METHOD_DELETE_MAPPING,
                                                                                 SpringPackageConst.METHOD_PATCH_MAPPING,
                                                                                 SpringPackageConst.METHOD_REQUEST_MAPPING);
}
