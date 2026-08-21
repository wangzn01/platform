package com.wang.platform.demo.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 未版本化的示例接口
 */
@RestController
public class HelloController {

    /**
     * 返回固定欢迎语
     *
     * @return 欢迎文本
     */
    @GetMapping(path = "/hello", produces = MediaType.TEXT_PLAIN_VALUE)
    public String hello() {
        return "Hello, World!";
    }

}