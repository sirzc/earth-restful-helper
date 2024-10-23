package com.myth.earth.restful.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.common.collect.Maps;
import com.intellij.codeInsight.AnnotationUtil;
import com.intellij.psi.*;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.javadoc.PsiDocTag;
import com.intellij.psi.util.InheritanceUtil;
import com.intellij.psi.util.PsiUtil;
import com.myth.earth.restful.consts.DataTypeConst;
import com.myth.earth.restful.consts.ProjectConst;
import com.myth.earth.restful.consts.ProjectSetting;
import com.myth.earth.restful.consts.SwaggerConst;
import com.myth.earth.restful.kits.PsiKit;
import com.myth.earth.restful.model.ParamInfo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * 类分析工具
 *
 * @author changan
 * @date 2023-04-25 21:09
 */
public final class DataExtractUtil {
    /**
     * 版权信息
     */
    private static final String COPYRIGHT = "Copyright";

    /**
     * 从类中获取类描述
     *
     * @param psiClass psiClass
     * @return 非空，类名称
     */
    @NotNull
    public static String getClassDescribe(@NotNull PsiClass psiClass) {
        PsiDocComment psiDocComment = psiClass.getDocComment();
        // 类注释
        String comment = PsiKit.getDocComment(psiDocComment);
        // 注释里只有版权信息，就不要了
        if (StringUtils.isNotBlank(comment) && !StringUtils.containsIgnoreCase(comment, COPYRIGHT)) {
            return comment;
        }
        // 类名
        String className = psiClass.getName();
        if (StringUtils.isNotBlank(className)) {
            return className;
        }
        // 获取全类名
        return Optional.ofNullable(psiClass.getQualifiedName()).orElse("接口文档");
    }

    /**
     * 从方法上获取方法描述
     *
     * @param psiMethod psiMethod
     * @return 非空，方法描述
     */
    @NotNull
    public static String getMethodDescribe(@NotNull PsiMethod psiMethod) {

        // swagger v3
        PsiAnnotation annotation = psiMethod.getAnnotation(SwaggerConst.V3_OPERATION);
        PsiAnnotationMemberValue v3 = Optional.ofNullable(annotation).map(p -> p.findAttributeValue("name")).orElse(null);
        if (Objects.nonNull(v3)) {
            return v3.getText().replace("\"", "");
        }

        // swagger v2
        annotation = psiMethod.getAnnotation(SwaggerConst.V2_API_OPERATION);
        PsiAnnotationMemberValue v2 = Optional.ofNullable(annotation).map(p -> p.findAttributeValue("value")).orElse(null);
        if (Objects.nonNull(v2)) {
            return v2.getText().replace("\"", "");
        }

        // 从文档注释上提取一行
        String nameComment = PsiKit.getDocComment(psiMethod.getDocComment(), true);
        if (StringUtils.isNotBlank(nameComment)) {
            return nameComment;
        }

        // 从行注释中提取描述
        nameComment = PsiKit.getRowComment(psiMethod);
        if (StringUtils.isNotBlank(nameComment)) {
            return nameComment;
        }

        return psiMethod.getName();
    }

    /**
     * 获取字段上的描述
     *
     * @param psiField 属性
     * @return 描述信息
     */
    @NotNull
    public static String getFiledDescribe(@NotNull PsiField psiField) {
        // swagger v2
        PsiAnnotationMemberValue v2 =
                Optional.ofNullable(psiField.getAnnotation(SwaggerConst.V2_API_MODEL_PROPERTY)).map(p -> p.findAttributeValue("value")).orElse(null);
        if (Objects.nonNull(v2)) {
            return v2.getText().replace("\"", "");
        }

        // 从方法注释上提取一行
        String nameComment = PsiKit.getDocComment(psiField.getDocComment(), true);
        if (StringUtils.isNotBlank(nameComment)) {
            return nameComment;
        }

        // 从行注释中提取描述
        nameComment = PsiKit.getRowComment(psiField);
        if (StringUtils.isNotBlank(nameComment)) {
            return nameComment;
        }

        return psiField.getName();
    }

