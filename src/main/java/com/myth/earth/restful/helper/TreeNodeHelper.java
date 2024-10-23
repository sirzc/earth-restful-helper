package com.myth.earth.restful.helper;

import com.google.common.collect.Lists;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ContentEntry;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.SourceFolder;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.myth.earth.restful.core.analyze.processor.TreeRestfulConvertProcessor;
import com.myth.earth.restful.kits.PsiKit;
import com.myth.earth.restful.model.RestfulClassMethod;
import com.myth.earth.restful.plugin.ServiceManager;
import com.myth.earth.restful.plugin.ui.tree.bean.ClassTreeInfo;
import com.myth.earth.restful.plugin.ui.tree.bean.ModuleTreeInfo;
import com.myth.earth.restful.plugin.ui.tree.node.ClassTreeNode;
import com.myth.earth.restful.plugin.ui.tree.node.MethodTreeNote;
import com.myth.earth.restful.plugin.ui.tree.node.ModuleTreeNode;
import com.myth.earth.restful.plugin.ui.tree.node.PackageTreeNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * 树节点计算
 *
 * @author zhouchao
 * @date 2024-06-12 下午11:03
 */
public class TreeNodeHelper {

    @NotNull
    public static List<ModuleTreeNode> getModuleTreeNodes(@NotNull Project project) {
        List<ModuleTreeNode> moduleTreeNodes = new ArrayList<>();
        @NotNull Module[] modules = ModuleManager.getInstance(project).getModules();
        for (Module module : modules) {
            // 扫描资源文件，资源文件存在则加入此节点
            ModuleTreeNode moduleTreeNode = scanModuleTreeNode(project, module);
            if (moduleTreeNode.getChildCount() > 0) {
                moduleTreeNodes.add(moduleTreeNode);
            }
        }
        return moduleTreeNodes;
    }

    @NotNull
    private static ModuleTreeNode scanModuleTreeNode(@NotNull Project project, @NotNull Module module) {
        ModuleTreeNode moduleTreeNode = new ModuleTreeNode(new ModuleTreeInfo(module.getName()));
        for (ContentEntry contentEntry : ModuleRootManager.getInstance(module).getContentEntries()) {
            // 资源文件夹
            for (SourceFolder sourceFolder : contentEntry.getSourceFolders()) {
                VirtualFile sourceRoot = sourceFolder.getFile();
                if (sourceRoot == null) {
                    continue;
                }
                List<VirtualFile> childrenList = getChildrenList(sourceRoot);
                // 源文件路径
                for (VirtualFile child : childrenList) {
                    // 存在包路径
                    if (child.isDirectory()) {
                        String packageName = child.getPath().substring(sourceRoot.getPath().length() + 1).replace('/', '.');
                        PackageTreeNode packageTreeNode = scanPackageTreeNode(project, child, packageName);
                        if (packageTreeNode != null && packageTreeNode.getChildCount() > 0) {
                            moduleTreeNode.add(packageTreeNode);
                        }
                    } else {
                        // 存在类路径（无包是一个类）
                        ClassTreeNode classTreeNode = scanClassTreeNode(project, child);
                        if (classTreeNode != null) {
                            // 直接加在模块目录下（没有包的情况）
                            moduleTreeNode.add(classTreeNode);
                        }
                    }
                }
            }
        }
        return moduleTreeNode;
    }

    @Nullable
    private static PackageTreeNode scanPackageTreeNode(@NotNull Project project, @NotNull VirtualFile parentPackageFile, @NotNull String parentPackageName) {
        // 空包
        List<VirtualFile> children = getChildrenList(parentPackageFile);
        if (children.isEmpty()) {
            return null;
        }

        PackageTreeNode packageTreeNode = new PackageTreeNode(parentPackageName);
        for (VirtualFile child : children) {
            if (child.isDirectory()) {
                // 当前的包名
                String packageName = child.getPath().substring(parentPackageFile.getPath().length() + 1).replace('/', '.');
                // 对于连续的包，正常拼接，不做其他操作
                if (children.size() == 1) {
                    return scanPackageTreeNode(project, child, parentPackageName + "." + packageName);
                }
                // 多个内容就需要展开显示
                Optional.ofNullable(scanPackageTreeNode(project, child, packageName)).ifPresent(packageTreeNode::add);
            } else {
                Optional.ofNullable(scanClassTreeNode(project, child)).ifPresent(packageTreeNode::add);
            }
        }

        if (packageTreeNode.getChildCount() == 0) {
            return null;
        }

        return packageTreeNode;
    }

    @Nullable
    private static ClassTreeNode scanClassTreeNode(@NotNull Project project, @NotNull VirtualFile virtualFile) {
        // 非java文件
        if (!virtualFile.getName().endsWith(".java")) {
            return null;
        }

        // 获取java类
        PsiClass psiClass = PsiKit.getPsiClassFromVirtualFile(project, virtualFile);
        if (psiClass == null) {
            return null;
        }

        TreeRestfulConvertProcessor convertProcessor = ServiceManager.getProjectInstance(project, TreeRestfulConvertProcessor.class);
        // 校验是否支持类
        if (!convertProcessor.supportClass(psiClass)) {
            return null;
        }

        // 分析接口信息
        List<RestfulClassMethod> analyzeResult = convertProcessor.getAnalyzeResult(psiClass);
        if (analyzeResult == null || analyzeResult.isEmpty()) {
            return null;
        }

        ClassTreeNode classTreeNode = new ClassTreeNode(new ClassTreeInfo(psiClass));
        for (RestfulClassMethod restfulClassMethod : analyzeResult) {
            classTreeNode.add(new MethodTreeNote(restfulClassMethod));
        }
        return classTreeNode;
    }

    @NotNull
    private static List<VirtualFile> getChildrenList(@NotNull VirtualFile virtualFile) {
        VirtualFile[] children = virtualFile.getChildren();
        return children == null ? Collections.emptyList() : Lists.newArrayList(children);
    }
}
