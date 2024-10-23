package com.myth.earth.restful.plugin.state;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.ui.JBColor;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Objects;

/**
 * 为每个项目添加配置
 *
 * @author zhouchao
 * @date 2023-06-12 18:57
 */
public class RestfulHelperConfigurable implements Configurable {

    /**
     * 当前项目
     */
    private final Project                   project;
    /**
     * 项目设置视图
     */
    private final RestfulSettingView        restfulSettingView;
    /**
     * 项目配置
     */
    private final RestfulHelperProjectState restfulHelperProjectState;

    public RestfulHelperConfigurable(Project project) {
        this.project = project;
        this.restfulSettingView = new RestfulSettingView();
        this.restfulHelperProjectState = RestfulHelperProjectState.getInstance(project);
    }

    @Override
    @Nls(capitalization = Nls.Capitalization.Title)
    public String getDisplayName() {
        return "Earth Restful Helper";
    }

    @Override
    @Nullable
    public JComponent createComponent() {
        JPanel root = restfulSettingView.getPanel();
        restfulSettingView.init(RestfulHelperProjectState.getInstance(project));
        return root;
    }

    @Override
    public boolean isModified() {
        if (!Objects.equals(restfulHelperProjectState.curlHost, restfulSettingView.getCurlHost())) {
            return true;
        }

        if (!Objects.equals(restfulHelperProjectState.apiGroupType, restfulSettingView.getApiGroupType())) {
            return true;
        }

        return false;
    }

    @Override
    public void apply() throws ConfigurationException {
        restfulHelperProjectState.curlHost = restfulSettingView.getCurlHost();
        restfulHelperProjectState.apiGroupType = restfulSettingView.getApiGroupType();
        restfulSettingView.tip("设置成功！", JBColor.GREEN);
    }
}

