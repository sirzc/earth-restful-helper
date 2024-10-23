package com.myth.earth.restful.helper;

import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.myth.earth.restful.consts.ProjectConst;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

/**
 * IDEA 中文件相关操作
 *
 * @author zhouchao
 * @date 2023-05-05 19:57
 */
public class IdeaFileOperateHelper {

    /**
     * 获取用户选择路径
     *
     * @param project 项目
     * @return 用户选择目录
     */
    @Nullable
    public static String getUserSelectPath(@NotNull Project project) {
        FileChooserDescriptor fileChooserDescriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor();
        fileChooserDescriptor.setForcedToUseIdeaFileChooser(true);
        VirtualFile chooser = FileChooser.chooseFile(fileChooserDescriptor, project, null);
        if (chooser == null) {
            return null;
        }
        return chooser.getPath();
    }

    /**
     * 获取临时目录位置
     *
     * @param project 项目
     * @return 临时目录
     */
    public static String getTemporaryDirectory(@NotNull Project project) {
        // 零时文件路径
        String tempFolderPath = project.getBasePath() + File.separator + ProjectConst.IDEA + File.separator + ProjectConst.PROJECT_OUTPUT_DIRECTORY;
        File directory = new File(tempFolderPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        return tempFolderPath;
    }
}