    @Nullable
    public static String getFieldAnnotationTagValue(@NotNull PsiField psiField, @NotNull String annotation, @NotNull String tag) {
        PsiAnnotation psiAnnotation = psiField.getAnnotation(SwaggerConst.V2_API_MODEL_PROPERTY);
        if (Objects.isNull(psiAnnotation)) {
            return null;
        }
        PsiAnnotationMemberValue attributeValue = psiAnnotation.findAttributeValue(tag);
        if (Objects.isNull(attributeValue)) {
            return null;
        }
        return attributeValue.getText().replaceAll("\"", "");
    }

    @Nullable
    public static String getFieldDocTagValue(@NotNull PsiField psiField, @NotNull String tag) {
        PsiDocComment docComment = psiField.getDocComment();
        if (docComment == null) {
            return null;
        }
        // 获取@param标签
        PsiDocTag psiDocTag = docComment.findTagByName(tag);
        if (psiDocTag == null) {
            return null;
        }

        // 集合可能是空的
        PsiElement[] psiElements = psiDocTag.getDataElements();
        if (psiElements.length == 0) {
            return null;
        }

        return psiElements[0].getText();
    }

    /**
     * 判断参数是否需要排除
     *
     * @param psiParameter 参数
     * @return ture or false
     */
    public static boolean isExcludeParameter(@NotNull PsiParameter psiParameter) {
        PsiType parameterType = psiParameter.getType();
        for (String excludeParameterType : ProjectSetting.excludeParameterTypes) {
            if (InheritanceUtil.isInheritor(parameterType, excludeParameterType)) {
                return true;
            }
        }
        return ProjectSetting.excludeFieldNames.contains(psiParameter.getName());
    }

    /**
     * 是否为常规数据类型
     *
     * @param psiType 类型
     * @return 是：基本数据类型|Date|String
     */
    public static boolean isRegularType(@NotNull PsiType psiType) {
        return psiType instanceof PsiPrimitiveType || DataTypeConst.BASE_WRAPPER_TYPE_INIT_VALUE.containsKey(psiType.getPresentableText());
    }

    /**
     * 是否是需要排除的字段
     *
     * @param psiField psi 字段
     * @return 需要排除字段, 返回 true
     */
    public static boolean isExcludeField(@NotNull PsiField psiField) {

        if (ProjectSetting.excludeFieldNames.contains(psiField.getName())) {
            return true;
        }
        // 排除掉被 static 修饰的字段
        if (PsiKit.hasModifierProperty(psiField, PsiModifier.STATIC)) {
            return true;
        }

        if (PsiKit.hasModifierProperty(psiField, PsiModifier.TRANSIENT)) {
            return true;
        }

        // 排除部分注解的字段
        if (AnnotationUtil.isAnnotated(psiField, ProjectSetting.excludeFieldAnnotation, 0)) {
            return true;
        }

        PsiClass containingClass = psiField.getContainingClass();
        if (containingClass == null) {
            return true;
        }
        return isExcludeClassPackage(containingClass);
    }

