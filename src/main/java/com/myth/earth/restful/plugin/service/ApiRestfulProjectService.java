package com.myth.earth.restful.plugin.service;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.ui.JBSplitter;
import com.intellij.ui.border.CustomLineBorder;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.util.messages.MessageBusConnection;
import com.intellij.util.ui.JBUI;
import com.myth.earth.restful.helper.TreeNodeHelper;
import com.myth.earth.restful.kits.ToolbarKit;
import com.myth.earth.restful.model.RestfulClassMethod;
import com.myth.earth.restful.plugin.enums.RestfulOperateEnum;
import com.myth.earth.restful.plugin.ui.ApiRestfulTreePanel;
import com.myth.earth.restful.plugin.ui.console.HttpRequestPanel;
import com.myth.earth.restful.plugin.ui.table.model.HostModel;
import com.myth.earth.restful.plugin.ui.tree.topic.RefreshActionNotifier;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

/**
 * api 控制台面板
 *
 * @author zhouchao
 * @date 2024-05-18 下午4:41
 */
public class ApiRestfulProjectService {

    private final Project              project;
    private final ApiRestfulTreePanel  apiRestfulTreePanel;
    private final MessageBusConnection messageBusConnection;
    private final HttpRequestPanel     httpRequestPanel;

    public ApiRestfulProjectService(@NotNull Project project) {
        this.project = project;
        this.apiRestfulTreePanel = new ApiRestfulTreePanel(project, getChooseCallback());
        this.httpRequestPanel = new HttpRequestPanel(project);
        this.messageBusConnection = project.getMessageBus().connect();
        this.messageBusConnection.subscribe(RefreshActionNotifier.REFRESH_ACTION_TOPIC, (RefreshActionNotifier) this::refreshTree);
        this.project.getMessageBus().syncPublisher(RefreshActionNotifier.REFRESH_ACTION_TOPIC).refresh();
    }

    public static ApiRestfulProjectService getInstance(@NotNull Project project) {
        return project.getService(ApiRestfulProjectService.class);
    }

    public void initToolWindow(@NotNull ToolWindow toolWindow) {
        ContentFactory contentFactory = ApplicationManager.getApplication().getService(ContentFactory.class);
        Content content = contentFactory.createContent(getRootComponent(), "", false);
        toolWindow.getContentManager().addContent(content);
    }

    public JComponent getRootComponent() {
        JPanel jPanel = new JPanel(new BorderLayout());

        // 树 + console
        JBSplitter splitter = new JBSplitter(true, "EarthRestfulHelper.splitter", 0.65f);
        splitter.setFirstComponent(apiRestfulTreePanel);
        splitter.setSecondComponent(httpRequestPanel.getRootPanel());

        // toolbar
        JComponent actionToolbar = ToolbarKit.createActionToolbar(jPanel, "EarthRestfulHelper.TreeToolbar", false);
        actionToolbar.setBorder(new CustomLineBorder(JBUI.insetsRight(1)));

        jPanel.add(splitter, BorderLayout.CENTER);
        jPanel.add(actionToolbar, BorderLayout.WEST);
        return jPanel;
    }

    public void gotoMethod(@NotNull PsiMethod psiMethod) {
        apiRestfulTreePanel.gotoApiMethod(psiMethod);
    }

    public void gotoClass(@NotNull PsiClass psiClass) {
        apiRestfulTreePanel.gotoApiClass(psiClass);
    }

    public void runActionHandler(@NotNull RestfulOperateEnum restfulOperateEnum) {
        switch (restfulOperateEnum) {
            case REFRESH:
                project.getMessageBus().syncPublisher(RefreshActionNotifier.REFRESH_ACTION_TOPIC).refresh();
                break;
            case EXPAND:
                if (apiRestfulTreePanel.canExpand()) {
                    apiRestfulTreePanel.expandAll();
                }
                break;
            case COLLAPSE:
                if (apiRestfulTreePanel.canCollapse()) {
                    apiRestfulTreePanel.collapseAll();
                }
                break;
            default:
                throw new IllegalArgumentException("未知操作类型" + restfulOperateEnum);
        }
    }

    public void refreshHostComboBox(@NotNull List<HostModel> hostModels) {
        httpRequestPanel.refreshHostComboBox(hostModels);
    }

    private @NotNull Consumer<RestfulClassMethod> getChooseCallback() {
        return restfulClassMethod -> {
            httpRequestPanel.refreshHttpRequest(restfulClassMethod);
        };
    }

    private void refreshTree() {
        apiRestfulTreePanel.renderAll(TreeNodeHelper.getModuleTreeNodes(project), false);
    }
}
