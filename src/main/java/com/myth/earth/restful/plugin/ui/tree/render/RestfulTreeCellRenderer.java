package com.myth.earth.restful.plugin.ui.tree.render;

import com.intellij.ui.ColoredTreeCellRenderer;
import com.myth.earth.restful.plugin.ui.tree.node.AbstractRestfulTreeNode;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class RestfulTreeCellRenderer extends ColoredTreeCellRenderer {

    @Override
    public void customizeCellRenderer(@NotNull JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        AbstractRestfulTreeNode<?> node = null;
        if (value instanceof AbstractRestfulTreeNode) {
            node = (AbstractRestfulTreeNode<?>) value;
        }
        if (node == null) {
            return;
        }
        setIcon(node.getIcon(selected));
        append(node.getNodeText(), node.getTextAttributes());
    }
}
