package com.myth.earth.restful.plugin.ui.table;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.ui.AnActionButton;
import com.intellij.ui.BooleanTableCellRenderer;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.table.JBTable;
import com.myth.earth.restful.plugin.ui.table.annotations.JTableField;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.table.TableColumn;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

/**
 * 抽象表格视图
 *
 * @author zhouchao
 * @date 2024-03-30 09:56Ò
 */
public class TablePanelAdapter<T> extends JBTable {
    private final Class<T>             clz;
    private final TableModelAdapter<T> dataModel;

    public TablePanelAdapter(@NotNull Class<T> clz) {
        this.clz = clz;
        this.dataModel = new TableModelAdapter<>(clz);
        this.setModel(dataModel);
        // 设置单元格样式
        this.initTableStyle(clz);
        // 设置表格单选
        // this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.setCellSelectionEnabled(true);
        this.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
    }

    private void initTableStyle(@NotNull Class<T> clz) {
        Field[] fields = clz.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(JTableField.class)) {
                JTableField tableField = field.getAnnotation(JTableField.class);
                int width = tableField.width();
                if (width == 0) {
                    continue;
                }
                int min = tableField.min() == 0 ? width : tableField.min();
                int max = tableField.max() == 0 ? width : tableField.max();
                TableColumn tableColumn = this.getColumnModel().getColumn(tableField.order());
                tableColumn.setPreferredWidth(width);
                tableColumn.setMinWidth(min);
                tableColumn.setMaxWidth(max);
                // 设置编辑样式和编辑器
                if (tableField.isSelect()) {
                    tableColumn.setCellEditor(JBTable.createBooleanEditor());
                    tableColumn.setCellRenderer(new BooleanTableCellRenderer());
                } else {
                    tableColumn.setCellEditor(new DefaultCellEditor(new JTextField()));
                }
            }
        }
    }

    /**
     * 添加一行数据
     */
    public void add(T t) {
        // 存储一条数据
        this.dataModel.addData(t);
        this.dataModel.fireTableDataChanged();
        int lastRow = dataModel.getRowCount();
        this.setRowSelectionInterval(lastRow - 1, lastRow - 1);
    }

    /**
     * 更新一条数据
     *
     * @param selectedRow 选择行
     * @param target      目标内容
     */
    public void update(int selectedRow, T target) {
        this.dataModel.updateDate(selectedRow, target);
        this.dataModel.fireTableDataChanged();
        this.setRowSelectionInterval(selectedRow, selectedRow);
    }

    /**
     * 删除选中数据
     */
    public void remove() {
        int selectedRow = getSelectedRow();
        // 删除选中行，并刷新表格
        this.dataModel.removeData(selectedRow);
        this.dataModel.fireTableDataChanged();
        // 获取当前行数，如果最小位置大于等于行数，则选中最后一行，否则选中最小位置
        int rowCount = this.getRowCount();
        if (selectedRow < rowCount) {
            this.setRowSelectionInterval(selectedRow, selectedRow);
        } else if (rowCount > 0) {
            this.setRowSelectionInterval(rowCount - 1, rowCount - 1);
        }
    }

    /**
     * 上移动
     */
    public void moveUp() {
        int selectedRow = getSelectedRow();
        int index = selectedRow - 1;
        if (selectedRow != -1) {
            Collections.swap(this.dataModel.getDataList(), selectedRow, index);
        }
        setRowSelectionInterval(index, index);
    }

    /**
     * 下移
     */
    public void moveDown() {
        int selectedRow = getSelectedRow();
        int index = selectedRow + 1;
        if (selectedRow != -1) {
            Collections.swap(this.dataModel.getDataList(), selectedRow, index);
        }
        setRowSelectionInterval(index, index);
    }

    /**
     * 重置
     */
    public void reset() {
        this.dataModel.clear();
        this.dataModel.fireTableDataChanged();
    }

    /**
     * 获取所有数据
     *
     * @return 所有数据
     */
    @NotNull
    public List<T> getTableData() {
        return this.dataModel.getDataList();
    }

    public JPanel createTableAndToolbar() {
        ToolbarDecorator toolbarDecorator = ToolbarDecorator.createDecorator(this);
        toolbarDecorator.setAddAction(anActionButton -> {
            try {
                T t = clz.getDeclaredConstructor().newInstance();
                this.add(t);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        toolbarDecorator.setRemoveAction(anActionButton -> this.remove());
        toolbarDecorator.setMoveUpAction(anActionButton -> this.moveUp());
        toolbarDecorator.setMoveDownAction(anActionButton -> this.moveDown());
        toolbarDecorator.addExtraAction(new AnActionButton("重置面板内容", AllIcons.Actions.Rollback) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
                reset();
            }
        });
        JPanel panel = toolbarDecorator.createPanel();
        panel.setBorder(BorderFactory.createEmptyBorder());
        return panel;
    }
}
