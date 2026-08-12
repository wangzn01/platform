package com.wang.platform.common.response;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 全局返回参数测试
 */
class ResultTests {

    /**
     * 测试用 Jackson ObjectMapper，用于断言序列化结果。
     */
    private final ObjectMapper objectMapper = new ObjectMapper();


    /**
     * success() 无参时应使用 SUCCESS 枚举的 code 与 msg，data 为 null。
     */
    @Test
    void successWithoutDataShouldUseSuccessCodeAndMsg() {
        Result<Void> result = Result.success();

        assertThat(result.getCode()).isEqualTo(ResultEnum.SUCCESS.getCode());
        assertThat(result.getMsg()).isEqualTo(ResultEnum.SUCCESS.getMsg());
        assertThat(result.getData()).isNull();
    }


    /**
     * success(data) 带参时应使用 SUCCESS 枚举的 code 与 msg，并保留传入的 data。
     */
    @Test
    void successShouldCarryData() {
        Result<String> result = Result.success("payload");

        assertThat(result.getCode()).isEqualTo(ResultEnum.SUCCESS.getCode());
        assertThat(result.getData()).isEqualTo("payload");
    }


    /**
     * fail(enum) 应使用枚举的 code 与 msg，data 固定为 null。
     */
    @Test
    void failWithEnumShouldUseEnumCodeAndMsgWithoutData() {
        Result<String> result = Result.fail(ResultEnum.NOT_FOUND);

        assertThat(result.getCode()).isEqualTo(ResultEnum.NOT_FOUND.getCode());
        assertThat(result.getMsg()).isEqualTo(ResultEnum.NOT_FOUND.getMsg());
        assertThat(result.getData()).isNull();
    }


    /**
     * fail(code, msg) 应保留调用方传入的 code 与 msg，data 固定为 null。
     */
    @Test
    void failWithCustomCodeShouldKeepGivenCodeAndMsg() {
        Result<String> result = Result.fail(4001, "余额不足");

        assertThat(result.getCode()).isEqualTo(4001);
        assertThat(result.getMsg()).isEqualTo("余额不足");
    }


    /**
     * 序列化响应体只输出 code、data、msg，不额外输出 success 等派生标志；该约束对成功与失败均成立。
     */
    @Test
    void serializedFieldsShouldBeCodeDataAndMsgOnly() {
        JsonNode success = objectMapper.readTree(objectMapper.writeValueAsString(Result.success("payload")));
        JsonNode failure = objectMapper.readTree(objectMapper.writeValueAsString(Result.fail(ResultEnum.SERVER_ERROR)));

        // 成功与失败响应的字段集合都应只包含 code、data、msg。
        for (JsonNode node : new JsonNode[]{success, failure}) {
            assertThat(node.propertyNames()).containsExactlyInAnyOrder("code", "data", "msg");
        }

        assertThat(failure.get("data").isNull()).isTrue();
    }

}