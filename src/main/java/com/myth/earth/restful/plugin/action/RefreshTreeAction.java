package com.myth.earth.restful.plugin.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.myth.earth.restful.plugin.enums.RestfulOperateEnum;
import com.myth.earth.restful.plugin.service.ApiRestfulProjectService;
import org.jetbrains.annotations.NotNull;

/**
 * 刷新树信息
 *
 * @author zhouchao
 * @date 2024-05-25 下午7:01
 */
public class RefreshTreeAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) {
            return;
        }
        ApiRestfulProjectService.getInstance(project).runActionHandler(RestfulOperateEnum.REFRESH);
    }
}
