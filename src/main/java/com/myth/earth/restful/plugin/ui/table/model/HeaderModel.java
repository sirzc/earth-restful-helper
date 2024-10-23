package com.myth.earth.restful.plugin.ui.table.model;

import com.myth.earth.restful.plugin.ui.table.annotations.JTableField;
import lombok.Data;

/**
 * key-value 模型
 *
 * @author zhouchao
 * @date 2024-03-31 15:21
 */
@Data
public class HeaderModel {

    @JTableField(title = "", order = 0, width = 40, isSelect = true)
    private boolean select;

    @JTableField(title = "key", order = 1, width = 150)
    private String key;

    @JTableField(title = "value", order = 2)
    private String value;

    public HeaderModel() {
        this.select = false;
    }

    public HeaderModel(String key, String value) {
        this.select = true;
        this.key = key;
        this.value = value;
    }
}