    /**
     * 是否在需要排除的包内
     *
     * @param psiClass psiClass
     * @return 需要排除 返回 true
     */
    private static boolean isExcludeClassPackage(@NotNull PsiClass psiClass) {
        String qualifiedName = psiClass.getQualifiedName();
        if (qualifiedName == null) {
            return true;
        }

        for (String packagePrefix : ProjectSetting.excludeClassPackage) {
            if (qualifiedName.startsWith(packagePrefix)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 构建API路径
     *
     * @param classApiPath  类路径
     * @param methodApiPath 方法路径
     * @return API路径
     */
    public static String analyzeApiPath(String classApiPath, String methodApiPath) {
        if (StringUtils.isBlank(classApiPath) && StringUtils.isBlank(methodApiPath)) {
            return null;
        }
        StringJoiner apiPath = new StringJoiner(ProjectConst.API_URL_DELIMITER, ProjectConst.API_URL_DELIMITER, "");
        Optional.ofNullable(classApiPath).map(s -> s.split(ProjectConst.API_URL_DELIMITER)).ifPresent(path -> {
            for (String s : path) {
                if (org.apache.commons.lang3.StringUtils.isNotBlank(s)) {
                    apiPath.add(s);
                }
            }
        });
        Optional.ofNullable(methodApiPath).map(s -> s.split(ProjectConst.API_URL_DELIMITER)).ifPresent(path -> {
            for (String s : path) {
                if (org.apache.commons.lang3.StringUtils.isNotBlank(s)) {
                    apiPath.add(s);
                }
            }
        });
        return apiPath.toString();
    }

    @NotNull
    public static String getParamValue(@NotNull ParamInfo paramInfo) {
        String paramType = paramInfo.getParamType();
        // 非类对象
        if (!paramInfo.isAnClass()) {
            return Optional.ofNullable(DataTypeConst.BASE_WRAPPER_TYPE_INIT_VALUE.get(paramType)).map(String::valueOf).orElse("");
        }
        // 类对象
        PsiClassType psiClassType = (PsiClassType) paramInfo.getPsiType();
        PsiClass psiClass = PsiUtil.resolveClassInType(paramInfo.getPsiType());
        if (psiClass != null) {
            // 返回类型是集合
            if (InheritanceUtil.isInheritor(psiClass, CommonClassNames.JAVA_UTIL_COLLECTION)) {
                // 获取泛型
                PsiType iterableType = PsiUtil.extractIterableTypeParameter(psiClassType, false);
                if (iterableType == null) {
                    return "[]";
                }
                if (iterableType instanceof PsiPrimitiveType) {
                    return "[]";
                }

                if (CommonClassNames.JAVA_LANG_STRING_SHORT.equals(iterableType.getPresentableText())) {
                    return "[\"\"]";
                }

                if (DataTypeConst.BASE_WRAPPER_TYPE_INIT_VALUE.containsKey(iterableType.getPresentableText())) {
                    return "[\"\"]";
                }

                PsiClass iterableClass = PsiUtil.resolveClassInClassTypeOnly(iterableType);
                Map<String, Object> fieldMap = ParamInfoPsiUtil.getFieldsAndDefaultValue(iterableClass, null);
                Object[] objectArr = {fieldMap};
                return JSON.toJSONString(objectArr, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue,
                                         SerializerFeature.WriteDateUseDateFormat);
            } else {
                Map<String, PsiType> genericsMap = ParamInfoPsiUtil.getGenericsMap(psiClassType);
                Map<String, Object> fieldMap = ParamInfoPsiUtil.getFieldsAndDefaultValue(psiClass, genericsMap);
                return JSON.toJSONString(fieldMap, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue,
                                         SerializerFeature.WriteDateUseDateFormat);
            }
        }
        return "";
    }

    /**
     * 生成参数json，只支持基本数据类型和String
     *
     * @param paramInfos 参数信息
     * @return json
     */
    public static String generateBasicTypeJson(List<ParamInfo> paramInfos) {
        if (CollectionUtils.isEmpty(paramInfos)) {
            return "";
        }
        // 参数名和参数值
        Map<String, Object> paramsMap = Maps.newHashMapWithExpectedSize(paramInfos.size());
        for (ParamInfo paramInfo : paramInfos) {
            if (paramInfo.isAnClass()) {
                continue;
            }
            String paramType = paramInfo.getParamType();
            Object paramValue = Optional.ofNullable(DataTypeConst.BASE_WRAPPER_TYPE_INIT_VALUE.get(paramType)).orElse("");
            paramsMap.put(paramInfo.getName(), paramValue);
        }
        return JSON.toJSONString(paramsMap, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue, SerializerFeature.WriteDateUseDateFormat);
    }
}
