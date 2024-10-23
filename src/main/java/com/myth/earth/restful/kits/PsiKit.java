package com.myth.earth.restful.kits;

import com.intellij.codeInsight.AnnotationUtil;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.javadoc.PsiDocTag;
import com.intellij.psi.javadoc.PsiDocTagValue;
import com.intellij.psi.util.PsiTreeUtil;
import com.myth.earth.restful.consts.ProjectConst;
import com.myth.earth.restful.plugin.ServiceManager;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * psi 操作组件类
 *
 * @author changan
 * @date 2023-04-25 21:10
 */
public final class PsiKit {

    /**
     * 读取文档注释
     *
     * @param psiDocComment 文档注释内容
     * @return 所有注释内容
     */
    @NotNull
    public static String getDocComment(PsiDocComment psiDocComment) {
        if (Objects.isNull(psiDocComment)) {
            return "";
        }
        return ServiceManager.runReadAction(() -> {
            StringJoiner joiner = new StringJoiner("");
            PsiElement[] children = psiDocComment.getChildren();
            for (PsiElement psiElement : children) {
                if (!"PsiDocToken:DOC_COMMENT_DATA".equalsIgnoreCase(psiElement.toString())) {
                    continue;
                }
                String text = psiElement.getText().replaceAll("[* \n]+", StringUtils.EMPTY);
                joiner.add(text);
            }
            return joiner.toString();
        });
    }

    /**
     * 获取文档注释
     *
     * @param psiDocComment 注释内容
     * @param readOnlyOne   只读一行
     * @return 文档注释
     */
    @NotNull
    public static String getDocComment(PsiDocComment psiDocComment, boolean readOnlyOne) {
        if (Objects.isNull(psiDocComment)) {
            return "";
        }
        // 只读一行
        if (!readOnlyOne) {
            return getDocComment(psiDocComment);
        }
        return ServiceManager.runReadAction(() -> {
            for (PsiElement psiElement : psiDocComment.getChildren()) {
                if (!"PsiDocToken:DOC_COMMENT_DATA".equalsIgnoreCase(psiElement.toString())) {
                    continue;
                }
                // 只获取第一行注释
                String target = psiElement.getText().replaceAll("[* \n]+", StringUtils.EMPTY);
                if (StringUtils.isNotBlank(target)) {
                    return target;
                }
            }
            return getPsiDocTagComment(psiDocComment);
        });
    }

    /**
     * 获取PSI中的标签注释
     *
     * @param psiDocComment 注释内容
     * @return 符合条件的标签注释内容
     */
    @NotNull
    private static String getPsiDocTagComment(PsiDocComment psiDocComment) {
        PsiDocTag[] tags = psiDocComment.getTags();
        for (PsiDocTag tag : tags) {
            if (!ProjectConst.METHOD_API_NAME.contains(StringUtils.toRootLowerCase(tag.getName()))) {
                continue;
            }
            PsiDocTagValue valueElement = tag.getValueElement();
            return Optional.ofNullable(valueElement).map(PsiDocTagValue::getText).orElse("");
        }
        return "";
    }

    /**
     * 获取行注释
     *
     * @param psiField 字段
     * @return 注释
     */
    @NotNull
    public static String getRowComment(PsiField psiField) {
        if (Objects.isNull(psiField)) {
            return "";
        }
        return getRowComment(psiField.getChildren());
    }


    /**
     * 获取行注释
     *
     * @param psiMethod 注释内容
     * @return 注释
     */
    @NotNull
    public static String getRowComment(PsiMethod psiMethod) {
        if (Objects.isNull(psiMethod)) {
            return "";
        }
        return getRowComment(psiMethod.getChildren());
    }

    /**
     * 获取方法的所有注解（包括父类）
     *
     * @param psiMethod psiMethod
     * @return annotations
     */
    @NotNull
    public static List<PsiAnnotation> getMethodAnnotations(@NotNull PsiMethod psiMethod) {
        List<PsiAnnotation> annotations = new ArrayList<>(Arrays.asList(psiMethod.getModifierList().getAnnotations()));
        for (PsiMethod superMethod : psiMethod.findSuperMethods()) {
            getMethodAnnotations(superMethod)
                    .stream()
                    // 筛选：子类中方法定义了父类中方法存在的注解时只保留最上层的注解（即实现类的方法注解
                    .filter(annotation -> !annotations.contains(annotation))
                    .forEach(annotations::add);
        }
        return annotations;
    }


    /**
     * 校验字段是否有某个修饰符
     *
     * @param psiField - 字段
     * @param psiModifier - 属性
     * @return ture or false
     */
    public static boolean hasModifierProperty(@NotNull PsiField psiField, String psiModifier) {
        PsiModifierList modifierList = psiField.getModifierList();
        if (modifierList == null) {
            return false;
        }
        return modifierList.hasModifierProperty(psiModifier);
    }


