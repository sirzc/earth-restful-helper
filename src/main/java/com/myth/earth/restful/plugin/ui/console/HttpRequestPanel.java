package com.myth.earth.restful.plugin.ui.console;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.ui.border.CustomLineBorder;
import com.intellij.util.ui.JBUI;
import com.myth.earth.restful.consts.ProjectConst;
import com.myth.earth.restful.core.analyze.processor.ApiDetailConvertProcessor;
import com.myth.earth.restful.enums.FileTypeEnum;
import com.myth.earth.restful.enums.HttpMethod;
import com.myth.earth.restful.enums.RequestType;
import com.myth.earth.restful.helper.HttpRequestHelper;
import com.myth.earth.restful.model.ApiDetail;
import com.myth.earth.restful.model.HttpRequestRef;
import com.myth.earth.restful.model.ParamInfo;
import com.myth.earth.restful.model.RestfulClassMethod;
import com.myth.earth.restful.plugin.ServiceManager;
import com.myth.earth.restful.plugin.state.RestfulHelperGlobalState;
import com.myth.earth.restful.plugin.state.RestfulHelperProjectState;
import com.myth.earth.restful.plugin.ui.editor.MyEditorTextField;
import com.myth.earth.restful.plugin.ui.table.TablePanelAdapter;
import com.myth.earth.restful.plugin.ui.table.model.HeaderModel;
import com.myth.earth.restful.plugin.ui.table.model.HostModel;
import com.myth.earth.restful.plugin.ui.table.model.PairsModel;
import com.myth.earth.restful.utils.DataExtractUtil;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * http 请求panel
 *
 * @author zhouchao
 * @date 2024-06-12 下午7:14
 */
public class HttpRequestPanel {
    private final Project                       project;
    private final TablePanelAdapter<PairsModel> headerTableAdapter;
    private final TablePanelAdapter<PairsModel> bodyFormTableAdapter;
    private final TablePanelAdapter<PairsModel> bodyWwwTableAdapter;
    private final MyEditorTextField             rawEditorTextField;
    private final MyEditorTextField             responseEditorTextField;
    private       JPanel                        rootPanel;
    private       JPanel                        urlPanel;
    private       JTabbedPane                   tabbedPane;
    private       JPanel                        headerPanel;
    private       JPanel                        bodyPanel;
    private       JPanel                        searchPanel;
    private       JComboBox<HttpMethod>         httpMethodBox;
    private       JTextField                    urlField;
    private       JPanel                        buttonPanel;
    private       JButton                       sendButton;
    private       JRadioButton                  formDataRadioButton;
    private       JRadioButton                  xWwwFormUrlencodedRadioButton;
    private       JRadioButton                  rawRadioButton;
    private       JComboBox<RequestType>        rawTypeComboBox;
    private       JPanel                        bodyBoxPanel;
    private       JPanel                        bodyInfoPanel;
    private       JPanel                        responsePanel;
    private       JComboBox<FileTypeEnum>       responseTypeComboBox;
    private       JPanel                        responseOptionPanel;
    private       JPanel                        responseBodyPanel;
    private       JComboBox<HostModel>          hostComboBox;
    private       JLabel                        apiNameLabel;

