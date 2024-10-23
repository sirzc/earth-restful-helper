package com.myth.earth.restful.plugin.ui.tree.node;

import com.intellij.icons.AllIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class PackageTreeNode extends AbstractRestfulTreeNode<String> {

    public PackageTreeNode(@NotNull String source) {
        super(source);
    }

    @Override
    public @Nullable Icon getIcon(boolean selected) {
        return AllIcons.Nodes.Package;
    }
}