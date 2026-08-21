package com.wang.platform.demo.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 未版本化示例接口测试
 */
@WebMvcTest(HelloController.class)
class HelloControllerTests {

    /**
     * MVC 测试客户端，用于调用未版本化示例接口
     */
    private final MockMvc mockMvc;


    /**
     * 创建未版本化示例接口测试
     *
     * @param mockMvc MVC 测试客户端
     */
    @Autowired
    HelloControllerTests(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }


    /**
     * 验证 GET /hello 无需版本前缀即可返回固定欢迎文本
     */
    @Test
    void helloShouldReturnWelcomeTextWithoutVersionPrefix() throws Exception {
        mockMvc.perform(get("/hello"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith("text/plain"))
            .andExpect(content().string("Hello, World!"));
    }


    /**
     * 验证未版本化的 hello 接口不会匹配带版本前缀的路径
     */
    @Test
    void helloShouldNotMatchVersionPrefix() throws Exception {
        mockMvc.perform(get("/v1/hello"))
            .andExpect(status().isNotFound());
    }

}