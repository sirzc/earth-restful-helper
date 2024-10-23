package com.myth.earth.restful.utils;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * 文件操作
 *
 * @author changan
 * @date 2021-09-08 18:34
 */
public final class FileUtil {

    /**
     * 读取文件内容到流中
     *
     * @param filePath       文件路径
     * @param fileFunctional 文件后续操作
     */
    public static void readFileToWrite(String filePath, IFileFunctional fileFunctional) {
        try (Writer writer = new FileWriter(filePath)) {
            fileFunctional.process(writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FunctionalInterface
    public interface IFileFunctional {

        /**
         * 文件流处理
         *
         * @param writer 待操作的流
         */
        void process(Writer writer);
    }
}
