package com.myth.earth.restful.plugin.insight;

import com.intellij.codeInsight.daemon.GutterName;
import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.codeInsight.daemon.LineMarkerProviderDescriptor;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.IconLoader;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.util.PsiTreeUtil;
import com.myth.earth.restful.core.analyze.processor.ApiDetailConvertProcessor;
import com.myth.earth.restful.plugin.ServiceManager;
import com.myth.earth.restful.plugin.service.ApiRestfulProjectService;
import com.myth.earth.restful.plugin.toolwindow.RootToolWindowFactory;
import com.myth.earth.restful.utils.DataExtractUtil;
import io.swagger.v3.oas.models.OpenAPI;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.List;

/**
 * 添加文档标记信息
 *
 * @author zhouchao
 * @date 2024-03-21 11:32
 */
public class ApiLineMarkerProvider extends LineMarkerProviderDescriptor {

    @NotNull
    private static final Icon HTTP_ICON = IconLoader.getIcon("/icons/c-http.svg", ApiLineMarkerProvider.class);

    /**
     * api 快速导航
     * @return 装订区域图标描述名称
     */
    @Override
    @Nullable("null means disabled")
    public @GutterName String getName() {
        return "Api quick navigation";
    }

    @Override
    @Nullable
    public Icon getIcon() {
        return HTTP_ICON;
    }

    @Override
    public LineMarkerInfo<?> getLineMarkerInfo(@NotNull PsiElement psiElement) {
        return null;
    }

    /**
     * 收集慢线标记
     *
     * @param elements 节点列表
     * @param result 标记结果
     */
    @Override
    public void collectSlowLineMarkers(@NotNull List<? extends PsiElement> elements, @NotNull Collection<? super LineMarkerInfo<?>> result) {
        for (PsiElement psiElement : elements) {
            LineMarkerInfo<?> lineMarkerInfo = null;
            PsiElement parent = psiElement.getParent();
            if (psiElement instanceof PsiIdentifier) {
                if (parent instanceof PsiClass) {
                    lineMarkerInfo = createClassLineMarker(psiElement);
                }

                if (parent instanceof PsiMethod) {
                    lineMarkerInfo = createMethodLineMarker(psiElement);
                }
            }

            if (lineMarkerInfo != null) {
                result.add(lineMarkerInfo);
            }
        }
    }

    private LineMarkerInfo<?> createClassLineMarker(PsiElement psiElement) {
        Project project = psiElement.getProject();
        PsiClass psiClass = (PsiClass) psiElement.getParent();
        ApiDetailConvertProcessor processor = ServiceManager.getProjectInstance(project, ApiDetailConvertProcessor.class);
        boolean supportClass = processor.supportClass(psiClass);
        if (!supportClass) {
            return null;
        }
        String describe = DataExtractUtil.getClassDescribe(psiClass);
        return new LineMarkerInfo<>(psiElement, psiElement.getTextRange(), HTTP_ICON,
                                    element -> "接口文档：" + describe,
                                    this::clickAction,
                                    GutterIconRenderer.Alignment.RIGHT,
                                    () -> "OpenAPI操作");
    }


    private LineMarkerInfo<?> createMethodLineMarker(PsiElement psiElement) {
        Project project = psiElement.getProject();
        PsiMethod psiMethod = (PsiMethod) psiElement.getParent();
        ApiDetailConvertProcessor processor = ServiceManager.getProjectInstance(project, ApiDetailConvertProcessor.class);
        boolean supportMethod = processor.supportMethod(psiMethod);
        if (!supportMethod) {
            return null;
        }
        String describe = DataExtractUtil.getMethodDescribe(psiMethod);
        return new LineMarkerInfo<>(psiElement, psiElement.getTextRange(), HTTP_ICON,
                                    e -> "接口：" + describe,
                                    this::clickAction,
                                    GutterIconRenderer.Alignment.RIGHT,
                                    () -> "OpenAPI操作");
    }

    private <T extends PsiElement> void clickAction(MouseEvent mouseEvent, PsiElement psiElement) {
        Project project = psiElement.getProject();
        ApiDetailConvertProcessor openApiConvertProcessor = ServiceManager.getProjectInstance(project, ApiDetailConvertProcessor.class);
        PsiElement psiElementParent = psiElement.getParent();
        OpenAPI openapi = null;
        if (psiElementParent instanceof PsiClass) {
            PsiClass targetClass = (PsiClass) psiElementParent;
            RootToolWindowFactory.showWindow(project, () -> ApiRestfulProjectService.getInstance(project).gotoClass(targetClass));
        } else if (psiElementParent instanceof PsiMethod) {
            PsiMethod targetMethod = (PsiMethod) psiElementParent;
            // PsiClass targetClass = PsiTreeUtil.getParentOfType(targetMethod, PsiClass.class);
            RootToolWindowFactory.showWindow(project, () -> ApiRestfulProjectService.getInstance(project).gotoMethod(targetMethod));
        }
    }
}