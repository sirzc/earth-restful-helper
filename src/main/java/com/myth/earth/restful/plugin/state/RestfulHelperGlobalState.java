package com.myth.earth.restful.plugin.state;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.myth.earth.restful.plugin.ServiceManager;
import com.myth.earth.restful.plugin.ui.table.model.HostModel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhouchao
 * @date 2024-06-19 上午12:24
 */
@State(name = "com.myth.earth.restful.plugin.state.RestfulHelperGlobalState", storages = {@Storage("EarthRestfulHelper-setting.xml")})
public class RestfulHelperGlobalState implements PersistentStateComponent<RestfulHelperGlobalState> {

    /**
     * 环境列表
     */
    private List<HostModel> hostModels;

    public static RestfulHelperGlobalState getInstance() {
        return ServiceManager.getApplicationInstance(RestfulHelperGlobalState.class);
    }

    public List<HostModel> getHostModels() {
        if (hostModels == null) {
            hostModels = new ArrayList<>();
            hostModels.add(new HostModel("localhost", "http://localhost:8080"));
        }
        return hostModels;
    }

    public void setHostModels(List<HostModel> hostModels) {
        this.hostModels = hostModels;
    }

    @Override
    public @Nullable RestfulHelperGlobalState getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull RestfulHelperGlobalState restfulHelperGlobalState) {
        XmlSerializerUtil.copyBean(restfulHelperGlobalState, this);
    }
}
