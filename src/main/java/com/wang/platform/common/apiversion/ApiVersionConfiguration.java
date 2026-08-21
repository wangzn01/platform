package com.wang.platform.common.apiversion;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.RequestPath;
import org.springframework.web.servlet.config.annotation.ApiVersionConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 公共 API 路径版本配置
 *
 * <p>版本解析使用 Spring MVC 官方的 {@link ApiVersionConfigurer}；版本段的位置、格式与路径判定统一由
 * {@link ApiVersions} 提供，避免额外的自定义注解或解析器
 */
@Configuration(proxyBeanMethods = false)
public class ApiVersionConfiguration implements WebMvcConfigurer {

    /**
     * 从请求路径中约定的版本段位置解析 API 版本
     *
     * @param configurer Spring MVC API 版本配置器
     */
    @Override
    public void configureApiVersioning(ApiVersionConfigurer configurer) {
        configurer.usePathSegment(ApiVersions.VERSION_PATH_SEGMENT_INDEX, ApiVersionConfiguration::isVersionedPath);
    }


    /**
     * 判断请求是否应交给 API 版本解析器处理
     *
     * @param path 已解析的请求路径
     * @return 版本化 API 返回 true，否则返回 false
     */
    private static boolean isVersionedPath(RequestPath path) {
        return ApiVersions.isVersionedPath(path.pathWithinApplication().value());
    }

}