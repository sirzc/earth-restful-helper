package com.myth.earth.restful.model;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.myth.earth.restful.enums.HttpMethod;
import lombok.Data;

/**
 * 包含类、方法、接口信息的数据
 *
 * @author zhouchao
 * @date 2024-05-22 下午6:48
 */
@Data
public class RestfulClassMethod {
    private String     apiPath;
    private HttpMethod httpMethod;
    private PsiClass   psiClass;
    private PsiMethod  psiMethod;
    private String     moduleName;
}
