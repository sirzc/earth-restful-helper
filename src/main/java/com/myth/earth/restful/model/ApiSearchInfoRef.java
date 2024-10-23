package com.myth.earth.restful.model;

import com.intellij.psi.PsiMethod;
import com.myth.earth.restful.enums.HttpMethod;
import lombok.Data;

/**
 * 接口类引用
 *
 * @author zhouchao
 * @date 2024-05-31 8:29
 */
@Data
public class ApiSearchInfoRef {
    private String     apiPath;
    private String     apiName;
    private String     description;
    private String     classMethodPath;
    private PsiMethod  psiMethod;
    private HttpMethod httpMethod;
}

