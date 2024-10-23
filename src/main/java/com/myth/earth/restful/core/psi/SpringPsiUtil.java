package com.myth.earth.restful.core.psi;

import com.google.common.collect.Lists;
import com.intellij.psi.*;
import com.myth.earth.restful.consts.SpringPackageConst;
import com.myth.earth.restful.enums.HttpMethod;
import com.myth.earth.restful.kits.PsiKit;
import com.myth.earth.restful.model.ParamInfo;
import com.myth.earth.restful.utils.DataExtractUtil;
import com.myth.earth.restful.utils.ParamInfoPsiUtil;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

/**
 * spring 类型的psi操作
 *
 * @author changan
 * @date 2023-05-06 18:30
 */
public class SpringPsiUtil {

    /**
     * 获取spring接口方法的请求路径的注解类型
     *
     * @param psiMethod 接口方法
     * @return 请求路径的注解类型
     */
    @Nullable
    public static PsiAnnotation getHttpMethodAnnotation(@NotNull PsiMethod psiMethod) {
        PsiAnnotation[] annotations = psiMethod.getAnnotations();
        for (PsiAnnotation annotation : annotations) {
            if (SpringPackageConst.SUPPORT_METHOD_ANNOTATIONS.contains(annotation.getQualifiedName())) {
                return annotation;
            }
        }
        return null;
    }

    /**
     * 获取spring接口方法的请求类型
     *
     * @param annotation 请求路径的注解类型
     * @return 请求类型
     */
    public static HttpMethod getMethodType(@NotNull PsiAnnotation annotation) {
        String qualifiedName = annotation.getQualifiedName();

        // 针对方法注解：@RequestMapping
        if (StringUtils.equals(SpringPackageConst.METHOD_REQUEST_MAPPING, qualifiedName)) {
            PsiAnnotationParameterList parameterList = annotation.getParameterList();
            for (PsiNameValuePair attribute : parameterList.getAttributes()) {
                if (attribute.getAttributeName().contains("method")) {
                    String text = Optional.ofNullable(attribute.getValue()).map(PsiAnnotationMemberValue::getText).orElse(null);
                    return HttpMethod.getHttpMethod(text);
                }
            }
        }

        // 针对类型注解：@GetMapping、@PostMapping、@PutMapping、@DeleteMapping、@PatchMapping
        for (String methodAnnotation : SpringPackageConst.SUPPORT_METHOD_ANNOTATIONS) {
            if (methodAnnotation.equalsIgnoreCase(qualifiedName)) {
                return HttpMethod.getHttpMethod(methodAnnotation);
            }
        }
        return HttpMethod.REQUEST;
    }

    @NotNull
    public static List<ParamInfo> analyzeInputParam(@NotNull PsiMethod psiMethod) {
        PsiParameter[] psiParameters = psiMethod.getParameterList().getParameters();
        List<ParamInfo> paramInfos = Lists.newArrayListWithCapacity(psiParameters.length);
        // 获取方法上的
        for (PsiParameter psiParameter : psiParameters) {
            if (DataExtractUtil.isExcludeParameter(psiParameter)) {
                continue;
            }
            ParamInfo paramInfo = ParamInfoPsiUtil.buildInputParam(psiParameter);
            // 将PathVariable注解的value作为参数名
            Optional.ofNullable(PsiKit.getPropertyFromAnnotation(psiParameter, SpringPackageConst.PATH_VARIABLE_NAME, "value")).ifPresent(paramInfo::setName);
            // 将RequestParam注解的value作为参数名
            Optional.ofNullable(PsiKit.getPropertyFromAnnotation(psiParameter, SpringPackageConst.REQUEST_PARAM_NAME, "value")).ifPresent(paramInfo::setName);
            paramInfos.add(paramInfo);
        }
        return paramInfos;
    }
}
