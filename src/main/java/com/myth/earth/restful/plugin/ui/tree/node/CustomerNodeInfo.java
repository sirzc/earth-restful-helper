package com.myth.earth.restful.plugin.ui.tree.node;

import com.intellij.ui.SimpleTextAttributes;
import org.jetbrains.annotations.NotNull;

public interface CustomerNodeInfo {

    /**
     * 获取节点文本内容
     *
     * @return str
     */
    @NotNull
    String getNodeText();

    @NotNull
    default SimpleTextAttributes getTextAttributes() {
        return SimpleTextAttributes.REGULAR_ATTRIBUTES;
    }
}
