package com.myth.earth.restful.core.builder;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.tags.Tag;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * open api builder
 * @date 2023-06-07 18:09
 */
public class OpenApiBuilder {

    /**
     * 必填，应用的名称
     */
    private final String    title;
    /**
     * 对所提供的API有效的路径和操作
     */
    private final Paths     paths = new Paths();
    /**
     * 提供更多元数据的一系列标签，标签的顺序可以被转换工具用来决定API的顺序，每个标签名都应该是唯一的
     */
    private final List<Tag>  tags       = new ArrayList<>();
    /**
     * 包含开放API规范固定的各种可重用组件。当没有被其他对象引用时，在这里定义定义的组件不会产生任何效果。
     */
    private final Components components = new Components();

    public OpenApiBuilder(String title) {
        this.title = title;
    }

    /**
     * 生成openAPI信息
     *
     * @return openAPI
     */
    public OpenAPI build() {
        return build(null);
    }

    /**
     * 生成openAPI信息
     *
     * @param description api描述
     * @return openAPI
     */
    public OpenAPI build(String description) {
        // 此字段提供API相关的元数据，相关工具可能需要这个字段
        Info info = new Info();
        info.setTitle(this.title);
        info.setDescription(description);
        info.setVersion("1.3.1");

        // OpenAPI document的根文档对象
        OpenAPI openapi = new OpenAPI();
        openapi.setInfo(info);
        openapi.setPaths(this.paths);
        openapi.setTags(this.tags);
        openapi.setComponents(this.components);
        return openapi;
    }

    /**
     * 添加api信息
     *
     * @param apiPath  api路径
     * @param pathItem api相关信息
     */
    public void addApi(String apiPath, PathItem pathItem) {
        this.paths.addPathItem(apiPath, pathItem);
    }

    /**
     * 添加一个标签
     *
     * @return OpenapiBuilder
     */
    public OpenApiBuilder addTag(Tag tag) {
        this.tags.add(tag);
        return this;
    }

    /**
     * 添加一个类型引用
     *
     * @param key 类名（例：User）
     * @param schema  引用类型
     * @return OpenapiBuilder
     */
    public OpenApiBuilder addSchemas(String key, Schema schema) {
        this.components.addSchemas(key, schema);
        return this;
    }

    /**
     * 添加一组类型引用
     *
     * @param schemas 引用类型Map
     * @return OpenApiBuilder
     */
    public OpenApiBuilder setSchemas(Map<String, Schema> schemas) {
        this.components.setSchemas(schemas);
        return this;
    }

    /**
     * 判断是否包含
     *
     * @param key schema名称
     * @return  component中是否已经包含
     */
    public boolean containSchema(String key) {
        return this.components.getSchemas() != null && this.components.getSchemas().containsKey(key);
    }

    /**
     * 添加一个标签
     *
     * @param name        名称，用于分组
     * @param description 描述
     * @return OpenapiBuilder
     */
    public OpenApiBuilder addTag(String name, String description) {
        Tag tag = new Tag();
        tag.setName(name);
        tag.setDescription(description);

        this.tags.add(tag);
        return this;
    }


    /**
     * openapi 构造器
     *
     * @param title 应用名称
     * @return openapi构造器
     */
    public static OpenApiBuilder create(String title) {
        return new OpenApiBuilder(title);
    }

    /**
     * 合并OpenApiBuilder
     *
     * @param builder 构建器
     */
    public void merge(@NotNull OpenApiBuilder builder) {
        // 合并路径;
        this.paths.putAll(builder.getPaths());

        // 合并schemas
        if (this.getComponents().getSchemas() != null) {
            this.components.getSchemas().putAll(builder.getComponents().getSchemas());
        } else {
            builder.getComponents().getSchemas().forEach(this.components::addSchemas);
        }

        // 合并tags
        this.tags.addAll(builder.getTags());
    }

    private Paths getPaths() {
        return paths;
    }

    private Components getComponents() {
        return components;
    }

    private List<Tag> getTags() {
        return tags;
    }
}

