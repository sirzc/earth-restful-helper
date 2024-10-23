package com.myth.earth.restful.consts;

/**
 * swagger支持注解
 *
 * @author zhouchao
 * @date 2022/3/14 16:00
 **/
public final class SwaggerConst {

    private SwaggerConst() {
    }

    /**
     * Swagger 2.x 配置
     */
    public static final String V2_API_MODEL          = "io.swagger.annotations.ApiModel";
    public static final String V2_API_MODEL_PROPERTY = "io.swagger.annotations.ApiModelProperty";
    public static final String V2_API                = "io.swagger.annotations.Api";
    public static final String V2_API_OPERATION      = "io.swagger.annotations.ApiOperation";
    public static final String V2_API_PARAM          = "io.swagger.annotations.ApiParam";
    /**
     * Swagger 3 配置
     */
    public static final String V3_TAG                = "io.swagger.v3.oas.annotations.tags.Tag";
    public static final String V3_OPERATION          = "io.swagger.v3.oas.annotations.Operation";
    public static final String V3_PARAMETER          = "io.swagger.v3.oas.annotations.Parameter";
    public static final String V3_PARAMETERS         = "io.swagger.v3.oas.annotations.Parameters";
    public static final String V3_SCHEMA             = "io.swagger.v3.oas.annotations.media.Schema";
}
