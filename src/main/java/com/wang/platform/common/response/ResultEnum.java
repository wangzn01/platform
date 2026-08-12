package com.wang.platform.common.response;

import lombok.Getter;

/**
 * 全局返回参数使用的业务状态码枚举。沿用 HTTP 语义：
 * - 200 表示成功
 * - 4xx 表示请求方问题（参数、校验、鉴权、资源缺失）
 * - 5xx 表示服务端问题
 * 具体业务子码按需在控制器或服务层用 {@link Result#fail(int, String)} 扩展。
 */
@Getter
public enum ResultEnum {

    SUCCESS(200, "OK"),

    BAD_REQUEST(400, "请求参数错误"),

    VALIDATION_FAILED(422, "参数校验失败"),

    UNAUTHORIZED(401, "未登录"),

    FORBIDDEN(403, "无权限"),

    NOT_FOUND(404, "资源不存在"),

    METHOD_NOT_ALLOWED(405, "请求方法不允许"),

    SERVER_ERROR(500, "服务器内部错误");


    /**
     * 业务状态码。SUCCESS=200 表示成功，4xx 表示请求方问题，5xx 表示服务端问题。
     */
    private final int code;

    /**
     * 默认业务说明文案。枚举内置的提示文字，可被 {@link Result#fail(int, String)} 覆盖。
     */
    private final String msg;


    ResultEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

}