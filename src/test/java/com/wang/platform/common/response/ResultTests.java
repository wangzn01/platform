package com.wang.platform.common.response;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * 统一结构化响应测试
 */
class ResultTests {

    /**
     * 测试用 JSON 映射器，用于断言统一响应的序列化字段
     */
    private final ObjectMapper objectMapper = new ObjectMapper();


    /**
     * 验证无数据的成功响应使用统一成功应用码和说明，并保持业务载荷为空
     */
    @Test
    void successWithoutDataShouldUseSuccessCodeAndMsg() {
        Result<Void> result = Result.success();

        assertThat(result.getCode()).isEqualTo(ResultEnum.SUCCESS.getCode());
        assertThat(result.getMsg()).isEqualTo(ResultEnum.SUCCESS.getMsg());
        assertThat(result.getData()).isNull();
    }


    /**
     * 验证带数据的成功响应保留业务载荷，并使用统一成功应用码
     */
    @Test
    void successShouldCarryData() {
        Result<String> result = Result.success("payload");

        assertThat(result.getCode()).isEqualTo(ResultEnum.SUCCESS.getCode());
        assertThat(result.getData()).isEqualTo("payload");
    }


    /**
     * 验证失败响应使用指定错误枚举的应用码和说明，并保持业务载荷为空
     */
    @Test
    void failWithEnumShouldUseEnumCodeAndMsgWithoutData() {
        Result<String> result = Result.fail(ResultEnum.NOT_FOUND);

        assertThat(result.getCode()).isEqualTo(ResultEnum.NOT_FOUND.getCode());
        assertThat(result.getMsg()).isEqualTo(ResultEnum.NOT_FOUND.getMsg());
        assertThat(result.getData()).isNull();
    }


    /**
     * 验证失败工厂拒绝成功枚举，避免产生语义矛盾的响应
     */
    @Test
    void failWithSuccessEnumShouldBeRejected() {
        assertThatThrownBy(() -> Result.fail(ResultEnum.SUCCESS))
            .isInstanceOf(IllegalArgumentException.class);
    }


    /**
     * 验证失败工厂拒绝空枚举，并返回明确的参数错误信息
     */
    @Test
    void failWithNullEnumShouldBeRejected() {
        assertThatThrownBy(() -> Result.fail(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("错误枚举不能为空");
    }


    /**
     * 验证自定义文案的失败响应保留枚举应用码，并使用调用方给出的说明
     */
    @Test
    void failWithCustomMsgShouldUseEnumCodeAndCustomMsg() {
        Result<String> result = Result.fail(ResultEnum.BAD_REQUEST, "手机号格式无效");

        assertThat(result.getCode()).isEqualTo(ResultEnum.BAD_REQUEST.getCode());
        assertThat(result.getMsg()).isEqualTo("手机号格式无效");
        assertThat(result.getData()).isNull();
    }


    /**
     * 验证自定义文案的失败工厂同样拒绝成功枚举
     */
    @Test
    void failWithCustomMsgAndSuccessEnumShouldBeRejected() {
        assertThatThrownBy(() -> Result.fail(ResultEnum.SUCCESS, "自定义说明"))
            .isInstanceOf(IllegalArgumentException.class);
    }


    /**
     * 验证自定义文案的失败工厂拒绝空文案，避免响应缺少说明
     */
    @Test
    void failWithNullMsgShouldBeRejected() {
        assertThatThrownBy(() -> Result.fail(ResultEnum.BAD_REQUEST, null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("响应说明文案不能为空");
    }


    /**
     * 验证成功与失败响应序列化后都只包含 code、data、msg 三个约定字段
     */
    @Test
    void serializedFieldsShouldBeCodeDataAndMsgOnly() {
        JsonNode success = objectMapper.readTree(objectMapper.writeValueAsString(Result.success("payload")));
        JsonNode failure = objectMapper.readTree(objectMapper.writeValueAsString(Result.fail(ResultEnum.SERVER_ERROR)));

        // 成功与失败响应的字段集合都应只包含 code、data、msg
        for (JsonNode node : new JsonNode[]{success, failure}) {
            assertThat(node.propertyNames()).containsExactlyInAnyOrder("code", "data", "msg");
        }

        assertThat(failure.get("data").isNull()).isTrue();
    }

}