    public HttpRequestPanel(@NotNull Project project) {
        // 初始化数据
        this.project = project;
        this.rootPanel.setBorder(new CustomLineBorder(JBUI.insetsTop(1)));

        Border emptyBorder = BorderFactory.createEmptyBorder();
        this.httpMethodBox.setBorder(emptyBorder);
        this.urlField.setBorder(emptyBorder);
        this.buttonPanel.setBorder(emptyBorder);

        this.apiNameLabel.setText("新建请求");
        // 全局host设置
        List<HostModel> hostModels = RestfulHelperGlobalState.getInstance().getHostModels();
        hostModels.forEach(hostComboBox::addItem);
        // 初始化下拉数据
        HttpMethod.TYPES.forEach(httpMethodBox::addItem);

        this.urlPanel.setBorder(new CustomLineBorder(JBUI.insets(1, 0)));
        this.searchPanel.setBorder(new CustomLineBorder(JBUI.insetsRight(1)));
        this.httpMethodBox.setPreferredSize(new Dimension(70, httpMethodBox.getPreferredSize().height));

        // 发送按钮初始化+监听
        this.sendButton.setMinimumSize(new Dimension(90, 35));
        this.sendButton.setMaximumSize(new Dimension(90, 35));
        this.sendButton.setPreferredSize(new Dimension(90, 35));
        this.sendButton.addActionListener(e -> executeHttp());

        // header显示编辑区
        this.headerTableAdapter = new TablePanelAdapter<>(PairsModel.class);
        this.headerPanel.add(headerTableAdapter.createTableAndToolbar(), BorderLayout.CENTER);

        // 默认选中：raw，请求内容为：json格式
        this.rawEditorTextField = new MyEditorTextField(project, RequestType.RAW_JSON.getFileType());
        this.rawEditorTextField.setBorder(emptyBorder);
        this.rawRadioButton.setSelected(true);
        this.rawTypeComboBox.setSelectedItem(RequestType.RAW_JSON);
        this.bodyInfoPanel.add(rawEditorTextField, BorderLayout.CENTER);

        // body panel:form|www|raw
        this.bodyFormTableAdapter = new TablePanelAdapter<>(PairsModel.class);
        JPanel bodyFormPanel = bodyFormTableAdapter.createTableAndToolbar();
        this.bodyWwwTableAdapter = new TablePanelAdapter<>(PairsModel.class);
        JPanel bodyWwwPanel = bodyWwwTableAdapter.createTableAndToolbar();

        // body按钮区：form、www、raw
        this.bodyBoxPanel.setBorder(new CustomLineBorder(JBUI.insetsBottom(1)));
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(formDataRadioButton);
        buttonGroup.add(xWwwFormUrlencodedRadioButton);
        buttonGroup.add(rawRadioButton);
        // 添加ActionListener到每个按钮，但通常只需对ButtonGroup操作即可，此处为示例添加到每个按钮
        ActionListener radioListener = createBodyRadioButtonListener(bodyFormPanel, bodyWwwPanel);
        this.formDataRadioButton.addActionListener(radioListener);
        this.xWwwFormUrlencodedRadioButton.addActionListener(radioListener);
        this.rawRadioButton.addActionListener(radioListener);

        // raw条件下，下拉选择请求类型
        RequestType.RAW_LIST.forEach(rawTypeComboBox::addItem);
        this.rawTypeComboBox.addActionListener(e -> refreshBodyRawType());

        // 响应内容加载
        this.responseEditorTextField = new MyEditorTextField(project, FileTypeEnum.JSON.getFileType());
        this.responseOptionPanel.setBorder(new CustomLineBorder(JBUI.insetsBottom(1)));
        this.responseBodyPanel.setBorder(emptyBorder);
        this.responseBodyPanel.add(responseEditorTextField, BorderLayout.CENTER);
        for (FileTypeEnum value : FileTypeEnum.values()) {
            this.responseTypeComboBox.addItem(value);
        }
        this.responseTypeComboBox.setSelectedItem(FileTypeEnum.JSON);
        this.responseTypeComboBox.addActionListener(e -> refreshResponseType());
    }

    private void refreshBodyRawType() {
        RequestType requestType = (RequestType) rawTypeComboBox.getSelectedItem();
        if (requestType != null) {
            rawTypeComboBox.setSelectedItem(requestType);
            rawEditorTextField.setFileType(requestType.getFileType());
        }
    }

    private void refreshResponseType() {
        FileTypeEnum selectedItem = (FileTypeEnum) responseTypeComboBox.getSelectedItem();
        if (selectedItem != null) {
            this.responseEditorTextField.setFileType(selectedItem.getFileType());
        }
    }

    public JComponent getRootPanel() {
        return rootPanel;
    }

