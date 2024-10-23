package com.myth.earth.restful.plugin.toolwindow;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.openapi.wm.ToolWindowManager;
import com.myth.earth.restful.plugin.service.ApiRestfulProjectService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * API调度控制面板
 *
 * @author zhouchao
 * @date 2024-05-18 下午4:27
 */
public class RootToolWindowFactory implements ToolWindowFactory {

    @Nullable
    public static ToolWindow getToolWindow(@NotNull Project project) {
        return ToolWindowManager.getInstance(project).getToolWindow("EarthRestfulHelper.Tool");
    }

    @Override
    public void init(@NotNull ToolWindow toolWindow) {
        toolWindow.setStripeTitle("Restful Helper");
    }

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        // 面板内容，显示标题，是否可锁定（如果设置为true，则用户无法关闭或移动该内容。如果设置为false，则用户可以自由地关闭或移动它。）
        ApiRestfulProjectService apiRestfulProjectService = ApiRestfulProjectService.getInstance(project);
        apiRestfulProjectService.initToolWindow(toolWindow);
    }

    public static void showWindow(@NotNull Project project, @Nullable Runnable onShow) {
        ToolWindow window = getToolWindow(project);
        if (window == null) {
            return;
        }
        window.show(onShow);
    }
}
