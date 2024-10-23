package com.myth.earth.restful.plugin.ui.tree.node;

import com.intellij.icons.AllIcons;
import com.intellij.psi.PsiClass;
import com.myth.earth.restful.plugin.ui.tree.bean.ClassTreeInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class ClassTreeNode extends AbstractRestfulTreeNode<ClassTreeInfo> {

    public ClassTreeNode(@NotNull ClassTreeInfo source) {
        super(source);
    }

    @Override
    public @Nullable Icon getIcon(boolean selected) {
        return AllIcons.Nodes.Class;
    }

    @Override
    public @NotNull String getNodeText() {
        return getObj().getName();
    }

    public PsiClass getPsiClass() {
        return getObj().getPsiClass();
    }

    public void navigate(boolean requestFocus) {
        getPsiClass().navigate(requestFocus);
    }
}
