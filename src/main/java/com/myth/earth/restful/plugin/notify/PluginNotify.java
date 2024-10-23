package com.myth.earth.restful.plugin.notify;

import com.intellij.notification.*;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Optional;

/**
 * 插件通知，此插件通知的内容都会在指定的组中显示 <br/>
 * <p>
 * displayType 通知类型 <br/>
 * NONE: 无通知 <br/>
 * BALLOON: 气球通知，10秒后自动过期 <br/>
 * STICKY_BALLOON: 气球通知，不会自动消失，由用户进行关闭 <br/>
 * TOOL_WINDOW: 工具窗口通知 <br/>
 *
 * @author zhouchao
 * @date 2023/2/2 22:18
 **/
public class PluginNotify {

    /**
     * 顶级通知（气球）
     *
     * @sine 203 版本后使用
     */
    @SuppressWarnings("all")
    private static final NotificationGroup NOTIFICATION_GROUP = NotificationGroupManager.getInstance().getNotificationGroup("EarthRestfulHelper.Notify");

    /**
     * 发送一个气球 信息
     *
     * @param project 当前项目
     * @param message 消息
     */
    public static void info(@NotNull Project project, @NotNull String message) {
        notify(project, message, NotificationType.INFORMATION);
    }

    /**
     * 发送一个气球 告警
     *
     * @param project 当前项目
     * @param message 消息
     */
    public static void warn(@NotNull Project project, @NotNull String message) {
        notify(project, message, NotificationType.WARNING);
    }

    /**
     * 发送一个气球 错误
     *
     * @param project 当前项目
     * @param message 消息
     */
    public static void error(@NotNull Project project, @NotNull String message) {
        notify(project, message, NotificationType.ERROR);
    }

    /**
     * 推送一个通知
     *
     * @param project          当前项目
     * @param message          消息
     * @param notificationType 通知类型
     */
    private static void notify(@NotNull Project project, @NotNull String message, NotificationType notificationType) {
        NOTIFICATION_GROUP.createNotification(message, notificationType).notify(project);
    }

    public static void prettifyInfo(@NotNull Project project, @NotNull String message, @Nullable String title, @Nullable Icon icon, @Nullable AnAction action) {
        Notification notification = NOTIFICATION_GROUP.createNotification(message, NotificationType.INFORMATION);
        Optional.ofNullable(title).ifPresent(notification::setTitle);
        Optional.ofNullable(icon).ifPresent(notification::setIcon);
        Optional.ofNullable(action).ifPresent(notification::addAction);
        notification.notify(project);
    }
}
