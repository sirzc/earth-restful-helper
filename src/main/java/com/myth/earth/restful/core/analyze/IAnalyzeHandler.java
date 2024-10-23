package com.myth.earth.restful.core.analyze;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.myth.earth.restful.model.PsiClassInfo;
import org.jetbrains.annotations.NotNull;


/**
 * 读取处理程序
 */
public interface IAnalyzeHandler {
    /**
     * 支持类
     *
     * @param psiClass 类
     * @return 是否支持
     */
    boolean supportClass(@NotNull PsiClass psiClass);

    /**
     * 支持方法
     *
     * @param psiMethod 方法
     * @return 是否支持
     */
    boolean supportMethod(@NotNull PsiMethod psiMethod);

    /**
     * 解析类
     *
     * @param psiClass    类
     * @param isQuickScan 是否快速扫描
     * @return 解析结果
     */
    @NotNull
    PsiClassInfo analyzeByClass(@NotNull PsiClass psiClass, boolean isQuickScan);

    /**
     * 解析方法
     *
     * @param psiClass  类
     * @param psiMethod 方法
     * @return 解析结果
     */
    @NotNull
    PsiClassInfo analyzeByMethod(@NotNull PsiClass psiClass, @NotNull PsiMethod psiMethod);
}
