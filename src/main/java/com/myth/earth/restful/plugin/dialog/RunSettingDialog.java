package com.myth.earth.restful.plugin.dialog;

import com.intellij.openapi.project.Project;
import com.intellij.ui.border.CustomLineBorder;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.JBUI;
import com.myth.earth.restful.plugin.service.ApiRestfulProjectService;
import com.myth.earth.restful.plugin.state.RestfulHelperGlobalState;
import com.myth.earth.restful.plugin.state.RestfulHelperProjectState;
import com.myth.earth.restful.plugin.ui.table.TablePanelAdapter;
import com.myth.earth.restful.plugin.ui.table.model.HeaderModel;
import com.myth.earth.restful.plugin.ui.table.model.HostModel;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class RunSettingDialog extends JDialog {
    private final Project                        project;
    private final TablePanelAdapter<HostModel>   hostModelTablePanelAdapter;
    private final TablePanelAdapter<HeaderModel> headerModelTablePanelAdapter;
    private       JPanel                         contentPane;
    private       JButton                        buttonOK;
    private       JPanel                         globalPanel;
    private       JBTextField                    contextPathField;
    private       JPanel                         headerPanel;

    public RunSettingDialog(@NotNull Project project) {
        this.project = project;
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        setSize(600, 600);
        setTitle("Earth Restful Helper");
        setResizable(true);
        setLocationRelativeTo(null);
        buttonOK.addActionListener(e -> onOK());

        CustomLineBorder lineBorder = new CustomLineBorder(JBUI.insets(1));
        // project ui
        this.contextPathField.getEmptyText().setText("Eg：{{host}}/demo or http://127.0.0.1:8080/demo");
        this.headerModelTablePanelAdapter = new TablePanelAdapter<>(HeaderModel.class);
        JPanel headerTable = headerModelTablePanelAdapter.createTableAndToolbar();
        headerTable.setBorder(lineBorder);
        this.headerPanel.add(headerTable, BorderLayout.CENTER);

        // global ui
        this.hostModelTablePanelAdapter = new TablePanelAdapter<>(HostModel.class);
        JPanel hostTable = hostModelTablePanelAdapter.createTableAndToolbar();
        hostTable.setBorder(lineBorder);
        this.globalPanel.add(hostTable, BorderLayout.CENTER);

        // project + global setting init
        this.initData();
    }

    private void onOK() {
        RestfulHelperProjectState projectState = RestfulHelperProjectState.getInstance(project);
        projectState.contextPath = contextPathField.getText();

        List<HeaderModel> headerTableData = headerModelTablePanelAdapter.getTableData();
        if (CollectionUtils.isNotEmpty(headerTableData)) {
            projectState.headerModels = headerTableData;
        }

        List<HostModel> hostTableData = hostModelTablePanelAdapter.getTableData();
        if (CollectionUtils.isNotEmpty(hostTableData)) {
            RestfulHelperGlobalState globalState = RestfulHelperGlobalState.getInstance();
            globalState.setHostModels(hostTableData);
            ApiRestfulProjectService.getInstance(project).refreshHostComboBox(hostTableData);
        }
        dispose();
    }

    private void initData() {
        RestfulHelperProjectState projectState = RestfulHelperProjectState.getInstance(project);
        if (StringUtils.isNotBlank(projectState.contextPath)) {
            this.contextPathField.setText(projectState.contextPath);
        }

        if (CollectionUtils.isNotEmpty(projectState.headerModels)) {
            projectState.headerModels.forEach(headerModelTablePanelAdapter::add);
        }

        RestfulHelperGlobalState globalState = RestfulHelperGlobalState.getInstance();
        List<HostModel> hostModels = globalState.getHostModels();
        if (CollectionUtils.isNotEmpty(hostModels)) {
            hostModels.forEach(hostModelTablePanelAdapter::add);
        }
    }

}
