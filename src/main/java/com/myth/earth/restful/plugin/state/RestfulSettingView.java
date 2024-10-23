package com.myth.earth.restful.plugin.state;

import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBRadioButton;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * 支持为设置api server 及token {@link JPanel}
 *
 * @author zhouchao
 * @date 2023/6/12 18:57
 **/
public class RestfulSettingView {

    public static final  JBLabel       PLACEHOLDER_LABEL = new JBLabel();
    private static final String        PLACEHOLDER_CHAR  = "      ";
    private final        JPanel        rootPanel;
    private final        JBLabel       tipLabel;
    private final        JBTextField   curlField;
    private final        JBRadioButton classNameRadio;
    private final        JBRadioButton classDescRadio;

    public RestfulSettingView() {
        this.curlField = new JBTextField();
        this.tipLabel = new JBLabel();
        this.classNameRadio = new JBRadioButton("Use class name  ");
        this.classDescRadio = new JBRadioButton("Use class desc  ");

        // 创建一个横向Box容器将两个单选按钮放在同一行
        Box topicGroupBox = createHorizontalBox(classNameRadio, classDescRadio);

        ButtonGroup topicButtonGroup = new ButtonGroup();
        topicButtonGroup.add(classNameRadio);
        topicButtonGroup.add(classDescRadio);

        this.rootPanel = FormBuilder.createFormBuilder().addComponent(crateTitleSeparator("Base setting"))
                                    // .addLabeledComponent("Base setting", new JSeparator(), SwingConstants.LEFT, false)
                                    .addLabeledComponent(new JBLabel(PLACEHOLDER_CHAR + "Api group name:"), topicGroupBox)
                                    .addComponentFillVertically(PLACEHOLDER_LABEL, 0).addComponent(crateTitleSeparator("Curl setting"))
                                    // .addLabeledComponent("Curl setting", new JSeparator(), SwingConstants.LEFT, false)
                                    .addLabeledComponent(new JBLabel(PLACEHOLDER_CHAR + "Curl host :"), curlField)
                                    .addComponentFillVertically(PLACEHOLDER_LABEL, 0).getPanel();
    }

    public JPanel getPanel() {
        return rootPanel;
    }

    public void tip(String tip, JBColor color) {
        this.tipLabel.setText(tip);
        this.tipLabel.setForeground(color);
    }

    public void init(RestfulHelperProjectState restfulHelperProjectState) {
        this.curlField.setText(restfulHelperProjectState.curlHost);
        switch (restfulHelperProjectState.apiGroupType) {
            case 0:
                this.classNameRadio.setSelected(true);
                break;
            case 1:
                this.classDescRadio.setSelected(true);
                break;
            default:
                break;
        }
    }

    public String getCurlHost() {
        return this.curlField.getText();
    }

    public int getApiGroupType() {
        if (classNameRadio.isSelected()) {
            return 0;
        }
        if (classDescRadio.isSelected()) {
            return 1;
        }
        return 0;
    }

    /**
     * 创建一个水平Box
     *
     * @param jComponents 多个JComponent
     * @return 水平Box
     */
    private Box createHorizontalBox(@NotNull JComponent... jComponents) {
        Box horizontalBox = Box.createHorizontalBox();
        for (JComponent jComponent : jComponents) {
            horizontalBox.add(jComponent);
        }
        return horizontalBox;
    }

    /**
     * 创建一个标题和分割线 （在水平同一方向）
     *
     * @param title 标题
     * @return 水平Box
     */
    private Box crateTitleSeparator(@NotNull String title) {
        JBLabel jbLabel = new JBLabel(title + "  ");
        JSeparator jSeparator = new JSeparator();
        jSeparator.setAlignmentY(JComponent.TOP_ALIGNMENT);
        return createHorizontalBox(jbLabel, jSeparator);
    }
}