package com.myth.earth.restful.core.analyze.handlers;

import com.google.common.collect.Lists;
import com.intellij.codeInsight.AnnotationUtil;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiType;
import com.myth.earth.restful.consts.DataTypeConst;
import com.myth.earth.restful.consts.SpringPackageConst;
import com.myth.earth.restful.core.psi.SpringPsiUtil;
import com.myth.earth.restful.enums.RequestType;
import com.myth.earth.restful.kits.PsiKit;
import com.myth.earth.restful.model.ParamInfo;
import com.myth.earth.restful.model.PsiClassInfo;
import com.myth.earth.restful.model.PsiMethodInfo;
import com.myth.earth.restful.utils.DataExtractUtil;
import com.myth.earth.restful.utils.ParamInfoPsiUtil;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Spring 风格接口处理
 * @date 2023-04-25 19:40
 */
public class SpringAnalyzeDataHandler extends AbstractAnalyzeData {

    @Override
    public boolean supportClass(@NotNull PsiClass psiClass) {
        if (!super.supportClass(psiClass)) {
            return false;
        }
        return !psiClass.isInterface() && AnnotationUtil.isAnnotated(psiClass, SpringPackageConst.SUPPORT_CLASS_ANNOTATIONS, 0);
    }

    @Override
    public boolean supportMethod(@NotNull PsiMethod psiMethod) {
        // 判断当前方法是否为Spring类型注解，且是公开非静态的方法
        return PsiKit.supportApiMethod(psiMethod, SpringPackageConst.SUPPORT_METHOD_ANNOTATIONS);
    }

    @NotNull
    @Override
    public PsiClassInfo analyzeByClass(@NotNull PsiClass psiClass, boolean isQuickScan) {
        PsiClassInfo psiClassInfo = getPsiClassInfo(psiClass);
        PsiMethod[] methods = psiClass.getMethods();
        if (methods.length == 0) {
            return psiClassInfo;
        }
        List<PsiMethodInfo> psiMethodInfos = new ArrayList<>(methods.length);
        for (PsiMethod method : methods) {
            if (!supportMethod(method)) {
                continue;
            }
            psiMethodInfos.add(getPsiMethodInfo(method, isQuickScan));
        }
        psiClassInfo.setPsiMethodInfos(psiMethodInfos);
        return psiClassInfo;
    }

    @NotNull
    @Override
    public PsiClassInfo analyzeByMethod(@NotNull PsiClass psiClass, @NotNull PsiMethod psiMethod) {
        PsiClassInfo psiClassInfo = getPsiClassInfo(psiClass);
        // 补充方法信息
        if (!supportMethod(psiMethod)) {
            return psiClassInfo;
        }
        psiClassInfo.setPsiMethodInfos(Lists.newArrayList(getPsiMethodInfo(psiMethod, false)));
        return psiClassInfo;
    }

    /**
     * 获取类信息
     *
     * @param psiClass 类
     * @return 类信息
     */
    private PsiClassInfo getPsiClassInfo(@NotNull PsiClass psiClass) {
        PsiClassInfo psiClassInfo = new PsiClassInfo();
        psiClassInfo.setPsiClass(psiClass);
        psiClassInfo.setClassDescribe(DataExtractUtil.getClassDescribe(psiClass));
        PsiAnnotation annotation = AnnotationUtil.findAnnotation(psiClass, SpringPackageConst.METHOD_REQUEST_MAPPING);
        // 获取类上的路径信息
        if (Objects.nonNull(annotation)) {
            psiClassInfo.setClassApiPath(AnnotationUtil.getStringAttributeValue(annotation, "value"));
        }
        return psiClassInfo;
    }

    /**
     * 获取方法信息
     *
     * @param psiMethod   方法
     * @param isQuickScan 是否快速扫描
     * @return 方法信息
     */
    private PsiMethodInfo getPsiMethodInfo(@NotNull PsiMethod psiMethod, boolean isQuickScan) {
        PsiMethodInfo psiMethodInfo = new PsiMethodInfo();
        psiMethodInfo.setPsiMethod(psiMethod);
        psiMethodInfo.setMethodDescribe(DataExtractUtil.getMethodDescribe(psiMethod));
        // 获取方法上的路径信息
        PsiAnnotation annotation = SpringPsiUtil.getHttpMethodAnnotation(psiMethod);
        if (Objects.nonNull(annotation)){
            String value = AnnotationUtil.getStringAttributeValue(annotation, "value");
            psiMethodInfo.setMethodApiPath(StringUtils.isNotBlank(value) ? value : "/");
            psiMethodInfo.setHttpMethod(SpringPsiUtil.getMethodType(annotation));
        }
        // 有返回值
        PsiType returnType = psiMethod.getReturnType();
        if (Objects.nonNull(returnType) && returnType.isValid() && !returnType.equalsToText(DataTypeConst.VOID_MARK)) {
            psiMethodInfo.setResponseParam(ParamInfoPsiUtil.analyzeOutputParam(returnType));
        }
        // 设置请求参数类型：json、form
        psiMethodInfo.setRequestType(RequestType.RAW_JSON);
        // 无参数，直接返回
        if (!psiMethod.hasParameters()) {
            return psiMethodInfo;
        }

        // 获取请求参数
        List<ParamInfo> roots = SpringPsiUtil.analyzeInputParam(psiMethod);
        List<ParamInfo> pathParams = Lists.newArrayListWithCapacity(roots.size());
        List<ParamInfo> queryParams = Lists.newArrayListWithCapacity(roots.size());
        List<ParamInfo> requestParams = Lists.newArrayListWithCapacity(roots.size());
        for (ParamInfo paramInfo : roots) {
            List<String> annotationNames = paramInfo.getAnnotationNames();
            boolean anClass = paramInfo.isAnClass();
            // 如果是 @PathVariable 注解，则认为是路径参数
            if (!anClass && annotationNames.contains(SpringPackageConst.PATH_VARIABLE_NAME)) {
                pathParams.add(paramInfo);
                continue;
            }
            // 如果是 @RequestParam 则认为是查询参数
            if (!anClass && annotationNames.contains(SpringPackageConst.REQUEST_PARAM_NAME)) {
                queryParams.add(paramInfo);
                continue;
            }
            requestParams.add(paramInfo);
        }
        psiMethodInfo.setPathParams(pathParams);
        psiMethodInfo.setQueryParams(queryParams);
        psiMethodInfo.setRequestParams(requestParams);
        return psiMethodInfo;
    }
}