    @NotNull
    private ActionListener createBodyRadioButtonListener(@NotNull JPanel bodyFormPanel, @NotNull JPanel bodyWwwPanel) {
        return e -> {
            this.bodyInfoPanel.removeAll();
            this.rawTypeComboBox.setVisible(false);
            if (this.formDataRadioButton.isSelected()) {
                this.bodyInfoPanel.add(bodyFormPanel, BorderLayout.CENTER);
                return;
            }

            if (this.xWwwFormUrlencodedRadioButton.isSelected()) {
                this.bodyInfoPanel.add(bodyWwwPanel, BorderLayout.CENTER);
                return;
            }

            this.rawTypeComboBox.setVisible(true);
            this.bodyInfoPanel.add(rawEditorTextField, BorderLayout.CENTER);
        };
    }

    private void resetRootUI() {
        this.apiNameLabel.setText(null);
        this.urlField.setText(null);
        this.bodyWwwTableAdapter.reset();
        this.bodyFormTableAdapter.reset();
        this.headerTableAdapter.reset();
        this.bodyInfoPanel.removeAll();
        this.rawEditorTextField.setText(null);
        this.rawTypeComboBox.setVisible(false);
        this.tabbedPane.setSelectedComponent(headerPanel);
        this.responseEditorTextField.setText(null);
        this.sendButton.setEnabled(true);
    }

    private void selectRawUI(@NotNull RequestType requestType) {
        this.rawRadioButton.setSelected(true);
        this.rawTypeComboBox.setVisible(true);
        this.rawTypeComboBox.setSelectedItem(requestType);
        this.bodyInfoPanel.add(rawEditorTextField, BorderLayout.CENTER);
    }

    private void selectFormDataUI() {
        this.formDataRadioButton.setSelected(true);
    }

    private void selectXWwwFormUrlencodedUI() {
        this.xWwwFormUrlencodedRadioButton.setSelected(true);
    }

    public void refreshHttpRequest(@NotNull RestfulClassMethod restfulClassMethod) {
        this.resetRootUI();
        this.apiNameLabel.setText("新建请求");
        this.urlField.setText(restfulClassMethod.getApiPath());
        this.httpMethodBox.setSelectedItem(restfulClassMethod.getHttpMethod());

        ApiDetailConvertProcessor apiDetailConvertProcessor = ServiceManager.getProjectInstance(project, ApiDetailConvertProcessor.class);
        List<ApiDetail> analyzeResult = apiDetailConvertProcessor.getAnalyzeResult(restfulClassMethod.getPsiClass(), restfulClassMethod.getPsiMethod());
        if (analyzeResult == null || analyzeResult.size() != 1) {
            return;
        }

        // 分析后的数据信息
        ApiDetail apiDetail = analyzeResult.get(0);
        this.apiNameLabel.setText(apiDetail.getApiName());
        this.urlField.setText(apiDetail.getApiExamplePath());
        RequestType requestType = apiDetail.getRequestType();
        List<ParamInfo> paramInfos = Optional.ofNullable(apiDetail.getRequestParams()).orElse(new ArrayList<>());
        if (RequestType.X_WWW_FORM_URLENCODED.equals(requestType)) {
            selectXWwwFormUrlencodedUI();
            for (ParamInfo requestParam : paramInfos) {
                this.bodyWwwTableAdapter.add(new PairsModel(requestParam.getName(), DataExtractUtil.getParamValue(requestParam)));
            }
        } else if (RequestType.FROM_DATA.equals(requestType)) {
            selectFormDataUI();
            for (ParamInfo requestParam : paramInfos) {
                this.bodyFormTableAdapter.add(new PairsModel(requestParam.getName(), DataExtractUtil.getParamValue(requestParam)));
            }
        } else {
            selectRawUI(requestType);
            String text = paramInfos.stream().findAny().map(DataExtractUtil::getParamValue).orElse("");
            this.rawEditorTextField.setText(text, requestType.getFileType());
        }
        this.tabbedPane.setSelectedComponent(bodyPanel);
    }

    public void refreshHostComboBox(@NotNull List<HostModel> hostModels) {
        hostComboBox.removeAllItems();
        // 全局host设置
        hostModels.forEach(hostComboBox::addItem);
    }

