package com.myth.earth.restful.plugin.ui.tree.bean;

import com.intellij.icons.AllIcons;
import lombok.Getter;

import javax.swing.*;

@Getter
public class ModuleTreeInfo {

    /**
     * 模块名称
     */
    private final String moduleName;

    /**
     * 图标
     */
    private final Icon icon;

    public ModuleTreeInfo(String moduleName) {
        this(moduleName, AllIcons.Modules.SourceRoot);
    }

    public ModuleTreeInfo(String moduleName, Icon icon) {
        this.moduleName = moduleName;
        this.icon = icon;
    }

    @Override
    public String toString() {
        return moduleName;
    }
}
