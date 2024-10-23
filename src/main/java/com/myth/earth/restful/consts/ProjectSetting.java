package com.myth.earth.restful.consts;

import java.util.HashSet;
import java.util.Set;

/**
 * 可做成配置的数据
 *
 * @author zhouchao
 * @date 2023/5/3 16:43
 **/
public final class ProjectSetting {
    /**
     * 需要排除的数据类型
     */
    public static final Set<String> excludeParameterTypes = new HashSet<>() {{
        add("org.springframework.core.io.InputStreamSource");
        add("javax.servlet.ServletResponse");
        add("javax.servlet.ServletRequest");
    }};

    /**
     * 需要排除的字段
     */
    public static Set<String> excludeFieldNames = new HashSet<>() {{
        add("serialVersionUID");
    }};

    /**
     * 被注解的字段需要过滤掉
     */
    public static Set<String> excludeFieldAnnotation = new HashSet<>() {{
        add("javax.annotation.Resource");
        add("org.springframework.beans.factory.annotation.Autowired");
        add("org.apache.dubbo.config.annotation.Reference");
        add("org.apache.dubbo.config.annotation.DubboReference");
    }};


    /**
     * 被注解的字段需要过滤掉
     */
    public static Set<String> excludeClassPackage = new HashSet<>() {{
        add("com.baomidou.mybatisplus.extension.activerecord.Model");
    }};

}

