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
public class HostModel {

    @JTableField(title = "name", order = 0, width = 175)
    private String name;

    @JTableField(title = "host", order = 1)
    private String host;

    public HostModel() {
    }

    public HostModel(String name, String host) {
        this.name = name;
        this.host = host;
    }

    public String toString() {
        return name;
    }

}