    /**
     * 判断方法是否是static、public等修饰符
     * @param psiMethod 方法
     * @param psiModifier 属性
     * @return ture or false
     */
    public static boolean hasModifierProperty(@NotNull PsiMethod psiMethod, @NotNull String psiModifier) {
        PsiModifierList modifierList = psiMethod.getModifierList();
        return modifierList.hasModifierProperty(psiModifier);
    }

    /**
     * 获取行注释
     *
     * @param children 所有节点
     * @return 返回首行注释
     */
    private static String getRowComment(@NotNull PsiElement[] children) {
        for (PsiElement child : children) {
            if (child instanceof PsiComment) {
                return child.getText().replace("//", "");
            }
        }
        return "";
    }


    /**
     * 获取目标类
     *
     * @param editor 编辑器
     * @param psiFile 文件信息
     * @return 目标类
     */
    @Nullable
    public static PsiClass getTargetClass(@NotNull Editor editor, @NotNull PsiFile psiFile) {
        int offset = editor.getCaretModel().getOffset();
        PsiElement element = psiFile.findElementAt(offset);
        if (Objects.isNull(element)) {
            return null;
        }
        // 当前类
        PsiClass target = PsiTreeUtil.getParentOfType(element, PsiClass.class);
        return target instanceof SyntheticElement ? null : target;
    }

    /**
     * 从虚拟文件中获取类信息
     *
     * @param project 项目
     * @param virtualFile 虚拟文件
     * @return 虚拟文件对应的类
     */
    @Nullable
    public static PsiClass getPsiClassFromVirtualFile(@NotNull Project project,@NotNull VirtualFile virtualFile) {
        if (!virtualFile.getFileType().isBinary() && Objects.equals(virtualFile.getExtension(), "java")) {
            PsiFile psiFile = PsiManager.getInstance(project).findFile(virtualFile);
            if (psiFile instanceof PsiJavaFile) {
                PsiClass targetClass = PsiTreeUtil.findChildOfType(psiFile, PsiClass.class);
                // 获取Java类或者接口
                if (targetClass == null || targetClass.isAnnotationType() || targetClass.isEnum()) {
                    return null;
                }
                return targetClass;
            }
        }
        return null;
    }

    /**
     * 获取目标方法，当前游标所在行的方法
     *
     * @param editor 编辑器
     * @param psiFile 文件信息
     * @return 目标方法
     */
    @Nullable
    public static PsiMethod getTargetMethod(@NotNull Editor editor, @NotNull PsiFile psiFile) {
        int offset = editor.getCaretModel().getOffset();
        PsiElement element = psiFile.findElementAt(offset);
        if (Objects.isNull(element)) {
            return null;
        }
        // 当前方法
        PsiMethod target = PsiTreeUtil.getParentOfType(element, PsiMethod.class);
        return target instanceof SyntheticElement ? null : target;
    }

    /**
     * 判断一个方法是否为api方法，非静态、公开，且包含指定注解
     *
     * @param psiMethod   方法
     * @param annotations 方法注解包路径
     * @return 是否支持
     */
    public static boolean supportApiMethod(@NotNull PsiMethod psiMethod, @NotNull Collection<String> annotations) {
        if (psiMethod.isConstructor()) {
            return false;
        }
        return ServiceManager.runReadAction(() -> {
            // 非公开 或 静态方法
            if (!hasModifierProperty(psiMethod, PsiModifier.PUBLIC) || hasModifierProperty(psiMethod, PsiModifier.STATIC)) {
                return false;
            }
            // 是否包含注解
            return AnnotationUtil.isAnnotated(psiMethod, annotations, 0);
        });
    }

    /**
     * 获取指定注解的值
     *
     * @param psiParameter   参数信息
     * @param annotationPath 注解地址
     * @param property       属性
     * @return 值
     */
    public static String getPropertyFromAnnotation(@NotNull PsiParameter psiParameter, @NotNull String annotationPath, @NotNull String property) {
        Optional<PsiAnnotation> optional = Optional.ofNullable(psiParameter.getAnnotation(annotationPath));
        return optional.map(p -> p.findAttributeValue(property))
                       .map(PsiAnnotationMemberValue::getText)
                       .map(s -> s.replace("\"", ""))
                       .filter(StringUtils::isNotBlank)
                       .orElse(null);
    }

    /**
     * 获取指定目录下的所有class类
     *
     * @param directory psi目录
     * @return 所有class类
     */
    @NotNull
    public static List<PsiClass> getPsiClassList(@NotNull PsiDirectory directory) {
        PsiElement[] children = directory.getChildren();
        if (children.length == 0) {
            return Collections.emptyList();
        }

        List<PsiClass> psiClassList = new ArrayList<>(children.length);
        for (PsiElement child : children) {
            if (child instanceof PsiDirectory) {
                psiClassList.addAll(getPsiClassList((PsiDirectory) child));
            }

            if ((child instanceof PsiJavaFile) && ((PsiJavaFile) child).getClasses().length > 0) {
                PsiClass psiClass = ((PsiJavaFile) child).getClasses()[0];
                psiClassList.add(psiClass);
            }
        }
        return psiClassList;
    }
}
