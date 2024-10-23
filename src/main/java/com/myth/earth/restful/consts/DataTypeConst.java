package com.myth.earth.restful.consts;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.jetbrains.annotations.NonNls;

import java.util.Map;
import java.util.Set;

/**
 * 数据类型常量集合
 *
 * @author zhouchao
 * @date 2022-04-01 8:39
 */
public final class DataTypeConst {

    private DataTypeConst() {
    }

    /**
     * 包装数据类型初始值
     */
    @NonNls
    public static final Map<String, Object> BASE_WRAPPER_TYPE_INIT_VALUE;
    /**
     * 基本数据类型
     */
    public static final Set<String>         BASIC_DATA_TYPE;
    /**
     * 包装数据类型
     */
    public static final Set<String> WRAPPER_DATA_TYPE;
    /**
     * 无返回值标记
     */
    public static final String      VOID_MARK = "void";

    static {
        BASIC_DATA_TYPE = Sets.newHashSet("byte", "short", "int", "long", "char", "float", "double", "boolean");
        WRAPPER_DATA_TYPE = Sets.newHashSet("Byte", "Short", "Integer", "Long", "Character", "Float", "Double", "Boolean", "String");

        // 包装数据类型
        BASE_WRAPPER_TYPE_INIT_VALUE = Maps.newHashMapWithExpectedSize(16);
        BASE_WRAPPER_TYPE_INIT_VALUE.put("Byte", 0);
        BASE_WRAPPER_TYPE_INIT_VALUE.put("Short", 0);
        BASE_WRAPPER_TYPE_INIT_VALUE.put("Integer", 0);
        BASE_WRAPPER_TYPE_INIT_VALUE.put("Long", 0L);
        BASE_WRAPPER_TYPE_INIT_VALUE.put("Float", 0.0F);
        BASE_WRAPPER_TYPE_INIT_VALUE.put("Double", 0.0D);
        BASE_WRAPPER_TYPE_INIT_VALUE.put("Boolean", false);
        BASE_WRAPPER_TYPE_INIT_VALUE.put("Character", "");

        // 其他
        BASE_WRAPPER_TYPE_INIT_VALUE.put("String", "");
        BASE_WRAPPER_TYPE_INIT_VALUE.put("BigDecimal", "");
        BASE_WRAPPER_TYPE_INIT_VALUE.put("Date", System.currentTimeMillis());
        BASE_WRAPPER_TYPE_INIT_VALUE.put("LocalDate", "");
        BASE_WRAPPER_TYPE_INIT_VALUE.put("LocalTime", "");
        BASE_WRAPPER_TYPE_INIT_VALUE.put("LocalDateTime", "");

    }
}