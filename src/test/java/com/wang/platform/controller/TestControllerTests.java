package com.wang.platform.controller;

import com.wang.platform.infra.config.ApiVersionConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * API 版本示例接口测试
 */
@WebMvcTest(TestController.class)
@Import(ApiVersionConfiguration.class)
class TestControllerTests {

    /**
     * API 版本 1 示例接口路径
     */
    private static final String VERSION_ONE_TEST_PATH = "/v1/test";

    /**
     * API 版本 2 示例接口路径
     */
    private static final String VERSION_TWO_TEST_PATH = "/v2/test";

    /**
     * MVC 测试客户端，用于调用 API 版本示例接口
     */
    private final MockMvc mockMvc;


    /**
     * 创建 API 版本示例接口测试
     *
     * @param mockMvc MVC 测试客户端
     */
    @Autowired
    TestControllerTests(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }


    /**
     * 验证普通未知单段路径按未版本化请求处理并返回 404
     */
    @Test
    void unknownSingleSegmentShouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/unknown"))
            .andExpect(status().isNotFound());
    }


    /**
     * 验证 {@code GET /v1/test} 匹配版本 1 映射并返回统一成功响应
     */
    @Test
    void versionOneShouldReturnSuccessResult() throws Exception {
        mockMvc.perform(get(VERSION_ONE_TEST_PATH))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data").value("API version 1"))
            .andExpect(jsonPath("$.msg").value("OK"));
    }


    /**
     * 验证 {@code GET /v2/test} 匹配版本 2 映射并返回统一成功响应
     */
    @Test
    void versionTwoShouldReturnSuccessResult() throws Exception {
        mockMvc.perform(get(VERSION_TWO_TEST_PATH))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data").value("API version 2"))
            .andExpect(jsonPath("$.msg").value("OK"));
    }


    /**
     * 验证路径是唯一版本来源，请求头中的冲突版本不会改变版本 1 映射
     */
    @Test
    void versionOneShouldIgnoreConflictingApiVersionHeader() throws Exception {
        mockMvc.perform(get(VERSION_ONE_TEST_PATH).header("Api-Version", "2"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data").value("API version 1"))
            .andExpect(jsonPath("$.msg").value("OK"));
    }


    /**
     * 验证格式有效但未声明的版本由 Spring MVC 返回 400
     */
    @Test
    void unsupportedVersionShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/v3/test"))
            .andExpect(status().isBadRequest());
    }


    /**
     * 验证版本化示例接口缺少版本路径段时不会匹配
     */
    @Test
    void versionedEndpointShouldNotMatchWithoutVersionPrefix() throws Exception {
        mockMvc.perform(get("/test"))
            .andExpect(status().isNotFound());
    }


    /**
     * 验证不符合主版本格式的路径不会进入 API 版本解析
     */
    @Test
    void invalidVersionFormatShouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/v1.x/test"))
            .andExpect(status().isNotFound());
    }


    /**
     * 验证主版本为零的路径不符合项目版本格式并返回 404
     */
    @Test
    void zeroVersionShouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/v0/test"))
            .andExpect(status().isNotFound());
    }


    /**
     * 验证超过三位的主版本不进入版本解析，避免数值解析溢出
     */
    @Test
    void oversizedVersionShouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/v2147483648/test"))
            .andExpect(status().isNotFound());
    }


    /**
     * 验证非版本首段路径不会误匹配版本化示例接口
     */
    @Test
    void nonVersionSegmentShouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/foo/test"))
            .andExpect(status().isNotFound());
    }

}