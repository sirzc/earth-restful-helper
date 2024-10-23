package com.myth.earth.restful.core.analyze.handlers;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.myth.earth.restful.model.PsiClassInfo;
import org.jetbrains.annotations.NotNull;

/**
 * dubbo 风格接口处理
 * @date 2023-04-25 19:40
 */
public class DubboAnalyzeDataHandler extends AbstractAnalyzeData {

    @NotNull
    @Override
    public PsiClassInfo analyzeByClass(@NotNull PsiClass psiClass, boolean isQuickScan) {
        return new PsiClassInfo();
    }

    @NotNull
    @Override
    public PsiClassInfo analyzeByMethod(@NotNull PsiClass psiClass, @NotNull PsiMethod psiMethod) {
        return new PsiClassInfo();
    }
}
