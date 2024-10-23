package com.myth.earth.restful.plugin.ui.tree.node;

import com.intellij.icons.AllIcons;
import com.myth.earth.restful.plugin.ui.tree.bean.ModuleTreeInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class ModuleTreeNode extends AbstractRestfulTreeNode<ModuleTreeInfo> {

    public ModuleTreeNode(@NotNull ModuleTreeInfo source) {
        super(source);
    }

    @Override
    public @Nullable Icon getIcon(boolean selected) {
        return AllIcons.Nodes.Module;
    }

}


