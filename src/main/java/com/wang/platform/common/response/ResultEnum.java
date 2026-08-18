package com.wang.platform.common.response;

import lombok.Getter;

/**
 * 统一结构化响应使用的通用应用码。数值借用常见 HTTP 状态码，便于识别，但不替代 HTTP 响应状态
 */
@Getter
public enum ResultEnum {

    /**
     * 请求处理成功
     */
    SUCCESS(200, "OK"),

    /**
     * 请求内容或格式无效
     */
    BAD_REQUEST(400, "请求无效"),

    /**
     * 请求未提供有效的认证信息
     */
    UNAUTHORIZED(401, "认证信息无效"),

    /**
     * 当前身份无权访问目标资源
     */
    FORBIDDEN(403, "无权访问该资源"),

    /**
     * 请求的资源不存在
     */
    NOT_FOUND(404, "资源不存在"),

    /**
     * 目标资源不支持当前请求方法
     */
    METHOD_NOT_ALLOWED(405, "请求方法不允许"),

    /**
     * 服务端发生未预期错误
     */
    SERVER_ERROR(500, "服务器内部错误");


    /**
     * 应用码数值。当前通用码借用 HTTP 状态码数值，但响应状态由 API 边界单独决定
     */
    private final int code;

    /**
     * 默认响应说明文案。枚举内置的提示文字，调用方应保证其安全且可展示
     */
    private final String msg;


    /**
     * 创建通用应用码枚举
     *
     * @param code 应用码
     * @param msg  默认响应说明文案
     */
    ResultEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

}