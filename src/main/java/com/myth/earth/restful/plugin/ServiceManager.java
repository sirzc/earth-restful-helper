package com.myth.earth.restful.plugin;

import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
import org.jetbrains.annotations.NotNull;

public final class ServiceManager {

    /**
     * idea应用级别服务管理器
     */
    private static final Application APPLICATION_MANAGER = ApplicationManager.getApplication();

    /**
     * 获取应用级别服务实例
     */
    public static <T> T getApplicationInstance(@NotNull Class<T> clz) {
        return APPLICATION_MANAGER.getService(clz);
    }

    /**
     * 获取项目级别服务实例
     */
    public static <T> T getProjectInstance(@NotNull Project project, @NotNull Class<T> clz) {
        return project.getService(clz);
    }

    /**
     * 运行指定的读取操作。可以从任何线程调用。如果当前没有写入操作正在运行，则立即执行该操作，或者阻塞直到当前正在运行的写入操作完成。
     *
     * @param computable 计算并得到数据
     * @return 计算一个数据
     */
    public static <T> T runReadAction(@NotNull Computable<T> computable) {
        return APPLICATION_MANAGER.runReadAction(computable);
    }

    public static void runReadAction(@NotNull Runnable runnable) {
        APPLICATION_MANAGER.runReadAction(runnable);
    }

    /**
     * 运行指定的写入操作。
     *
     * @param runnable 计算并得到数据
     * @return 计算一个数据
     */
    public static void runWriteAction(@NotNull Runnable runnable) {
        APPLICATION_MANAGER.runWriteAction(runnable);
    }

    /**
     * 调用并等待
     *
     * @param runnable 执行的任务
     */
    public static void invokeAndWait(@NotNull Runnable runnable) {
        APPLICATION_MANAGER.invokeAndWait(runnable);
    }

    /**
     * 该方法接受一个Runnable对象作为参数，并将其加入到事件调度队列中，以便在主线程空闲时执行。
     * 这个方法通常用于需要在后台或其他非主线程中执行的操作，确保它们不会阻塞用户界面的更新和响应。
     * 注意事项：
     * - invokeLater()方法内部使用了SwingUtilities.invokeLater()来进行实际的调度操作。
     * - 可以多次调用invokeLater()方法，以便按照添加顺序执行多个Runnable对象。
     * - Runnable对象中的代码将在队列中排队等待主线程空闲时才会被执行，因此它不保证立即执行。
     * - 如果需要立即执行任务而不管是否为主线程，请使用invokeAndWait()方法。
     *
     * @param runnable 执行的任务
     */
    public static void invokeLater(@NotNull Runnable runnable) {
        APPLICATION_MANAGER.invokeLater(runnable);
    }
}