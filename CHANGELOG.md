# 版本更新记录

本文件记录每个版本的重要变化，最新版本在最上方。变更类型使用「新增」「修改」「修复」「移除」。

## 0.1.0 - 未发布

### 新增

- 使用 Java 25、Spring Boot 4.1.0 与 Maven 搭建后端项目骨架。
- 集成 Spring MVC 与嵌入式 Tomcat，默认监听 `9999` 端口。
- 集成 Lombok，并为生产代码与测试代码分别配置注解处理器；`Result` 与 `ResultEnum` 采用 `@Getter` 生成访问器。
- 新增 `GET /hello` 示例接口，返回 `Hello, World!`。
- 新增公共 API 全局返回参数 `Result<T>` 与业务状态码枚举 `ResultEnum`，放在 `common/response/`。
- 新增应用上下文启动测试、Controller 切片测试，以及覆盖 `Result` 静态工厂方法与 JSON 字段集合的 `ResultTests`。
- 新增中文 README、AI 协作规则、技术决策记录、技术待办、代码格式与风格规范，以及跨系统编码与换行规范。
