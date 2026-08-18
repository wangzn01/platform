package com.wang.platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Platform 应用入口
 */
@SpringBootApplication
public class PlatformApplication {

    /**
     * 启动 Platform 应用
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        SpringApplication.run(PlatformApplication.class, args);
    }

}