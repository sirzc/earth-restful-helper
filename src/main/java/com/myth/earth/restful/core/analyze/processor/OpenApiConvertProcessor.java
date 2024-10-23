package com.myth.earth.restful.core.analyze.processor;

import com.google.common.collect.Maps;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.myth.earth.restful.core.builder.OpenApiBuilder;
import com.myth.earth.restful.kits.OpenApiKit;
import com.myth.earth.restful.model.ParamInfo;
import com.myth.earth.restful.model.PsiClassInfo;
import com.myth.earth.restful.model.PsiMethodInfo;
import com.myth.earth.restful.plugin.state.RestfulHelperProjectState;
import com.myth.earth.restful.utils.DataExtractUtil;
import io.swagger.v3.oas.models.media.Schema;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * openapi转换处理器 
 * @date 2023-06-06 19:54
 */
public class OpenApiConvertProcessor extends AbstractConvertProcessor<OpenApiBuilder> {

    private static final Logger log = Logger.getInstance(OpenApiConvertProcessor.class);

    public OpenApiConvertProcessor(@NotNull Project project) {
        super(project);
    }

    @Override
    protected OpenApiBuilder getTargets(@NotNull PsiClassInfo psiClassInfo) {
        OpenApiBuilder builder = new OpenApiBuilder("earth doc view");
        List<PsiMethodInfo> psiMethodInfos = psiClassInfo.getPsiMethodInfos();
        if (CollectionUtils.isEmpty(psiMethodInfos)) {
            return builder;
        }

        String className = psiClassInfo.getPsiClass().getName();
        String classApiPath = psiClassInfo.getClassApiPath();
        String classDescribe = psiClassInfo.getClassDescribe().trim();

        int type = RestfulHelperProjectState.getInstance(getProject()).apiGroupType;
        String topicName = type == 0 ? className : classDescribe;
        String topicDesc = type == 0 ? classDescribe : className;

        // 添加类信息
        List<ParamInfo> schemaParamInfos = new ArrayList<>(psiMethodInfos.size() * 2);
        for (PsiMethodInfo psiMethodInfo : psiMethodInfos) {
            String apiPath = DataExtractUtil.analyzeApiPath(classApiPath, psiMethodInfo.getMethodApiPath());
            if (StringUtils.isEmpty(apiPath)) {
                continue;
            }
            // query参数
            if (CollectionUtils.isNotEmpty(psiMethodInfo.getQueryParams())) {
                schemaParamInfos.addAll(psiMethodInfo.getQueryParams());
            }
            // body参数
            if (CollectionUtils.isNotEmpty(psiMethodInfo.getRequestParams())) {
                schemaParamInfos.addAll(psiMethodInfo.getRequestParams());
            }
            // 响应参数
            if (Objects.nonNull(psiMethodInfo.getResponseParam())) {
                schemaParamInfos.add(psiMethodInfo.getResponseParam());
            }
            try {
                // 添加api信息
                builder.addApi(apiPath, OpenApiKit.buildPathItem(apiPath, psiMethodInfo, topicName));
            } catch (Exception e) {
                log.warn("接口生成异常，接口地址：" + apiPath, e);
            }
        }

        // 添加各种可重用组件
        Map<String, Schema> schemaMap = Maps.newHashMapWithExpectedSize(schemaParamInfos.size());
        schemaParamInfos.forEach(t -> OpenApiKit.buildSchema(t,false, schemaMap));
        builder.setSchemas(schemaMap);

        // 添加分组标记
        builder.addTag(topicName, topicDesc);
        return builder;
    }
}
