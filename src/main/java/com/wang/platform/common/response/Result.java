package com.wang.platform.common.response;

import lombok.Getter;

/**
 * 公共 API 全局返回参数。所有 controller 通过静态工厂方法返回，不直接构造。
 *
 * @param <T> 业务载荷类型
 * @see ResultEnum
 */
@Getter
public class Result<T> {

    /**
     * 业务状态码。SUCCESS=200 表示成功，4xx 表示请求方问题，5xx 表示服务端问题；
     * 其他取值见 {@link ResultEnum} 与业务子码扩展。
     */
    private final int code;

    /**
     * 业务载荷。成功时返回数据，失败时为 null。
     */
    private final T data;

    /**
     * 业务说明文案。成功时为默认成功提示，失败时携带错误描述；
     * 调用 {@link #fail(int, String)} 时由调用方覆盖。
     */
    private final String msg;


    private Result(int code, T data, String msg) {
        this.code = code;
        this.data = data;
        this.msg = msg;
    }


    /**
     * 成功，无业务数据。
     */
    public static <T> Result<T> success() {
        return new Result<>(ResultEnum.SUCCESS.getCode(), null, ResultEnum.SUCCESS.getMsg());
    }


    /**
     * 成功，带业务数据。
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(ResultEnum.SUCCESS.getCode(), data, ResultEnum.SUCCESS.getMsg());
    }


    /**
     * 失败，使用错误枚举。
     */
    public static <T> Result<T> fail(ResultEnum resultEnum) {
        return new Result<>(resultEnum.getCode(), null, resultEnum.getMsg());
    }


    /**
     * 失败，指定业务子码和提示文案。code 必须是非 200 的错误码。
     */
    public static <T> Result<T> fail(int code, String msg) {
        return new Result<>(code, null, msg);
    }

}