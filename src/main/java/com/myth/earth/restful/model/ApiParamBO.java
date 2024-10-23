package com.myth.earth.restful.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ApiParamBO implements Serializable {

    private static final long             serialVersionUID = 1L;
    /**
     * 参数名称
     */
    private              String           name;
    /**
     * 参数类型
     */
    private              String           type;
    /**
     * 示例值
     */
    private              String         value;
    /**
     * 参数描述
     */
    private              String         desc;
    /**
     * 是否必填
     */
    private              Boolean        required;
    /**
     * 是否启用
     */
    private              Boolean          enable;
    /**
     * 子参数：可为null
     */
    private              List<ApiParamBO> childParams;
}
