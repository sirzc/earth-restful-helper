package com.myth.earth.restful.utils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.PsiClassReferenceType;
import com.intellij.psi.util.InheritanceUtil;
import com.intellij.psi.util.PsiTypesUtil;
import com.intellij.psi.util.PsiUtil;
import com.myth.earth.restful.consts.DataTypeConst;
import com.myth.earth.restful.consts.SpringPackageConst;
import com.myth.earth.restful.consts.SwaggerConst;
import com.myth.earth.restful.kits.PsiKit;
import com.myth.earth.restful.model.ParamInfo;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 参数信息提取工具类
 *
 * @author changan
 * @date 2023/5/3 15:26
 **/
public class ParamInfoPsiUtil {

    private static final Pattern MAP_LIST_PATTERN = Pattern.compile("<([^<>]*)>");

    /**
     * 获取泛型Map, 将泛型的 参数和实际指定的泛型进行对应
     *
     * @param psiClassType 实际返回的填写泛型的代码 Result<User>
     * @return key 是 泛型参数 T K , value 是实际参数中写的类型
     */
    @Nullable
    public static Map<String, PsiType> getGenericsMap(@NotNull PsiClassType psiClassType) {
        // 转换为原始类 Result<T>
        PsiClass psiClass = PsiUtil.resolveClassInClassTypeOnly(psiClassType);
        if (psiClass == null || !psiClass.hasTypeParameters()) {
            // 无泛型
            return null;
        }

        PsiTypeParameter[] typeParameters = psiClass.getTypeParameters();
        PsiType[] typeArr = psiClassType.getParameters();
        if (typeParameters.length != typeArr.length) {
            return null;
        }
        // 构建泛型类型对应的psiType
        Map<String, PsiType> hashMap = Maps.newHashMapWithExpectedSize(typeParameters.length);
        for (int i = 0; i < typeParameters.length; i++) {
            hashMap.put(typeParameters[i].getName(), typeArr[i]);
        }
        return hashMap;

    }

    public static List<ParamInfo> childClassPsiType(@NotNull PsiType psiType, Map<String, PsiType> genericsMap) {
        // 考虑到可能是泛型，提前替换字段比如 T -> UserDTO   List<T> -> List<UserDTO>
        psiType = replaceFieldType(psiType, genericsMap);
        // List Set
        if (InheritanceUtil.isInheritor(psiType, CommonClassNames.JAVA_UTIL_COLLECTION)) {
            return listPsiType(psiType);
        }
        // Map
        if (InheritanceUtil.isInheritor(psiType, CommonClassNames.JAVA_UTIL_MAP)) {
            return mapPsiType(psiType);
        }
        // 是一个类
        if (psiType instanceof PsiClassType) {
            return beanPsiType(psiType, genericsMap);
        }
        return new ArrayList<>();
    }

    @NotNull
    private static List<ParamInfo> mapPsiType(PsiType psiType) {
        // key
        PsiType matKeyType = PsiUtil.substituteTypeParameter(psiType, CommonClassNames.JAVA_UTIL_MAP, 0, false);
        if (Objects.isNull(matKeyType) || !DataExtractUtil.isRegularType(matKeyType)) {
            return new ArrayList<>();
        }
        // value
        PsiType matValueType = PsiUtil.substituteTypeParameter(psiType, CommonClassNames.JAVA_UTIL_MAP, 1, false);
        if (Objects.isNull(matValueType) || DataExtractUtil.isRegularType(matValueType)) {
            return new ArrayList<>();
        }

        // 获取Map中VALUE对应的类
        PsiClass genericsPsiClass = PsiUtil.resolveClassInClassTypeOnly(matValueType);
        if (Objects.isNull(genericsPsiClass)) {
            return new ArrayList<>();
        }
        // 获取VALUE类中泛型的映射关系
        Map<String, PsiType> fieldGenericsMap = getGenericsMap((PsiClassType) matValueType);
        List<ParamInfo> paramInfos = new ArrayList<>();
        // 获取bean中的属性
        for (PsiField childPsiFiled : genericsPsiClass.getAllFields()) {
            if (DataExtractUtil.isExcludeField(childPsiFiled)) {
                continue;
            }
            paramInfos.add(buildParamFromField(psiType, childPsiFiled, fieldGenericsMap));
        }
        return paramInfos;
    }

