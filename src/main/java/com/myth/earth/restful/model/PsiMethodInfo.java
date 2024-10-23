package com.myth.earth.restful.model;

import com.intellij.psi.PsiMethod;
import com.myth.earth.restful.enums.HttpMethod;
import com.myth.earth.restful.enums.RequestType;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class PsiMethodInfo implements Serializable {
    private static final long            serialVersionUID = 1L;
    /**
     * 方法
     */
    private              PsiMethod       psiMethod;
    /**
     * 方法描述
     */
    private              String          methodDescribe;
    /**
     * 方法上的请求地址
     */
    private              String          methodApiPath;
    /**
     * 请求方式
     */
    private              HttpMethod      httpMethod;
    /**
     * 路径参数
     */
    private              List<ParamInfo> pathParams;
    /**
     * query参数
     */
    private              List<ParamInfo> queryParams;
    /**
     * 请求参数类型
     */
    private              RequestType     requestType;
    /**
     * 请求参数
     */
    private              List<ParamInfo> requestParams;
    /**
     * 响应信息
     */
    private              ParamInfo       responseParam;
}
