package com.myth.earth.restful.plugin.ui.tree;

import com.intellij.ide.TreeExpander;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.ui.JBUI;
import com.myth.earth.restful.plugin.ui.tree.node.AbstractRestfulTreeNode;
import com.myth.earth.restful.plugin.ui.tree.render.RestfulTreeCellRenderer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Enumeration;
import java.util.function.Consumer;

public abstract class AbstractRestfulTreePanel extends JBScrollPane implements TreeExpander {

    private final JTree tree;

    public AbstractRestfulTreePanel(@NotNull final JTree tree) {
        this.tree = tree;
        this.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        this.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        this.setBorder(new EmptyBorder(JBUI.emptyInsets()));
        this.tree.setCellRenderer(new RestfulTreeCellRenderer());
        this.tree.setRootVisible(true);
        this.tree.setShowsRootHandles(false);
        this.setViewportView(tree);

        this.tree.addTreeSelectionListener(e -> {
            if (!this.tree.isEnabled()) {
                return;
            }
            Object component = tree.getLastSelectedPathComponent();
            if (!(component instanceof AbstractRestfulTreeNode<?>)) {
                return;
            }
            AbstractRestfulTreeNode<?> node = (AbstractRestfulTreeNode<?>) component;
            if (getChooseListener() != null) {
                getChooseListener().accept(node);
            }
        });
        this.tree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                if (!tree.isEnabled()) {
                    return;
                }
                AbstractRestfulTreeNode<?> node = getNode(event);
                if (node == null) {
                    return;
                }
                if (SwingUtilities.isLeftMouseButton(event)) {
                    if (event.getClickCount() == 2 && getDoubleClickListener() != null) {
                        getDoubleClickListener().accept(node);
                    }
                } else if (SwingUtilities.isRightMouseButton(event)) {
                    showPopupMenu(event.getX(), event.getY(), getPopupMenu(event, node));
                }
            }

            @Nullable
            private AbstractRestfulTreeNode<?> getNode(@NotNull MouseEvent event) {
                TreePath path = tree.getPathForLocation(event.getX(), event.getY());
                tree.setSelectionPath(path);
                return getChooseNode(path);
            }
        });
    }

    protected final JTree getTree() {
        return this.tree;
    }

    protected final DefaultTreeModel getTreeModel() {
        return (DefaultTreeModel) this.tree.getModel();
    }

    protected final void render(@NotNull AbstractRestfulTreeNode<?> rootNode) {
        getTreeModel().setRoot(rootNode);
    }

    /**
     * 获取选中节点
     *
     * @param treePath 树路径
     * @return 获取到的节点信息
     */
    @Nullable
    public AbstractRestfulTreeNode<?> getChooseNode(@Nullable TreePath treePath) {
        Object component = null;
        if (treePath != null) {
            component = treePath.getLastPathComponent();
        } else {
            component = tree.getLastSelectedPathComponent();
        }
        if (!(component instanceof AbstractRestfulTreeNode<?>)) {
            return null;
        }
        return (AbstractRestfulTreeNode<?>) component;
    }

    /**
     * 展开tree视图
     *
     */
    public void expandAll() {
        TreePath selectionPath = tree.getSelectionPath();
        if (selectionPath == null) {
            selectionPath = new TreePath(tree.getModel().getRoot());
        }
        extracted(selectionPath, true);
    }

    /**
     * 折叠 tree视图
     */
    public void collapseAll() {
        TreePath selectionPath = tree.getSelectionPath();
        if (selectionPath == null) {
            selectionPath = new TreePath(tree.getModel().getRoot());
        }
        extracted(selectionPath, false);
    }

    /**
     * 展开或折叠
     *
     * @param treePath 路径
     * @param expand true：展开，false：关闭
     */
    private void extracted(@NotNull TreePath treePath, boolean expand) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) treePath.getLastPathComponent();
        if (node.getChildCount() >= 0) {
            for (Enumeration<?> e = node.children(); e.hasMoreElements(); ) {
                TreeNode n = (TreeNode) e.nextElement();
                TreePath path = treePath.pathByAddingChild(n);
                extracted(path, expand);
            }
        }

        // 展开或收起必须自下而上进行
        if (expand) {
            tree.expandPath(treePath);
        } else {
            if (node.isRoot()) {
                return;
            }
            tree.collapsePath(treePath);
        }
    }

    /**
     * 显示右键菜单
     */
    protected void showPopupMenu(int x, int y, @Nullable JPopupMenu menu) {
        if (menu == null) {
            return;
        }
        TreePath path = tree.getPathForLocation(x, y);
        tree.setSelectionPath(path);
        Rectangle rectangle = tree.getUI().getPathBounds(tree, path);
        if (rectangle != null && rectangle.contains(x, y)) {
            menu.show(tree, x, rectangle.y + rectangle.height);
        }
    }

    @Override
    public boolean canExpand() {
        return tree.getRowCount() > 0;
    }

    @Override
    public boolean canCollapse() {
        return tree.getRowCount() > 0;
    }

    /**
     * 选中定位并聚焦到当前节点
     *
     * @param treeNode 节点
     */
    protected void gotoTreeNote(@NotNull DefaultMutableTreeNode treeNode) {
        // 有节点到根路径数组
        TreeNode[] nodes = getTreeModel().getPathToRoot(treeNode);
        TreePath path = new TreePath(nodes);
        // 选中
        tree.setSelectionPath(path);
        // 定位
        tree.scrollPathToVisible(path);
        // 聚焦
        tree.requestFocusInWindow();
    }

    @Nullable
    protected abstract JPopupMenu getPopupMenu(@NotNull MouseEvent event, @NotNull AbstractRestfulTreeNode<?> node);

    @Nullable
    protected abstract Consumer<AbstractRestfulTreeNode<?>> getChooseListener();

    @Nullable
    protected abstract Consumer<AbstractRestfulTreeNode<?>> getDoubleClickListener();
}

