package com.myth.earth.restful.model;

import com.intellij.psi.PsiType;
import lombok.Data;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * 参数信息
 *
 * @author changan
 * @date 2022-03-11 15:22
 */
@Data
public class ParamInfo implements Serializable {

    private static final long            serialVersionUID = 1L;
    /**
     * PSI类型
     */
    private transient    PsiType         psiType;
    /**
     * 参数名
     */
    private              String          name;
    /**
     * anClass为true时存在
     */
    private              String          qualifiedName;
    /**
     * 参数描述
     */
    private              String          description;
    /**
     * 类型
     */
    private              String          paramType;
    /**
     * 是否为实体对象
     */
    private              boolean         anClass;
    /**
     * 是否为嵌套对象
     */
    private              boolean         anNestedClass;
    /**
     * 包含注解全路径
     */
    private              List<String>    annotationNames;
    /**
     * 有子属性，参数为一个对象，就存在字属性
     */
    private              List<ParamInfo> childList        = new LinkedList<>();
    /**
     * 是否必须
     */
    private              Boolean         required;
    /**
     * 示例值
     */
    private              Object          example;
}

