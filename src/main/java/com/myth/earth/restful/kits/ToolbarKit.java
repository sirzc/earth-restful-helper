package com.myth.earth.restful.kits;

import com.intellij.openapi.actionSystem.*;

import javax.swing.*;

/**
 * tool bar 创建工具类
 *
 * @author zhouchao
 * @date 2024/5/25 下午6:57
 **/
public class ToolbarKit {

    /**
     * 创建 toolbar
     *
     * @param jPanel     toolbar 所在的 panel
     * @param groupId    toolbar 的 action group id
     * @param horizontal 是否水平：true 水平，false 垂直
     * @return actionToolbar
     */
    public static JComponent createActionToolbar(JPanel jPanel, String groupId, boolean horizontal) {
        ActionManager actionManager = ActionManager.getInstance();
        AnAction action = actionManager.getAction(groupId);
        ActionGroup actionGroup = action instanceof ActionGroup ? ((ActionGroup) action) : new DefaultActionGroup();
        ActionToolbar actionToolbar = actionManager.createActionToolbar(ActionPlaces.TOOLBAR, actionGroup, horizontal);
        actionToolbar.setTargetComponent(jPanel);
        return actionToolbar.getComponent();
    }
}
