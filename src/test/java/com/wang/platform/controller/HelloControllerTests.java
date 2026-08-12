package com.wang.platform.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 接口测试
 */
@WebMvcTest(HelloController.class)
class HelloControllerTests {

    /**
     * WebMvcTest 切片注入的 MockMvc，用于驱动 controller。
     */
    private final MockMvc mockMvc;


    @Autowired
    HelloControllerTests(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }


    /**
     * GET /hello 应返回 200，文本类型为 text/plain，内容为固定欢迎语。
     */
    @Test
    void shouldReturnHelloMessage() throws Exception {
        mockMvc.perform(get("/hello"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("text/plain"))
                .andExpect(content().string("Hello, World!"));
    }

}