    private void executeHttp() {
        ProgressManager.getInstance().run(new Task.Backgroundable(project, "Running request ...", true) {
            @Override
            public void run(@NotNull ProgressIndicator progressIndicator) {
                sendButton.setEnabled(false);
                String execute = null;
                try {
                    execute = HttpRequestHelper.execute(buildHttpRequestRef());
                } catch (Exception e) {
                    execute = e.getMessage();
                } finally {
                    String finalExecute = execute;
                    ApplicationManager.getApplication().invokeLater(() -> {
                        responseEditorTextField.setText(finalExecute, FileTypeEnum.JSON.getFileType());
                        tabbedPane.setSelectedComponent(responsePanel);
                    }, ModalityState.defaultModalityState());

                }
                sendButton.setEnabled(true);
            }

            @Override
            public void onCancel() {
                sendButton.setEnabled(true);
            }
        });
    }

    private HttpRequestRef buildHttpRequestRef() {
        String url = generateUrl(project, urlField.getText());
        HttpMethod httpMethod = (HttpMethod) httpMethodBox.getSelectedItem();
        HttpRequestRef httpRequestRef = new HttpRequestRef();
        httpRequestRef.setHttpMethod(httpMethod);
        httpRequestRef.setUrl(url);

        Map<String, String> headerMap = getProjectHeaderMap();
        Map<String, String> userHeaderMap = processTableData(headerTableAdapter, String::valueOf);
        if (!userHeaderMap.isEmpty()) {
            headerMap.putAll(userHeaderMap);
        }

        if (formDataRadioButton.isSelected()) {
            httpRequestRef.setFormParams(processTableData(bodyFormTableAdapter, o -> o));
            headerMap.put(ProjectConst.CONTENT_TYPE, RequestType.FROM_DATA.getDesc());
        } else if (xWwwFormUrlencodedRadioButton.isSelected()) {
            httpRequestRef.setFormParams(processTableData(bodyWwwTableAdapter, o -> o));
            headerMap.put(ProjectConst.CONTENT_TYPE, RequestType.X_WWW_FORM_URLENCODED.getDesc());
        } else {
            httpRequestRef.setOtherParams(rawEditorTextField.getText());
            RequestType requestType = (RequestType) rawTypeComboBox.getSelectedItem();
            if (requestType != null) {
                headerMap.put(ProjectConst.CONTENT_TYPE, requestType.getDesc());
            }
        }
        httpRequestRef.setHeaderMap(headerMap);
        return httpRequestRef;
    }

    @NotNull
    private Map<String, String> getProjectHeaderMap() {
        Map<String, String> headerMap = new HashMap<>(8);
        List<HeaderModel> headerModels = RestfulHelperProjectState.getInstance(project).headerModels;
        if (headerModels != null && !headerModels.isEmpty()) {
            for (HeaderModel headerModel : headerModels) {
                if (headerModel.isSelect()) {
                    headerMap.put(headerModel.getKey(), headerModel.getValue());
                }
            }
        }
        return headerMap;
    }

    private <R> Map<String, R> processTableData(@NotNull TablePanelAdapter<PairsModel> adapter, Function<Object, R> function) {
        List<PairsModel> tableData = adapter.getTableData();
        // 优化性能：避免在每次调用中创建匿名函数
        Predicate<PairsModel> isSelect = PairsModel::isSelect;
        Function<PairsModel, String> getKey = PairsModel::getKey;
        Function<PairsModel, R> getValue = b -> function.apply(b.getValue());
        return tableData.stream().filter(isSelect).collect(Collectors.toMap(getKey, getValue, (o1, o2) -> o2));
    }

    private String generateUrl(@NotNull Project project, @Nullable String root) {
        if (root == null || root.startsWith("http")) {
            return root;
        }

        RestfulHelperProjectState projectState = RestfulHelperProjectState.getInstance(project);
        if (StringUtils.isBlank(projectState.contextPath)) {
            return root;
        }

        HostModel hostModel = (HostModel) hostComboBox.getSelectedItem();
        String host = Optional.ofNullable(hostModel).map(HostModel::getHost).orElse("");
        return projectState.contextPath.replace("{{host}}", host) + root;
    }
}
