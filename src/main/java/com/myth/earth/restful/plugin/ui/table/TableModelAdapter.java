package com.myth.earth.restful.plugin.ui.table;

import com.myth.earth.restful.plugin.ui.table.annotations.JTableField;

import javax.swing.table.AbstractTableModel;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 抽象数据模型
 *
 * @author zhouchao
 * @date 2024-03-30 09:17
 */
public class TableModelAdapter<T> extends AbstractTableModel {

    private final Class<T>     clz;
    /**
     * 数据集合
     */
    private final List<T>      dataList;
    /**
     * 字段列表
     */
    private final List<String> columnNames;

    public TableModelAdapter(Class<T> clz) {
        this.clz = clz;
        this.dataList = new ArrayList<>();
        this.columnNames = new ArrayList<>(clz.getDeclaredFields().length);
        // 获取所有字段
        for (Field f : clz.getDeclaredFields()) {
            // 判断这个字段是否有MyField注解
            if (f.isAnnotationPresent(JTableField.class)) {
                JTableField annotation = f.getAnnotation(JTableField.class);
                columnNames.add(annotation.title());
            }
        }
    }

    public void addData(T t) {
        dataList.add(t);
    }

    public void removeData(int index) {
        dataList.remove(index);
    }

    public List<T> getDataList() {
        return dataList;
    }

    public void clear() {
        dataList.clear();
    }

    public void updateDate(int index, T target) {
        dataList.set(index, target);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        T t = dataList.get(rowIndex);
        for (Field field : clz.getDeclaredFields()) {
            if (!field.isAnnotationPresent(JTableField.class)) {
                continue;
            }

            // 获取字段的属性值
            JTableField tableField = field.getAnnotation(JTableField.class);
            if (tableField.order() == columnIndex) {
                try {
                    PropertyDescriptor propertyDescriptor = new PropertyDescriptor(field.getName(), clz);
                    Method readMethod = propertyDescriptor.getReadMethod();
                    return readMethod.invoke(t);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

            }
        }
        throw new IllegalStateException("Unexpected value: " + columnIndex);
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        T t = dataList.get(rowIndex);
        for (Field field : clz.getDeclaredFields()) {
            if (!field.isAnnotationPresent(JTableField.class)) {
                continue;
            }

            // 获取字段的属性值
            JTableField tableField = field.getAnnotation(JTableField.class);
            if (tableField.order() == columnIndex) {
                try {
                    PropertyDescriptor propertyDescriptor = new PropertyDescriptor(field.getName(), clz);
                    Method writeMethod = propertyDescriptor.getWriteMethod();
                    writeMethod.invoke(t, aValue);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

            }
        }
        fireTableCellUpdated(rowIndex, columnIndex);
    }

    @Override
    public String getColumnName(int column) {
        return columnNames.get(column);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }

    @Override
    public int getRowCount() {
        return dataList.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.size();
    }

}
