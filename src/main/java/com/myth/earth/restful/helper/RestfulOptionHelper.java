package com.myth.earth.restful.helper;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.myth.earth.restful.consts.ProjectConst;
import com.myth.earth.restful.core.analyze.processor.ApiDetailConvertProcessor;
import com.myth.earth.restful.core.analyze.processor.OpenApiConvertProcessor;
import com.myth.earth.restful.core.builder.OpenApiBuilder;
import com.myth.earth.restful.enums.RequestType;
import com.myth.earth.restful.model.ApiDetail;
import com.myth.earth.restful.model.Document;
import com.myth.earth.restful.model.ParamInfo;
import com.myth.earth.restful.plugin.ServiceManager;
import com.myth.earth.restful.plugin.state.RestfulHelperProjectState;
import com.myth.earth.restful.utils.DataExtractUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;

/**
 * restful 操作助手
 *
 * @author zhouchao
 * @date 2024-06-19 下午11:40
 */
public class RestfulOptionHelper {

    public static void uploadOpenapi(@NotNull Project project, @NotNull PsiClass psiClass) {
        OpenApiConvertProcessor openApiConvertProcessor = ServiceManager.getProjectInstance(project, OpenApiConvertProcessor.class);
        OpenApiBuilder builder = openApiConvertProcessor.getAnalyzeResult(psiClass);
        if (Objects.nonNull(builder)) {
            ExportOpenapiHelper.exportOpenapiJson(project, builder.build(project.getName()));
        }
    }

    public static void uploadOpenapi(@NotNull Project project, @NotNull PsiMethod psiMethod) {
        PsiClass psiClass = psiMethod.getContainingClass();
        if (psiClass == null) {
            return;
        }
        OpenApiConvertProcessor openApiConvertProcessor = ServiceManager.getProjectInstance(project, OpenApiConvertProcessor.class);
        OpenApiBuilder builder = openApiConvertProcessor.getAnalyzeResult(psiClass, psiMethod);
        if (Objects.nonNull(builder)) {
            ExportOpenapiHelper.exportOpenapiJson(project, builder.build(project.getName()));
        }
    }

    public static void copyCurl(@NotNull Project project, @NotNull PsiMethod psiMethod) {
        PsiClass psiClass = psiMethod.getContainingClass();
        if (psiClass == null) {
            return;
        }
        // 获取解析处理器
        ApiDetailConvertProcessor routeHandle = ServiceManager.getProjectInstance(project, ApiDetailConvertProcessor.class);
        List<ApiDetail> apiDetails = routeHandle.getAnalyzeResult(psiClass, psiMethod);
        if (CollectionUtils.isNotEmpty(apiDetails)) {
            // 获取项目配置
            RestfulHelperProjectState projectState = RestfulHelperProjectState.getInstance(project);
            String curlHost = Optional.ofNullable(projectState.curlHost).filter(StringUtils::isNotBlank).orElse(ProjectConst.DEFAULT_HOST);
            // 生成curl命令
            ApiDetail apiDetail = apiDetails.get(0);
            String baseCurl = String.format(ProjectConst.CURL_FORMAT, apiDetail.getHttpMethod().name(), curlHost + apiDetail.getApiExamplePath(),
                                            apiDetail.getRequestType().getDesc());
            // 生成参数信息
            StringJoiner paramJoiner = new StringJoiner("");
            List<ParamInfo> requestParams = apiDetail.getRequestParams();
            if (CollectionUtils.isNotEmpty(requestParams)) {
                switch (apiDetail.getRequestType()) {
                    case RAW_JSON:
                    case RAW_TEXT:
                        String paramText = DataExtractUtil.getParamValue(requestParams.get(0));
                        paramJoiner.add("--data-raw ").add("'" + paramText + "'");
                        break;
                    case X_WWW_FORM_URLENCODED:
                        for (int i = 0; i < requestParams.size(); i++) {
                            ParamInfo paramInfo = requestParams.get(i);
                            String paramValue = DataExtractUtil.getParamValue(paramInfo);
                            paramJoiner.add("--data-urlencode ").add("'" + paramInfo.getName() + "=" + paramValue + "'");
                            if (i != requestParams.size() - 1) {
                                paramJoiner.add(" \\\n");
                            }
                        }
                        break;
                    default:
                        break;
                }
            } else {
                paramJoiner.add("--data-raw").add("''");
            }
            ClipboardHelper.copy(baseCurl + paramJoiner);
        }
    }

    public static void generateMarkdown(@NotNull Project project, @NotNull PsiMethod psiMethod) {
        PsiClass psiClass = psiMethod.getContainingClass();
        if (psiClass == null) {
            return;
        }
        ApiDetailConvertProcessor routeHandle = ServiceManager.getProjectInstance(project, ApiDetailConvertProcessor.class);
        List<ApiDetail> apiDetails = routeHandle.getAnalyzeResult(psiClass, psiMethod);
        if (apiDetails == null || apiDetails.size() != 1) {
            return;
        }

        Document document = convertApiDetailToDocument(apiDetails.get(0));
        // 零时文件路径
        String tempFolderPath = project.getBasePath() + File.separator + ".idea" + File.separator + ProjectConst.PROJECT_OUTPUT_DIRECTORY;
        File directory = new File(tempFolderPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        String absolutePath = tempFolderPath + File.separator + document.getDesc() + ".md";
        // 输出内容到文件
        // FreemarkerHelper.defaultOutputToFile(document, absolutePath);
        // 打开文件
        // openMarkdownDoc(new File(absolutePath), project);
    }

    private static void openMarkdownDoc(File file, Project project) {
        ApplicationManager.getApplication().invokeAndWait(() -> {
            VirtualFile vf = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(file);

            if (vf == null) {
                return;
            }

            OpenFileDescriptor descriptor = new OpenFileDescriptor(project, vf);
            FileEditorManager.getInstance(project).openTextEditor(descriptor, false);
        });
    }

    private static Document convertApiDetailToDocument(ApiDetail apiDetail) {
        Document document = new Document();
        document.setPath(apiDetail.getApiPath());
        document.setExamplePath(apiDetail.getApiExamplePath());
        document.setDesc(apiDetail.getApiName());
        document.setHttpMethod(apiDetail.getHttpMethod());
        document.setRequestType(apiDetail.getRequestType());
        document.setRequestParams(apiDetail.getRequestParams());
        document.setResponseParam(apiDetail.getResponseParam());
        document.setResponseEg(DataExtractUtil.getParamValue(apiDetail.getResponseParam()));
        if (CollectionUtils.isEmpty(apiDetail.getRequestParams())) {
            return document;
        }
        if (!RequestType.RAW_TEXT.equals(apiDetail.getRequestType()) && !RequestType.RAW_JSON.equals(apiDetail.getRequestType())) {
            return document;
        }
        document.setRequestEg(DataExtractUtil.getParamValue(apiDetail.getRequestParams().get(0)));
        return document;
    }
}
