package com.myth.earth.restful.plugin.ui.tree.topic;

import com.intellij.util.messages.Topic;

/**
 * 刷新动作通知
 *
 * @author zhouchao
 * @date 2024-05-25 上午8:49
 */
public interface RefreshActionNotifier {

    @Topic.ProjectLevel
    Topic<RefreshActionNotifier> REFRESH_ACTION_TOPIC = Topic.create("refresh api tree", RefreshActionNotifier.class);

    /**
     * 刷新
     */
    void refresh();
}
