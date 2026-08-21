package com.wang.platform.common.apiversion;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 公共 API 版本定义测试
 */
class ApiVersionsTests {

    /**
     * 验证符合主版本格式的路径被判定为版本化路径
     */
    @Test
    void versionedPathShouldBeRecognized() {
        assertThat(ApiVersions.isVersionedPath("/v1")).isTrue();
        assertThat(ApiVersions.isVersionedPath("/v1/test")).isTrue();
        assertThat(ApiVersions.isVersionedPath("/v999/articles/detail/1")).isTrue();
    }


    /**
     * 验证主版本为零的路径不属于版本化路径
     */
    @Test
    void zeroVersionShouldNotBeVersionedPath() {
        assertThat(ApiVersions.isVersionedPath("/v0/test")).isFalse();
    }


    /**
     * 验证超过三位的主版本不属于版本化路径，避免数值解析溢出
     */
    @Test
    void oversizedVersionShouldNotBeVersionedPath() {
        assertThat(ApiVersions.isVersionedPath("/v2147483648/test")).isFalse();
    }


    /**
     * 验证非版本首段与非法版本格式都不属于版本化路径
     */
    @Test
    void nonVersionPathShouldNotBeVersionedPath() {
        assertThat(ApiVersions.isVersionedPath("/hello")).isFalse();
        assertThat(ApiVersions.isVersionedPath("/foo/test")).isFalse();
        assertThat(ApiVersions.isVersionedPath("/v1.x/test")).isFalse();
        assertThat(ApiVersions.isVersionedPath("/version1/test")).isFalse();
    }

}