package com.myth.earth.restful.plugin.ui.tree.node;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;

public abstract class AbstractRestfulTreeNode<T> extends DefaultMutableTreeNode implements CustomerNodeInfo {

    @NotNull
    private T obj;

    public AbstractRestfulTreeNode(@NotNull T obj) {
        super(obj);
        this.obj = obj;
    }

    @Nullable
    public abstract Icon getIcon(boolean selected);

    public @NotNull T getObj() {
        return obj;
    }

    public void setObj(@NotNull T obj) {
        this.obj = obj;
    }

    @Override
    public void add(@NotNull MutableTreeNode newChild) {
        if (!(newChild instanceof AbstractRestfulTreeNode<?>)) {
            return;
        }
        super.add(newChild);
    }

    @Override
    public @NotNull String getNodeText() {
        return String.valueOf(getObj());
    }
}
