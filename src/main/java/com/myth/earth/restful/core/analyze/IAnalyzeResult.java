package com.myth.earth.restful.core.analyze;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;

/**
 * 结果数据处理
 */
public interface IAnalyzeResult<R> {

    /**
     * 获取解析结果
     *
     * @param psiClass    类
     * @return 解析结果
     */
    R getAnalyzeResult(PsiClass psiClass);

    /**
     * 获取解析结果
     *
     * @param psiClass  类
     * @param psiMethod 方法
     * @return 解析结果
     */
    R getAnalyzeResult(PsiClass psiClass, PsiMethod psiMethod);
}