    @NotNull
    private static List<ParamInfo> listPsiType(PsiType psiType) {
        PsiType iterableType = PsiUtil.extractIterableTypeParameter(psiType, false);
        // List中的泛型为基本数据类型
        if (Objects.isNull(iterableType) || DataExtractUtil.isRegularType(iterableType)) {
            return new ArrayList<>();
        }

        // 获取泛型对应的T类
        PsiClass genericsPsiClass = PsiUtil.resolveClassInClassTypeOnly(iterableType);
        if (Objects.isNull(genericsPsiClass)) {
            return new ArrayList<>();
        }

        // 获取泛型对应的映射关系
        Map<String, PsiType> fieldGenericsMap = getGenericsMap((PsiClassType) iterableType);
        List<ParamInfo> paramInfos = new ArrayList<>();
        // 获取bean中的属性
        for (PsiField childPsiFiled : genericsPsiClass.getAllFields()) {
            if (DataExtractUtil.isExcludeField(childPsiFiled)) {
                continue;
            }
            paramInfos.add(buildParamFromField(psiType, childPsiFiled, fieldGenericsMap));
        }
        return paramInfos;
    }

    @NotNull
    private static List<ParamInfo> beanPsiType(@NotNull PsiType psiType, Map<String, PsiType> genericsMap) {
        // 前置过滤，基本数据类、枚举、注解、接口
        PsiClass fieldClass = PsiUtil.resolveClassInClassTypeOnly(psiType);
        if (Objects.isNull(fieldClass) || fieldClass.isEnum() || fieldClass.isInterface() || fieldClass.isAnnotationType()) {
            return new ArrayList<>();
        }
        List<ParamInfo> paramInfos = new ArrayList<>();
        // 获取bean中的属性
        for (PsiField childPsiFiled : fieldClass.getAllFields()) {
            if (DataExtractUtil.isExcludeField(childPsiFiled)) {
                continue;
            }
            paramInfos.add(buildParamFromField(psiType, childPsiFiled, genericsMap));
        }
        return paramInfos;
    }

    /**
     * 获取类中字段的属性
     *
     * @param parentPsiType 父节点类型
     * @param psiField      psiField
     * @param genericsMap   genericsMap泛型映射对象
     * @return 该属性的内容
     */
    private static ParamInfo buildParamFromField(@NotNull PsiType parentPsiType, @NotNull PsiField psiField, Map<String, PsiType> genericsMap) {
        ParamInfo paramInfo = new ParamInfo();
        paramInfo.setName(psiField.getName());
        paramInfo.setPsiType(psiField.getType());
        // 获取类上注解
        PsiAnnotation[] annotations = Optional.ofNullable(psiField.getModifierList()).map(PsiModifierList::getAnnotations).orElse(null);
        if (Objects.nonNull(annotations)) {
            List<String> annotationNames = Arrays.stream(annotations).map(PsiAnnotation::getQualifiedName).collect(Collectors.toList());
            paramInfo.setAnnotationNames(annotationNames);
            paramInfo.setDescription(DataExtractUtil.getFiledDescribe(psiField));
        }

        String required = DataExtractUtil.getFieldAnnotationTagValue(psiField, SwaggerConst.V2_API_MODEL_PROPERTY, "required");
        if (StringUtils.isBlank(required)) {
            required = DataExtractUtil.getFieldDocTagValue(psiField, "required");
        }
        Optional.ofNullable(required).map(StringUtils::trim).map(BooleanUtils::toBoolean).ifPresent(paramInfo::setRequired);


        String example = DataExtractUtil.getFieldAnnotationTagValue(psiField, SwaggerConst.V2_API_MODEL_PROPERTY, "example");
        if (StringUtils.isBlank(example)) {
            example = DataExtractUtil.getFieldDocTagValue(psiField, "example");
        }
        Optional.ofNullable(example).map(StringUtils::trim).filter(StringUtils::isNotBlank).ifPresent(paramInfo::setExample);

        // 引用参数类型，需要转换如：List<User>、User、Map<String,User>
        PsiType psiFieldType = replaceFieldType(psiField.getType(), genericsMap);
        paramInfo.setParamType(psiFieldType.getPresentableText());
        // 是基本数据类型，无需进行后续操作
        if (DataExtractUtil.isRegularType(psiFieldType)) {
            return paramInfo;
        }

        // 非基本数据类型
        Optional.ofNullable(PsiUtil.resolveClassInClassTypeOnly(psiFieldType)).map(PsiClass::getQualifiedName).ifPresent(paramInfo::setQualifiedName);
        paramInfo.setPsiType(psiFieldType);
        paramInfo.setAnClass(true);

        // 嵌套类型，直接返回
        if (Objects.equals(psiField.getType(), psiFieldType) && isNestedClass(parentPsiType, psiFieldType)) {
            paramInfo.setAnNestedClass(true);
            return paramInfo;
        }

        // 继续遍历属性
        paramInfo.setChildList(childClassPsiType(psiFieldType, genericsMap));
        return paramInfo;
    }

