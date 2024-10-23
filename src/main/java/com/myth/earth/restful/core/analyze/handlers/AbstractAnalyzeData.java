package com.myth.earth.restful.core.analyze.handlers;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.myth.earth.restful.core.analyze.IAnalyzeHandler;
import org.jetbrains.annotations.NotNull;

/**
 * 抽象分析数据
 */
public abstract class AbstractAnalyzeData implements IAnalyzeHandler {

    @Override
    public boolean supportClass(@NotNull PsiClass psiClass) {
        // 排除枚举、注解
        return !psiClass.isEnum() && !psiClass.isAnnotationType();
    }

    @Override
    public boolean supportMethod(@NotNull PsiMethod psiMethod) {
        return false;
    }
}

