package com.myth.earth.restful.core.analyze.processor;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.myth.earth.restful.core.analyze.IAnalyzeHandler;
import com.myth.earth.restful.core.analyze.IAnalyzeResult;
import com.myth.earth.restful.core.analyze.handlers.JaxRsAnalyzeDataHandler;
import com.myth.earth.restful.core.analyze.handlers.SpringAnalyzeDataHandler;
import com.myth.earth.restful.model.PsiClassInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 抽象转换处理器 
 * @date 2023-06-06 19:19
 */
public abstract class AbstractConvertProcessor<R> implements IAnalyzeResult<R> {

    private final Project               project;
    /**
     * 分析处理器
     */
    private final List<IAnalyzeHandler> analyzeHandlers;

    public AbstractConvertProcessor(Project project) {
        this.project = project;
        this.analyzeHandlers = new ArrayList<>();
        this.analyzeHandlers.add(new JaxRsAnalyzeDataHandler());
        this.analyzeHandlers.add(new SpringAnalyzeDataHandler());
    }

    /**
     * 获取当前项目实例
     *
     * @return project
     */
    @NotNull
    public Project getProject() {
        return project;
    }

    /**
     * 获取解析处理器
     *
     * @param psiClass 类
     * @return 解析结果
     */
    @Nullable
    private IAnalyzeHandler getAnalyzeHandler(@Nullable PsiClass psiClass) {
        if (Objects.isNull(psiClass)) {
            return null;
        }
        for (IAnalyzeHandler analyzeHandler : analyzeHandlers) {
            if (analyzeHandler.supportClass(psiClass)) {
                return analyzeHandler;
            }
        }
        return null;
    }

    /**
     * 获取目标信息
     *
     * @param psiClassInfo psi class info
     * @return 目标结果
     */
    @Nullable
    protected abstract R getTargets(@NotNull PsiClassInfo psiClassInfo);

    /**
     * 是否快速扫描（只解析基本信息，不管入参出参）
     *
     * @return 默认：false
     */
    protected boolean isQuickScan() {
        return false;
    }

    @Override
    @Nullable
    public R getAnalyzeResult(PsiClass psiClass) {
        IAnalyzeHandler handler = getAnalyzeHandler(psiClass);
        if (Objects.isNull(handler)) {
            return null;
        }
        return getTargets(handler.analyzeByClass(psiClass, isQuickScan()));
    }

    @Override
    @Nullable
    public R getAnalyzeResult(PsiClass psiClass, PsiMethod psiMethod) {
        if (psiMethod == null) {
            return null;
        }

        IAnalyzeHandler handler = getAnalyzeHandler(psiClass);
        if (Objects.isNull(handler)) {
            return null;
        }
        return getTargets(handler.analyzeByMethod(psiClass, psiMethod));
    }

    /**
     * 是否支持类
     *
     * @param psiClass 类
     * @return 是否支持
     */
    public boolean supportClass(@Nullable PsiClass psiClass) {
        if (Objects.isNull(psiClass)) {
            return false;
        }

        for (IAnalyzeHandler analyzeHandler : analyzeHandlers) {
            if (analyzeHandler.supportClass(psiClass)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 是否支持方法
     *
     * @param psiMethod 方法
     * @return 是否支持
     */
    public boolean supportMethod(@Nullable PsiMethod psiMethod) {
        if (Objects.isNull(psiMethod)) {
            return false;
        }

        for (IAnalyzeHandler analyzeHandler : analyzeHandlers) {
            if (analyzeHandler.supportMethod(psiMethod)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 是否支持方法
     *
     * @param psiMethod 方法
     * @return 是否支持
     */
    public boolean notSupportMethod(@Nullable PsiMethod psiMethod) {
        return !supportMethod(psiMethod);
    }
}
