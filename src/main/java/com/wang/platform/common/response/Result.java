package com.wang.platform.common.response;

import lombok.Getter;

import java.util.Objects;

/**
 * 公共 API 的统一结构化响应体。纯文本、文件流和无响应体等特殊响应不使用本类型
 *
 * @param <T> 业务载荷类型
 * @see ResultEnum
 */
@Getter
public final class Result<T> {

    /**
     * 应用码。当前通用应用码见 {@link ResultEnum}；业务模块出现后再定义稳定的业务错误契约
     */
    private final int code;

    /**
     * 业务载荷。成功时返回数据，失败时为 null
     */
    private final T data;

    /**
     * 响应说明文案。成功时为默认成功提示，失败时携带安全、可读的错误描述
     */
    private final String msg;


    /**
     * 创建统一结构化响应
     *
     * @param code 应用码
     * @param data 业务载荷
     * @param msg  响应说明文案
     */
    private Result(int code, T data, String msg) {
        this.code = code;
        this.data = data;
        this.msg = msg;
    }


    /**
     * 成功，无业务数据
     */
    public static <T> Result<T> success() {
        return new Result<>(ResultEnum.SUCCESS.getCode(), null, ResultEnum.SUCCESS.getMsg());
    }


    /**
     * 成功，带业务数据
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(ResultEnum.SUCCESS.getCode(), data, ResultEnum.SUCCESS.getMsg());
    }


    /**
     * 失败，使用错误枚举
     */
    public static <T> Result<T> fail(ResultEnum resultEnum) {
        Objects.requireNonNull(resultEnum, "错误枚举不能为空");

        return fail(resultEnum, resultEnum.getMsg());
    }


    /**
     * 失败，使用错误枚举的应用码与自定义说明文案。适用于校验失败等需要指出具体原因的场景
     */
    public static <T> Result<T> fail(ResultEnum resultEnum, String msg) {
        Objects.requireNonNull(resultEnum, "错误枚举不能为空");
        Objects.requireNonNull(msg, "响应说明文案不能为空");

        // 失败工厂只接受错误应用码，避免生成语义自相矛盾的响应体
        if (ResultEnum.SUCCESS.getCode() == resultEnum.getCode()) {
            throw new IllegalArgumentException("失败结果不能使用成功应用码");
        }

        return new Result<>(resultEnum.getCode(), null, msg);
    }

}