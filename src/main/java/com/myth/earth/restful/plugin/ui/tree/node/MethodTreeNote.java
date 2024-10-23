package com.myth.earth.restful.plugin.ui.tree.node;

import com.intellij.psi.PsiMethod;
import com.myth.earth.restful.enums.HttpMethod;
import com.myth.earth.restful.model.RestfulClassMethod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * 方法节点
 *
 * @author zhouchao
 * @date 2024-05-23 下午9:29
 */
public class MethodTreeNote extends AbstractRestfulTreeNode<RestfulClassMethod> {

    private final HttpMethod httpMethod;

    public MethodTreeNote(@NotNull RestfulClassMethod obj) {
        super(obj);
        httpMethod = obj.getHttpMethod();
    }

    @Override
    public @Nullable Icon getIcon(boolean selected) {
        return httpMethod.getIcon();
    }

    @Override
    public @NotNull String getNodeText() {
        return getObj().getApiPath();
    }

    public void navigate(boolean requestFocus) {
        getPsiMethod().navigate(requestFocus);
    }

    @NotNull
    public PsiMethod getPsiMethod(){
        return getObj().getPsiMethod();
    }
}
