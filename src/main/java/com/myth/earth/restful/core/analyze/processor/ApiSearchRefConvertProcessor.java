package com.myth.earth.restful.core.analyze.processor;

import com.google.common.collect.Lists;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ContentEntry;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.SourceFolder;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.myth.earth.restful.kits.PsiKit;
import com.myth.earth.restful.model.ApiSearchInfoRef;
import com.myth.earth.restful.model.PsiClassInfo;
import com.myth.earth.restful.model.PsiMethodInfo;
import com.myth.earth.restful.utils.DataExtractUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ApiSearchRefConvertProcessor extends AbstractConvertProcessor<List<ApiSearchInfoRef>> {

    private static final Logger log = Logger.getInstance(ApiSearchRefConvertProcessor.class);

    public ApiSearchRefConvertProcessor(@NotNull Project project) {
        super(project);
    }

    public static ApiSearchRefConvertProcessor getInstance(@NotNull Project project) {
        return project.getService(ApiSearchRefConvertProcessor.class);
    }

    @Override
    protected boolean isQuickScan() {
        return true;
    }

    @Nullable
    @Override
    protected List<ApiSearchInfoRef> getTargets(@NotNull PsiClassInfo psiClassInfo) {
        List<PsiMethodInfo> psiMethodInfos = psiClassInfo.getPsiMethodInfos();
        if (CollectionUtils.isEmpty(psiMethodInfos)) {
            return new ArrayList<>();
        }
        // 返回的API详情
        String className = psiClassInfo.getPsiClass().getName();
        List<ApiSearchInfoRef> apiSearchInfoRefs = new ArrayList<>(psiMethodInfos.size());
        for (PsiMethodInfo psiMethodInfo : psiMethodInfos) {
            String apiPath = DataExtractUtil.analyzeApiPath(psiClassInfo.getClassApiPath(), psiMethodInfo.getMethodApiPath());
            if (StringUtils.isEmpty(apiPath)) {
                continue;
            }
            String methodName = psiMethodInfo.getPsiMethod().getName();
            ApiSearchInfoRef apiSearchInfoRef = new ApiSearchInfoRef();
            apiSearchInfoRef.setApiPath(apiPath);
            apiSearchInfoRef.setApiName(psiMethodInfo.getMethodDescribe());
            apiSearchInfoRef.setDescription(psiMethodInfo.getMethodDescribe());
            apiSearchInfoRef.setClassMethodPath(className + "#" + methodName);
            apiSearchInfoRef.setPsiMethod(psiMethodInfo.getPsiMethod());
            apiSearchInfoRef.setHttpMethod(psiMethodInfo.getHttpMethod());
            apiSearchInfoRefs.add(apiSearchInfoRef);
        }
        return apiSearchInfoRefs;
    }

    @NotNull
    public List<ApiSearchInfoRef> buildApiSearchInfoRefs() {
        List<ApiSearchInfoRef> result = new ArrayList<>();
        @NotNull Module[] modules = ModuleManager.getInstance(getProject()).getModules();
        for (Module module : modules) {
            // 扫描资源文件，资源文件存在则加入此节点
            for (ContentEntry contentEntry : ModuleRootManager.getInstance(module).getContentEntries()) {
                // 资源目录
                for (SourceFolder sourceFolder : contentEntry.getSourceFolders()) {
                    VirtualFile sourceRoot = sourceFolder.getFile();
                    if (sourceRoot == null) {
                        continue;
                    }
                    List<VirtualFile> childrenList = getChildrenList(sourceRoot);
                    for (VirtualFile virtualFile : childrenList) {
                        List<ApiSearchInfoRef> restfulClassMethods = scanVirtualFile(getProject(), virtualFile);
                        if (restfulClassMethods != null && !restfulClassMethods.isEmpty()) {
                            result.addAll(restfulClassMethods);
                        }
                    }
                }
            }
        }
        return result;
    }

    private List<ApiSearchInfoRef> scanVirtualFile(@NotNull Project project, @NotNull VirtualFile virtualFile) {
        if (virtualFile.isDirectory()) {
            List<VirtualFile> childrenList = getChildrenList(virtualFile);
            if (childrenList.isEmpty()) {
                return null;
            }
            List<ApiSearchInfoRef> restfulClassMethods = new ArrayList<>();
            for (VirtualFile file : childrenList) {
                List<ApiSearchInfoRef> child = scanVirtualFile(project, file);
                if (child != null && !child.isEmpty()) {
                    restfulClassMethods.addAll(child);
                }
            }
            return restfulClassMethods;
        }

        if (!virtualFile.getName().endsWith(".java")) {
            return null;
        }

        try {
            // 获取java类
            PsiClass psiClass = PsiKit.getPsiClassFromVirtualFile(project, virtualFile);
            if (psiClass == null) {
                return null;
            }

            // 校验是否支持类
            if (!supportClass(psiClass)) {
                return null;
            }

            return getAnalyzeResult(psiClass);
        } catch (Exception e) {
            log.warn("api search class error：" + virtualFile.getPath(), e);
            return null;
        }
    }

    @NotNull
    private static List<VirtualFile> getChildrenList(@NotNull VirtualFile virtualFile) {
        VirtualFile[] children = virtualFile.getChildren();
        return children == null ? Collections.emptyList() : Lists.newArrayList(children);
    }
}
