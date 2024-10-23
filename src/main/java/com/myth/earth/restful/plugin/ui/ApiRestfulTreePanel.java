package com.myth.earth.restful.plugin.ui;

import com.google.common.collect.Maps;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ui.configuration.ProjectSettingsService;
import com.intellij.openapi.ui.JBMenuItem;
import com.intellij.openapi.ui.JBPopupMenu;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.ui.treeStructure.SimpleTree;
import com.myth.earth.restful.model.RestfulClassMethod;
import com.myth.earth.restful.plugin.ui.tree.AbstractRestfulTreePanel;
import com.myth.earth.restful.plugin.ui.tree.bean.ClassTreeInfo;
import com.myth.earth.restful.plugin.ui.tree.bean.ModuleTreeInfo;
import com.myth.earth.restful.plugin.ui.tree.node.*;
import com.myth.earth.restful.plugin.ui.tree.topic.RefreshActionNotifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ApiRestfulTreePanel extends AbstractRestfulTreePanel {

    private final transient Project                        project;
    private final transient Map<PsiClass, ClassTreeNode>   classTreeNotes;
    private final transient Map<PsiMethod, MethodTreeNote> methodTreeNotes;
    private                 Consumer<RestfulClassMethod>   chooseCallback;

    public ApiRestfulTreePanel(@NotNull Project project, @Nullable Consumer<RestfulClassMethod> chooseCallback) {
        super(new SimpleTree());
        this.project = project;
        this.classTreeNotes = new HashMap<>();
        this.methodTreeNotes = new HashMap<>();
        this.chooseCallback = chooseCallback;
        // 注册监听
        initListeners();
    }

    private void initListeners() {
        // 按回车键跳转到对应方法
        getTree().addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    AbstractRestfulTreeNode<?> node = getChooseNode(null);
                    if (node instanceof ClassTreeNode) {
                        ((ClassTreeNode) node).navigate(true);
                    }
                    if (node instanceof MethodTreeNote) {
                        ((MethodTreeNote) node).navigate(true);
                    }
                }
            }
        });
    }

    @Nullable
    @Override
    protected JPopupMenu getPopupMenu(@NotNull MouseEvent event, @NotNull AbstractRestfulTreeNode<?> node) {
        List<JMenuItem> items = new ArrayList<>();
        if (node instanceof ClassTreeNode) {
            // navigation
            JMenuItem navigation = new JBMenuItem("Goto class", AllIcons.Nodes.Class);
            navigation.addActionListener(actionEvent -> {
                ClassTreeInfo classTreeInfo = ((ClassTreeNode) node).getObj();
                classTreeInfo.getPsiClass().navigate(true);
            });
            items.add(navigation);
        } else if (node instanceof MethodTreeNote) {
            // navigation
            JMenuItem navigation = new JBMenuItem("Goto method", AllIcons.Nodes.Method);
            navigation.addActionListener(actionEvent -> {
                RestfulClassMethod restfulClassMethod = ((MethodTreeNote) node).getObj();
                restfulClassMethod.getPsiMethod().navigate(true);
            });
            items.add(navigation);

            // todo: Copy full url
            JMenuItem copyFullUrl = new JBMenuItem("Copy curl", AllIcons.Actions.Copy);
            copyFullUrl.addActionListener(actionEvent -> {

            });
            items.add(copyFullUrl);

            // todo: Copy api path
            JMenuItem copyApiPath = new JBMenuItem("Copy url", AllIcons.Actions.Copy);
            copyApiPath.addActionListener(actionEvent -> {

            });
            items.add(copyApiPath);
        } else if (node instanceof ModuleTreeNode) {
            ModuleTreeInfo moduleTreeInfo = ((ModuleTreeNode) node).getObj();
            String moduleName = moduleTreeInfo.getModuleName();

            JBMenuItem moduleSetting = new JBMenuItem("Module setting", AllIcons.General.Settings);
            moduleSetting.addActionListener(action -> {
                Module module = ModuleManager.getInstance(project).findModuleByName(moduleName);
                if (module == null) {
                    return;
                }
                // 打开当前项目模块设置
                ProjectSettingsService.getInstance(project).openModuleSettings(module);
            });
            items.add(moduleSetting);

            // JBMenuItem moduleHeaders = new JBMenuItem("Module Headers");
            // moduleHeaders.addActionListener(action -> {
            //     showPopupMenu(event.getX(), event.getY(),  new ModuleHeadersPopup(project, moduleName));
            // });
            // items.add(moduleHeaders);
        }
        if (items.isEmpty()) {
            return null;
        }
        JBPopupMenu menu = new JBPopupMenu();
        items.forEach(menu::add);
        return menu;
    }

    @Override
    protected @Nullable Consumer<AbstractRestfulTreeNode<?>> getChooseListener() {
        return node -> {
            if (!(node instanceof MethodTreeNote) || chooseCallback == null) {
                return;
            }
            MethodTreeNote methodTreeNote = (MethodTreeNote) node;
            chooseCallback.accept(methodTreeNote.getObj());
        };
    }

    @Override
    protected @Nullable Consumer<AbstractRestfulTreeNode<?>> getDoubleClickListener() {
        return node -> {
            if (node instanceof MethodTreeNote) {
                ((MethodTreeNote) node).navigate(true);
            }
        };
    }

    /**
     * 渲染树信息
     *
     * @param moduleTreeNodes 模块节点信息
     * @param expand          是否展开
     */
    public void renderAll(@NotNull List<ModuleTreeNode> moduleTreeNodes, boolean expand) {
        this.classTreeNotes.clear();
        this.methodTreeNotes.clear();
        // 使用Java 8 Stream API进行优化处理
        this.methodTreeNotes.putAll(moduleTreeNodes.stream().flatMap(
                                                           node -> countNodes(node, MethodTreeNote.class).stream().map(note -> Maps.immutableEntry(note.getPsiMethod(), note)))
                                                   .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));

        this.classTreeNotes.putAll(moduleTreeNodes.stream().flatMap(
                                                          node -> countNodes(node, ClassTreeNode.class).stream().map(note -> Maps.immutableEntry(note.getPsiClass(), note)))
                                                  .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));

        RootTreeNode rootNode = new RootTreeNode(this.methodTreeNotes.size());
        moduleTreeNodes.forEach(rootNode::add);
        super.render(rootNode);
        if (expand) {
            expandAll();
        } else {
            collapseAll();
        }
    }

    /**
     * 转到方法对应的api
     *
     * @param psiMethod 方法信息
     */
    public void gotoApiMethod(@NotNull PsiMethod psiMethod) {
        if (methodTreeNotes == null || methodTreeNotes.isEmpty()) {
            project.getMessageBus().syncPublisher(RefreshActionNotifier.REFRESH_ACTION_TOPIC).refresh();
            return;
        }

        // 获取方法对应的树节点
        MethodTreeNote methodTreeNote = methodTreeNotes.get(psiMethod);
        if (methodTreeNote == null) {
            return;
        }
        // 聚焦到节点
        gotoTreeNote(methodTreeNote);
    }

    /**
     * 转到方法对应的api
     *
     * @param psiClass api类信息
     */
    public void gotoApiClass(@NotNull PsiClass psiClass) {
        if (methodTreeNotes == null || methodTreeNotes.isEmpty()) {
            project.getMessageBus().syncPublisher(RefreshActionNotifier.REFRESH_ACTION_TOPIC).refresh();
            return;
        }

        // 获取方法对应的树节点
        ClassTreeNode classTreeNode = classTreeNotes.get(psiClass);
        if (classTreeNode == null) {
            return;
        }
        // 聚焦到节点
        gotoTreeNote(classTreeNode);
    }

    @NotNull
    private static <T> List<T> countNodes(@NotNull DefaultMutableTreeNode node, Class<T> clazz) {
        List<T> nodes = new ArrayList<>();
        if (clazz.isInstance(node)) {
            nodes.add(clazz.cast(node));
        }

        for (int i = 0; i < node.getChildCount(); i++) {
            nodes.addAll(countNodes((DefaultMutableTreeNode) node.getChildAt(i), clazz));
        }
        return nodes;
    }
}
