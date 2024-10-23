package com.myth.earth.restful.plugin.state;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.myth.earth.restful.plugin.ServiceManager;
import com.myth.earth.restful.plugin.ui.table.model.HeaderModel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * 项目设置
 *
 * @author zhouchao
 * @date 2023-06-12 18:57
 */
@State(name = "com.myth.earth.restful.plugin.state.RestfulHelperProjectState", storages = {@Storage("EarthRestfulHelper-setting.xml")})
public class RestfulHelperProjectState implements PersistentStateComponent<RestfulHelperProjectState> {

    /**
     * curl地址
     */
    public String           curlHost;
    /**
     * Api分组类型：0：类名，1：类描述
     */
    public int              apiGroupType;
    /**
     * 上下文路径
     */
    public String            contextPath;
    /**
     * 项目的header列表
     */
    public List<HeaderModel> headerModels;

    public static RestfulHelperProjectState getInstance(Project project) {
        return ServiceManager.getProjectInstance(project, RestfulHelperProjectState.class);
    }

    @Override
    public @Nullable RestfulHelperProjectState getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull RestfulHelperProjectState restfulHelperProjectState) {
        XmlSerializerUtil.copyBean(restfulHelperProjectState, this);
    }
}
