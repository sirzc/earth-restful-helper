package com.myth.earth.restful.plugin.insight.option;

import com.intellij.codeInsight.hints.ChangeListener;
import com.intellij.codeInsight.hints.ImmediateConfigurable;
import com.intellij.java.JavaBundle;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * restful 操作配置
 *
 * @author zhouchao
 * @date 2024-06-19 下午11:46
 */
@SuppressWarnings("all")
public class RestfulOptionConfigurable implements ImmediateConfigurable {

    private RestfulOptionSetting restfulOptionSetting;

    public RestfulOptionConfigurable(@NotNull RestfulOptionSetting restfulOptionSetting) {
        this.restfulOptionSetting = restfulOptionSetting;
    }

    @NotNull
    @Override
    public JComponent createComponent(@NotNull ChangeListener changeListener) {
        JPanel panel = new JPanel();
        panel.setVisible(false);
        return panel;
    }

    @NotNull
    @Override
    public String getMainCheckboxText() {
        return JavaBundle.message("settings.inlay.java.show.hints.for");
    }

    @NotNull
    @Override
    public List<Case> getCases() {
        List<Case> cases = new ArrayList<>();
        cases.add(new Case("上传Openapi", RestfulOptionProvider.RESTFUL_LENS_ID + "-openapi", () -> restfulOptionSetting.isShowUploadOpenapi(), (t) -> {
            restfulOptionSetting.setShowUploadOpenapi(t);
            return null;
        }, null));
        cases.add(new Case("复制Curl", RestfulOptionProvider.RESTFUL_LENS_ID + "-curl", () -> restfulOptionSetting.isShowCopyCurl(), (t) -> {
            restfulOptionSetting.setShowCopyCurl(t);
            return null;
        }, null));
        cases.add(new Case("生成Markdown", RestfulOptionProvider.RESTFUL_LENS_ID + "-markdown", () -> restfulOptionSetting.isShowExportMarkdown(), (t) -> {
            restfulOptionSetting.setShowExportMarkdown(t);
            return null;
        }, null));
        return cases;
    }
}
