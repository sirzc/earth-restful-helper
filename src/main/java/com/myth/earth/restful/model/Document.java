package com.myth.earth.restful.model;

import com.myth.earth.restful.enums.HttpMethod;
import com.myth.earth.restful.enums.RequestType;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * markdown文档生成实体
 *
 * @author changan
 * @date 2024/3/13 13:36
 **/
@Data
public class Document implements Serializable {

    private static final long            serialVersionUID = 1L;
    /**
     * 接口地址
     */
    private              String          path;
    /**
     * 示例路径：例：path?username=张三&age=14
     */
    private              String          examplePath;
    /**
     * 接口描述
     */
    private              String          desc;
    /**
     * 请求类型
     */
    private              HttpMethod      httpMethod;
    /**
     * 请求参数类型
     */
    private              RequestType     requestType;
    /**
     * 请求参数
     */
    private              List<ParamInfo> requestParams;
    /**
     * 响应参数
     */
    private              ParamInfo       responseParam;
    /**
     * 请求示例
     */
    private              String          requestEg;
    /**
     * 返回示例
     */
    private              String          responseEg;

}
