package com.wang.platform.demo.controller;

import com.wang.platform.common.apiversion.ApiVersions;
import com.wang.platform.common.response.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * API 版本示例接口
 *
 * <p>路径变量 {@code apiVersion} 只承担版本段的路由约束，版本值由 API Versioning 从路径段解析，
 * 不注入方法参数，因此抑制 IDEA 的路径变量未使用检查
 */
@SuppressWarnings("MVCPathVariableInspection")
@RestController
@RequestMapping(ApiVersions.VERSION_PATH_TEMPLATE + "/test")
public class TestController {

    /**
     * 返回版本 1 的固定 API 版本示例结果
     *
     * @return API 版本示例结果
     */
    @GetMapping(version = ApiVersions.V1)
    public Result<String> testVersionOne() {
        return Result.success("API version " + ApiVersions.V1);
    }


    /**
     * 返回版本 2 的固定 API 版本示例结果
     *
     * @return API 版本示例结果
     */
    @GetMapping(version = ApiVersions.V2)
    public Result<String> testVersionTwo() {
        return Result.success("API version " + ApiVersions.V2);
    }

}