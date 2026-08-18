package com.wang.platform.common.api;

import java.util.regex.Pattern;

/**
 * 公共 API 版本定义
 *
 * <p>版本段位于路径首段，格式为 {@code v} 加一至三位正整数。{@link #VERSION_PATH_TEMPLATE} 供版本化
 * Controller 声明映射路径，{@link #VERSION_PATH_SEGMENT_INDEX} 与 {@link #isVersionedPath(String)}
 * 供 API 版本解析配置使用，三者共用同一份版本段格式定义
 */
public final class ApiVersions {

    /**
     * 当前维护的 API 版本 1
     */
    public static final String V1 = "1";

    /**
     * 当前维护的 API 版本 2
     */
    public static final String V2 = "2";

    /**
     * 版本段的 URI 正则。项目接受 {@code v} 加一至三位正整数主版本的路径段。
     * 量词写作 {@code \d?\d?} 而非 {@code \d{0,2}}，避免花括号中的逗号被 Apifox 等工具误判为路径分隔符
     */
    public static final String VERSION_SEGMENT_PATTERN = "v[1-9]\\d?\\d?";

    /**
     * 版本化 API 的 URI 模板
     */
    public static final String VERSION_PATH_TEMPLATE = "/{apiVersion:" + VERSION_SEGMENT_PATTERN + "}";

    /**
     * 版本号在请求路径中的段索引。版本段位于首段，与 {@link #VERSION_PATH_TEMPLATE} 保持一致
     */
    public static final int VERSION_PATH_SEGMENT_INDEX = 0;

    /**
     * 版本化路径的完整匹配规则。首段为版本段，其后是可选的剩余路径
     */
    private static final Pattern VERSIONED_PATH_PATTERN =
        Pattern.compile("^/" + VERSION_SEGMENT_PATTERN + "(?:/.*)?$");


    /**
     * 禁止实例化 API 版本定义
     */
    private ApiVersions() {
    }


    /**
     * 判断路径是否符合本项目的版本化路径格式
     *
     * @param path 应用内的请求路径，以 {@code /} 开头
     * @return 首段为合法版本段时返回 true，否则返回 false
     */
    public static boolean isVersionedPath(String path) {
        return VERSIONED_PATH_PATTERN.matcher(path).matches();
    }

}