package com.myth.earth.restful.plugin.ui.tree.node;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class RootTreeNode extends AbstractRestfulTreeNode<Integer> {

    public RootTreeNode(@NotNull Integer source) {
        super(source);
    }

    @Override
    public @Nullable Icon getIcon(boolean selected) {
        return null;
    }

    @Override
    public @NotNull String getNodeText() {
        return "api find :" + getObj();
    }

}