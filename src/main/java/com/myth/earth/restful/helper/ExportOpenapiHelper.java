package com.myth.earth.restful.helper;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.IconLoader;
import com.myth.earth.restful.plugin.insight.ApiLineMarkerProvider;
import com.myth.earth.restful.plugin.notify.PluginNotify;
import io.swagger.v3.oas.models.OpenAPI;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * 导出openapi助手
 *
 * @author zhouchao
 * @date 2024-03-26 8:51
 */
public class ExportOpenapiHelper {

    /**
     * 导出openapi
     *
     * @param project 当前project
     * @param openapi 原始api数据
     */
    public static void exportOpenapiJson(@NotNull Project project, @Nullable OpenAPI openapi) {
        if (Objects.isNull(openapi)) {
            PluginNotify.error(project, "无法解析当前内容，请检查代码是否正确！");
            return;
        }

        String temporaryDirectory = IdeaFileOperateHelper.getTemporaryDirectory(project);
        try {
            String pathname = temporaryDirectory + File.separator + "openapi.json";
            FileUtils.writeStringToFile(new File(pathname), getOpenapiJson(openapi), StandardCharsets.UTF_8);
            Desktop.getDesktop().open(new File(temporaryDirectory));
        } catch (Exception ex) {
            PluginNotify.error(project, "openapi生成异常！" + ex.getMessage());
        }
    }

    /**
     * 获取openapi json文件
     *
     * @param openapi OpenAPI
     * @return json
     */
    private static String getOpenapiJson(OpenAPI openapi) {
        SimplePropertyPreFilter filter = new SimplePropertyPreFilter();
        filter.getExcludes().add("exampleSetFlag");
        filter.getExcludes().add("specVersion");
        filter.getExcludes().add("types");
        return JSON.toJSONString(openapi, filter);
    }
}