    /**
     * 判断一个class中的内容是否是它自己
     *
     * @param parentPsiType 父类 psiType
     * @param childPsiType  子类 psiType
     * @return 是否是它自己，如：List<User> - User - Map<String,User> 为true
     */
    private static boolean isNestedClass(@NotNull PsiType parentPsiType, @NotNull PsiType childPsiType) {
        List<String> parentList = extractContent(parentPsiType.getPresentableText());
        List<String> childList = extractContent(childPsiType.getPresentableText());
        if (parentList.size() == 0 || childList.size() == 0) {
            return false;
        }
        for (String p : parentList) {
            for (String c : childList) {
                if (p.equals(c)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 如：Map<String, List<User>>，获取到 User
     *
     * @param input 输入内容
     * @return 返回结果
     */
    @NotNull
    public static List<String> extractContent(@Nullable String input) {
        List<String> result = new ArrayList<>();
        if (StringUtils.isBlank(input)) {
            return result;
        }

        Matcher matcher = MAP_LIST_PATTERN.matcher(input);
        while (matcher.find()) {
            String content = matcher.group(1);
            result.add(content);
        }

        if (result.size() == 0){
            result.add(input);
        }

        return result;
    }

    /**
     * 判断当前字段是否含有泛型, 从泛型映射表中替换字段类型
     * <p>
     * 替换字段比如 T -> UserDTO   List<T> -> List<UserDTO>
     *
     * @param psiType     当前字段的类型
     * @param genericsMap 含有泛型的 Map
     * @return 泛型的实际类型
     */
    @NotNull
    private static PsiType replaceFieldType(PsiType psiType, Map<String, PsiType> genericsMap) {
        if (genericsMap == null || genericsMap.isEmpty()) {
            return psiType;
        }

        if (!(psiType instanceof PsiClassType)) {
            return psiType;
        }

        if (!(psiType instanceof PsiClassReferenceType)) {
            return psiType;
        }

        // 当前字段的类型就是泛型 private T data
        if (genericsMap.containsKey(psiType.getPresentableText())) {
            return genericsMap.get(psiType.getPresentableText());
        }

        // 当前字段含有泛型 List<T> -> List<UserDTO>
        PsiClass fieldClass = PsiUtil.resolveClassInClassTypeOnly(psiType);
        if (fieldClass == null || !fieldClass.hasTypeParameters()) {
            return psiType;
        }

        PsiType[] parameters = ((PsiClassType) psiType).getParameters();
        List<PsiType> psiTypeList = new ArrayList<>();
        // 替换泛型为实际类型
        for (PsiType param : parameters) {
            if (genericsMap.get(param.getPresentableText()) == null) {
                psiTypeList.add(param);
            } else {
                psiTypeList.add(genericsMap.get(param.getPresentableText()));
            }
        }
        if (!psiTypeList.isEmpty()) {
            PsiType[] psiTypes = new PsiType[psiTypeList.size()];
            for (int i = 0; i < psiTypeList.size(); i++) {
                psiTypes[i] = psiTypeList.get(i);
            }
            return PsiElementFactory.getInstance(fieldClass.getProject()).createType(fieldClass, psiTypes);
        }
        return psiType;
    }

    @NotNull
    public static Map<String, Object> getFieldsAndDefaultValue(PsiClass psiClass, Map<String, PsiType> genericMap) {
        return getFieldsAndDefaultValue(psiClass, genericMap, new LinkedList<>());
    }

    public static Map<String, Object> getFieldsAndDefaultValue(PsiClass psiClass, Map<String, PsiType> genericMap, LinkedList<String> qualifiedNameList) {
        Map<String, Object> fieldMap = new LinkedHashMap<>();
        if (psiClass == null || psiClass.isEnum() || psiClass.isInterface() || psiClass.isAnnotationType()) {
            return fieldMap;
        }
        // 设置当前类的类型
        qualifiedNameList.add(psiClass.getQualifiedName());
        for (PsiField field : psiClass.getAllFields()) {
            if (DataExtractUtil.isExcludeField(field)) {
                continue;
            }
            PsiType type = field.getType();
            String name = field.getName();
            if (type instanceof PsiPrimitiveType) {
                // 基本类型
                fieldMap.put(name, PsiTypesUtil.getDefaultValue(type));
                continue;
            }
            // 如果是泛型, 且泛型字段是当前字段, 将当前字段类型替换为泛型类型
            type = replaceFieldType(type, genericMap);
            // 引用类型
            String fieldTypeName = type.getPresentableText();
            // 指定的类型
            if (DataTypeConst.BASE_WRAPPER_TYPE_INIT_VALUE.containsKey(fieldTypeName)) {
                fieldMap.put(name, DataTypeConst.BASE_WRAPPER_TYPE_INIT_VALUE.get(fieldTypeName));
            } else if (type instanceof PsiArrayType) {
                // 数组类型
                List<Object> list = new ArrayList<>();
                PsiType deepType = type.getDeepComponentType();
                if (deepType instanceof PsiPrimitiveType) {
                    list.add(PsiTypesUtil.getDefaultValue(deepType));
                } else if (DataTypeConst.BASE_WRAPPER_TYPE_INIT_VALUE.containsKey(deepType.getPresentableText())) {
                    list.add(DataTypeConst.BASE_WRAPPER_TYPE_INIT_VALUE.get(deepType.getPresentableText()));
                } else {
                    // 参数类型为对象 校验是否递归
                    PsiClass classInType = PsiUtil.resolveClassInType(deepType);

                    LinkedList<String> temp = new LinkedList<>(qualifiedNameList);
                    if (classInType != null && hasContainQualifiedName(temp, classInType.getQualifiedName())) {
                        list.add("Object for " + classInType.getName());
                    } else {
                        list.add(getFieldsAndDefaultValue(classInType, null, temp));
                    }
                }
                fieldMap.put(name, list);
            } else if (InheritanceUtil.isInheritor(type, CommonClassNames.JAVA_UTIL_COLLECTION)) {
                // List Set or HashSet
                List<Object> list = new ArrayList<>();
                PsiType iterableType = PsiUtil.extractIterableTypeParameter(type, false);
                PsiClass iterableClass = PsiUtil.resolveClassInClassTypeOnly(iterableType);
                if (iterableClass != null) {
                    String classTypeName = iterableClass.getName();
                    if (DataTypeConst.BASE_WRAPPER_TYPE_INIT_VALUE.containsKey(classTypeName)) {
                        list.add(DataTypeConst.BASE_WRAPPER_TYPE_INIT_VALUE.get(classTypeName));
                    } else {

                        // 参数类型为对象 校验是否递归
                        LinkedList<String> temp = new LinkedList<>(qualifiedNameList);
                        if (hasContainQualifiedName(temp, iterableClass.getQualifiedName())) {
                            list.add("Object for " + iterableClass.getName());
                        } else {
                            list.add(getFieldsAndDefaultValue(iterableClass, null, temp));
                        }

                    }
                }
                fieldMap.put(name, list);
            } else if (InheritanceUtil.isInheritor(type, CommonClassNames.JAVA_UTIL_MAP)) {
                // HashMap or Map
                HashMap<String, Object> hashMap = new HashMap<>(4);
                PsiType matKeyType = PsiUtil.substituteTypeParameter(type, CommonClassNames.JAVA_UTIL_MAP, 0, false);
                // key 只能是 包装类型或者 String
                if (matKeyType != null && DataTypeConst.WRAPPER_DATA_TYPE.contains(matKeyType.getPresentableText())) {
                    // Value
                    PsiType matValueType = PsiUtil.substituteTypeParameter(type, CommonClassNames.JAVA_UTIL_MAP, 1, false);
                    if (Objects.isNull(matValueType) || !DataExtractUtil.isRegularType(matValueType)) {
                        PsiClass valueClass = PsiUtil.resolveClassInClassTypeOnly(matValueType);
                        if (valueClass != null) {
                            LinkedList<String> temp = new LinkedList<>(qualifiedNameList);
                            hashMap.put(matKeyType.getPresentableText(), getFieldsAndDefaultValue(valueClass, getGenericsMap((PsiClassType) matValueType), temp));
                        }
                    }
                }
                fieldMap.put(name, hashMap);
            } else if (psiClass.isEnum() || psiClass.isInterface() || psiClass.isAnnotationType()) {
                // enum or interface
                fieldMap.put(name, "");
            } else {
                // 参数类型为对象 校验是否递归
                PsiClass classInType = PsiUtil.resolveClassInType(type);
                LinkedList<String> temp = new LinkedList<>(qualifiedNameList);
                if (classInType != null && hasContainQualifiedName(temp, classInType.getQualifiedName())) {
                    fieldMap.put(name, "Object for " + classInType.getName());
                } else {
                    fieldMap.put(name, getFieldsAndDefaultValue(PsiUtil.resolveClassInType(type), getGenericsMap((PsiClassType) type), temp));
                }
            }
        }
        return fieldMap;
    }

    private static boolean hasContainQualifiedName(LinkedList<String> qualifiedNameList, String qualifiedName) {
        if (qualifiedNameList.isEmpty()) {
            return false;
        }
        for (String s : qualifiedNameList) {
            if (s.equals(qualifiedName)) {
                return true;
            }
        }
        return false;
    }


    /**
     * 获取方法出参数据
     *
     * @param psiType 方法返回类型
     * @return 出参数据
     */
    public static ParamInfo analyzeOutputParam(@NotNull PsiType psiType) {
        ParamInfo paramInfo = new ParamInfo();
        paramInfo.setName(null);
        paramInfo.setDescription("");
        paramInfo.setPsiType(psiType);
        paramInfo.setParamType(psiType.getPresentableText());
        // 基本数据类型，直接返回
        if (DataExtractUtil.isRegularType(psiType)) {
            return paramInfo;
        }

        // 非基本数据类型，获取类路径
        paramInfo.setAnClass(true);
        Optional.ofNullable(PsiUtil.resolveClassInClassTypeOnly(psiType)).map(PsiClass::getQualifiedName).ifPresent(paramInfo::setQualifiedName);
        // 处理泛型
        PsiClassType psiClassType = (PsiClassType) psiType;
        Map<String, PsiType> genericMap = ParamInfoPsiUtil.getGenericsMap(psiClassType);
        paramInfo.setChildList(ParamInfoPsiUtil.childClassPsiType(psiType, genericMap));
        return paramInfo;
    }

    /**
     * 获取参数信息
     *
     * @param psiParameter 方法参数
     * @return 参数信息
     */
    @NotNull
    public static ParamInfo buildInputParam(@NotNull PsiParameter psiParameter) {
        ParamInfo paramInfo = new ParamInfo();
        paramInfo.setName(psiParameter.getName());
        // 参数信息
        PsiType psiType = psiParameter.getType();
        paramInfo.setPsiType(psiType);
        paramInfo.setParamType(psiType.getPresentableText());
        paramInfo.setAnnotationNames(Lists.newArrayList());
        // 获取方法参数上注解
        PsiAnnotation[] annotations = Optional.ofNullable(psiParameter.getModifierList()).map(PsiModifierList::getAnnotations).orElse(null);
        if (Objects.nonNull(annotations)) {
            List<String> annotationNames = Arrays.stream(annotations).map(PsiAnnotation::getQualifiedName).collect(Collectors.toList());
            paramInfo.setAnnotationNames(annotationNames);
        }
        String required = PsiKit.getPropertyFromAnnotation(psiParameter, SpringPackageConst.REQUEST_PARAM_NAME, "required");
        Optional.ofNullable(required).map(BooleanUtils::toBoolean).ifPresent(paramInfo::setRequired);
        if (!DataExtractUtil.isRegularType(psiType)) {
            paramInfo.setAnClass(true);
            // 非基本数据类型，获取类路径
            PsiClass fieldClass = PsiUtil.resolveClassInClassTypeOnly(psiType);
            paramInfo.setQualifiedName(Optional.ofNullable(fieldClass).map(PsiClass::getQualifiedName).orElse(null));
            paramInfo.setChildList(ParamInfoPsiUtil.childClassPsiType(psiType, null));
        }
        return paramInfo;
    }
}
