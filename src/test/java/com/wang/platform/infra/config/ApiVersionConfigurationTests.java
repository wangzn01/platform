package com.wang.platform.infra.config;

import com.wang.platform.controller.HelloController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.accept.ApiVersionStrategy;
import org.springframework.web.util.ServletRequestPathUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 公共 API 路径版本配置测试
 *
 * <p>本类覆盖配置类交给 Spring MVC 的版本解析规则与未版本化接口的可访问性，版本段路由与非法版本的 HTTP
 * 结果由 {@code TestControllerTests} 覆盖。{@code /hello} 返回 200 不是必然结果：版本解析在处理器匹配之后
 * 执行，版本机制拒绝请求时返回 400。丢掉 {@code usePathSegment} 的路径 Predicate 时首段 {@code hello} 会被
 * 当作版本解析（Invalid API version）；{@code spring.mvc.apiversion.required} 改为 {@code true} 时缺少版本的
 * 请求被拒（API version is required）
 */
@WebMvcTest(HelloController.class)
@Import(ApiVersionConfiguration.class)
class ApiVersionConfigurationTests {

    /**
     * MVC 测试客户端，用于验证未版本化接口的实际响应
     */
    private final MockMvc mockMvc;

    /**
     * 容器按本项目配置构建的版本解析策略，用于直接验证版本来源与适用范围
     */
    private final ApiVersionStrategy versionStrategy;


    /**
     * 创建公共 API 路径版本配置测试
     *
     * @param mockMvc         MVC 测试客户端
     * @param versionStrategy 版本解析策略
     */
    @Autowired
    ApiVersionConfigurationTests(MockMvc mockMvc, ApiVersionStrategy versionStrategy) {
        this.mockMvc = mockMvc;
        this.versionStrategy = versionStrategy;
    }


    /**
     * 验证版本只从版本化路径的首段解析：{@code /v2/test} 解析出版本段原文，{@code /hello} 解析不出版本。
     * 路径 Predicate 缺失时 {@code /hello} 会解析出 {@code hello}，段索引改变时 {@code /v2/test} 会解析出
     * {@code test}，两种情况都使本用例失败
     */
    @Test
    void versionShouldBeResolvedOnlyFromVersionedPathSegment() {
        assertThat(resolveVersion(get("/v2/test"))).isEqualTo("v2");
        assertThat(resolveVersion(get("/hello"))).isNull();
    }


    /**
     * 验证路径是唯一版本来源：未版本化路径携带 {@code Api-Version} 请求头或 {@code api-version} 查询参数时
     * 仍解析不出版本，额外启用请求头或查询参数版本来源都使本用例失败
     */
    @Test
    void versionShouldNotBeResolvedFromHeaderOrQueryParam() {
        assertThat(resolveVersion(get("/hello").header("Api-Version", "2"))).isNull();
        assertThat(resolveVersion(get("/hello?api-version=2"))).isNull();
    }


    /**
     * 验证未版本化接口不进入版本解析即可访问，返回 200 而不是缺少版本的 400
     */
    @Test
    void unversionedEndpointShouldBeReachableWithoutVersion() throws Exception {
        mockMvc.perform(get("/hello"))
            .andExpect(status().isOk());
    }


    /**
     * 解析请求的原始版本值
     *
     * @param requestBuilder 请求构造器
     * @return 版本化路径返回版本段原文，解析不出版本时返回 null
     */
    private String resolveVersion(MockHttpServletRequestBuilder requestBuilder) {
        MockHttpServletRequest request = requestBuilder.buildRequest(new MockServletContext());

        // 路径段版本解析依赖已解析并缓存的 RequestPath，真实请求由 DispatcherServlet 完成该步骤
        ServletRequestPathUtils.parseAndCache(request);

        return versionStrategy.resolveVersion(request);
    }

}