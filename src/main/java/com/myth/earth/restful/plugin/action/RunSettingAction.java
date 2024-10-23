package com.myth.earth.restful.plugin.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.myth.earth.restful.plugin.dialog.RunSettingDialog;
import org.jetbrains.annotations.NotNull;

/**
 * @author zhouchao
 * @date 2024-06-18 下午11:37
 */
public class RunSettingAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) {
            return;
        }
        RunSettingDialog runSettingDialog = new RunSettingDialog(project);
        runSettingDialog.setVisible(true);
    }
}
