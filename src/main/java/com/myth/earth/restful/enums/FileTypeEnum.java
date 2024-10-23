package com.myth.earth.restful.enums;

import com.intellij.ide.highlighter.HtmlFileType;
import com.intellij.ide.highlighter.XmlFileType;
import com.intellij.json.JsonFileType;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypes;
import lombok.Getter;

/**
 * 文件类型枚举
 *
 * @author zhouchao
 * @date 2024-06-17 下午11:10
 */
public enum FileTypeEnum {

    TEXT("TEXT", FileTypes.PLAIN_TEXT), JSON("JSON", JsonFileType.INSTANCE), XML("XML", XmlFileType.INSTANCE), HTML("HTML", HtmlFileType.INSTANCE);

    @Getter
    private final String code;

    @Getter
    private final FileType fileType;

    FileTypeEnum(String code, FileType fileType) {
        this.code = code;
        this.fileType = fileType;
    }
}
