package com.wang.platform.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 示例接口
 */
@RestController
@RequestMapping("/hello")
public class HelloController {

    /**
     * 返回固定欢迎语。
     *
     * @return 欢迎文本
     */
    @GetMapping(produces = MediaType.TEXT_PLAIN_VALUE)
    public String hello() {
        return "Hello, World!";
    }
}
