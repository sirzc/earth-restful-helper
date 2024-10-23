package com.myth.earth.restful.core.analyze.processor;

import com.intellij.openapi.project.Project;
import com.myth.earth.restful.model.ApiDetail;
import com.myth.earth.restful.model.ParamInfo;
import com.myth.earth.restful.model.PsiClassInfo;
import com.myth.earth.restful.model.PsiMethodInfo;
import com.myth.earth.restful.utils.DataExtractUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

/**
 * api detail 转换处理
 *
 * @author zhouchao
 * @date 2023-04-25 13:54
 */
public class ApiDetailConvertProcessor extends AbstractConvertProcessor<List<ApiDetail>> {

    public ApiDetailConvertProcessor(Project project) {
        super(project);
    }

    @Override
    protected List<ApiDetail> getTargets(@NotNull PsiClassInfo psiClassInfo) {
        List<PsiMethodInfo> psiMethodInfos = psiClassInfo.getPsiMethodInfos();
        if (CollectionUtils.isEmpty(psiMethodInfos)) {
            return new ArrayList<>();
        }
        // 返回的API详情
        List<ApiDetail> apiDetails = new ArrayList<>(psiMethodInfos.size());
        for (PsiMethodInfo psiMethodInfo : psiMethodInfos) {
            String apiPath = DataExtractUtil.analyzeApiPath(psiClassInfo.getClassApiPath(), psiMethodInfo.getMethodApiPath());
            if (StringUtils.isEmpty(apiPath)) {
                continue;
            }
            ApiDetail apiDetail = new ApiDetail();
            apiDetail.setApiPath(apiPath);
            apiDetail.setApiModelName(psiClassInfo.getClassDescribe());
            apiDetail.setApiName(psiMethodInfo.getMethodDescribe());
            apiDetail.setHttpMethod(psiMethodInfo.getHttpMethod());
            // 接口参数的处置方式
            apiDetail.setApiExamplePath(apiPath + getQueryPath(psiMethodInfo.getQueryParams()));
            apiDetail.setPathParams(psiMethodInfo.getPathParams());
            apiDetail.setQueryParams(psiMethodInfo.getQueryParams());
            apiDetail.setRequestType(psiMethodInfo.getRequestType());
            apiDetail.setRequestParams(psiMethodInfo.getRequestParams());
            apiDetail.setResponseParam(psiMethodInfo.getResponseParam());
            apiDetails.add(apiDetail);
        }
        return apiDetails;
    }

    /**
     * 获取query参数路径
     *
     * @param queryParams query参数列表
     * @return xxx=111&yyy=222
     */
    private String getQueryPath(@Nullable List<ParamInfo> queryParams) {
        if (CollectionUtils.isEmpty(queryParams)) {
            return "";
        }
        StringJoiner joiner = new StringJoiner("&", "?", "");
        for (ParamInfo queryParam : queryParams) {
            joiner.add(queryParam.getName() + "=" + DataExtractUtil.getParamValue(queryParam));
        }
        return joiner.toString();
    }
}
