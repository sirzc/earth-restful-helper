package com.myth.earth.restful.plugin.ui.table.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 表格字段
 *
 * @author zhouchao
 * @date 2024/3/31 10:50
 **/
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface JTableField {

    /**
     * 字段列名
     * @return 列名
     */
    String title() default "列名";

    /**
     * 是否选择项
     *
     * @return 默认否
     */
    boolean isSelect() default false;

    /**
     * 顺序
     * @return 顺序
     */
    int order();

    /**
     * 宽度
     * @return 宽度
     */
    int width() default 0;

    /**
     * 最小宽度
     * @return 最小宽度
     */
    int min() default 0;

    /**
     * 最大宽度
     * @return 最大宽度
     */
    int max() default 0;
}
