package com.myth.earth.restful.enums;

import com.google.common.collect.Lists;
import com.intellij.json.JsonFileType;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypes;
import lombok.Getter;

import java.util.List;

/**
 * 请求类型枚举 <br/>
 * xml: application/xml
 * html: text/html
 *
 * @author changan
 * @date 2022-04-16 19:44
 */
public enum RequestType {
    /**
     * application/json
     */
    RAW_JSON("JSON", "application/json", JsonFileType.INSTANCE),
    /**
     * text/plain
     */
    RAW_TEXT("Text", "text/plain", FileTypes.PLAIN_TEXT),
    /**
     * multipart/form-data
     */
    FROM_DATA("form-date", "multipart/form-data", FileTypes.UNKNOWN),
    /**
     * application/x-www-form-urlencoded
     */
    X_WWW_FORM_URLENCODED("x-www-form-urlencoded", "application/x-www-form-urlencoded", FileTypes.UNKNOWN),
    ;

    @Getter
    private final String   code;
    @Getter
    private final String   desc;
    @Getter
    private final FileType fileType;

    RequestType(String code, String desc, FileType fileType) {
        this.code = code;
        this.desc = desc;
        this.fileType = fileType;
    }

    public static final List<RequestType> RAW_LIST = Lists.newArrayList(RAW_JSON, RAW_TEXT);

    @Override
    public String toString() {
        return getCode();
    }
}
