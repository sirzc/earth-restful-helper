package com.myth.earth.restful.core.analyze.processor;

import com.intellij.openapi.project.Project;
import com.myth.earth.restful.model.PsiClassInfo;
import com.myth.earth.restful.model.PsiMethodInfo;
import com.myth.earth.restful.model.RestfulClassMethod;
import com.myth.earth.restful.utils.DataExtractUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * restful api tree 显示需要内容
 *
 * @author zhouchao
 * @date 2024-05-22 下午6:46
 */
public class TreeRestfulConvertProcessor extends AbstractConvertProcessor<List<RestfulClassMethod>> {

    public TreeRestfulConvertProcessor(@NotNull Project project) {
        super(project);
    }

    @Override
    protected boolean isQuickScan() {
        return true;
    }

    @Override
    @Nullable
    protected List<RestfulClassMethod> getTargets(@NotNull PsiClassInfo psiClassInfo) {
        List<PsiMethodInfo> psiMethodInfos = psiClassInfo.getPsiMethodInfos();
        if (CollectionUtils.isEmpty(psiMethodInfos)) {
            return null;
        }

        // 返回的API详情
        List<RestfulClassMethod> restfulClassMethods = new ArrayList<>(psiMethodInfos.size());
        for (PsiMethodInfo psiMethodInfo : psiMethodInfos) {
            String apiPath = DataExtractUtil.analyzeApiPath(psiClassInfo.getClassApiPath(), psiMethodInfo.getMethodApiPath());
            if (StringUtils.isEmpty(apiPath)) {
                continue;
            }
            RestfulClassMethod restfulClassMethod = new RestfulClassMethod();
            restfulClassMethod.setApiPath(apiPath);
            restfulClassMethod.setHttpMethod(psiMethodInfo.getHttpMethod());
            restfulClassMethod.setPsiMethod(psiMethodInfo.getPsiMethod());
            restfulClassMethod.setPsiClass(psiClassInfo.getPsiClass());
            restfulClassMethods.add(restfulClassMethod);
        }
        return restfulClassMethods;
    }
}
