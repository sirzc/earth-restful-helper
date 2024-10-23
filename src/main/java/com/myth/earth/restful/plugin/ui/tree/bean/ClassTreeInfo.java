package com.myth.earth.restful.plugin.ui.tree.bean;

import com.intellij.icons.AllIcons;
import com.intellij.psi.PsiClass;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

@Getter
public class ClassTreeInfo {

    private final PsiClass psiClass;
    /**
     * 图标
     */
    private final Icon     icon;

    public ClassTreeInfo(@NotNull PsiClass psiClass) {
        this.psiClass = psiClass;
        this.icon = AllIcons.FileTypes.Java;
    }

    public String getQualifiedName() {
        return psiClass.getQualifiedName();
    }

    public String getName() {
        return psiClass.getName();
    }

    public String getSimpleName() {
        String[] split = getQualifiedName().split("\\.");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < split.length - 1; i++) {
            sb.append(split[i].charAt(0)).append(".");
        }
        return sb.append(split[split.length - 1]).toString();
    }

    @Override
    public String toString() {
        return getSimpleName();
    }
}
