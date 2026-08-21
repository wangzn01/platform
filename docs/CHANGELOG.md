# 版本更新记录

本文件记录每个版本的重要变化，最新版本在最上方。变更类型使用「新增」「修改」「修复」「移除」。

## 0.1.0 - 未发布

### 新增

- 使用 Java 25、Spring Boot 4.1.0、Spring MVC 和 Maven 搭建 Web 后端项目骨架，默认监听 `9999` 端口。
- 集成 Lombok，并为生产代码和测试代码配置编译期注解处理器。
- 采用 Spring MVC 官方 API Versioning 实现路径段版本管理，新增 `GET /hello` 未版本化示例接口，以及
  `GET /v1/test`、`GET /v2/test` 两个 API 版本示例接口。
- 新增 `Result<T>` 统一结构化响应、`ResultEnum` 通用应用码和对应测试。`Result.fail` 提供自定义说明文案的重载，
  用于校验失败等需要指出具体原因的场景；成功响应固定使用统一文案，不开放自定义。
- 新增应用上下文测试、Controller 切片测试和 API 版本行为测试。
- 新增项目说明（`README.md`）、AI 协作规则（`AGENTS.md`）、架构与公共契约（`docs/ARCHITECTURE.md`）、
  代码格式与风格规范（`docs/STYLE.md`）、版本更新记录（`docs/CHANGELOG.md`）和 Claude Code 规则入口
  （`CLAUDE.md`）文档，确立增删改查接口模板、DTO/Query/VO/Entity 命名与载体约定、GET/POST 请求方法约定
  和验证档位。
- 新增 `.editorconfig` 编辑器格式配置与 `.gitattributes` Git 换行规则，作为代码风格规范的机器可执行配置。

### 修改

- 按功能内聚调整包归属：平台公共服务放在 `common`；全部业务模块放在 `biz/<context>/`，尚未有业务时不创建 `biz`；
  中间件与外部系统装配放在 `infra/<capability>/`，尚未接入时不创建 `infra`；骨架示例放在 `demo`，第一个业务模块进入
  `biz` 后删除。根包、功能包、限界上下文包与中间件能力包用 `package-info.java` 说明职责；分层子包不写。功能包与中间件
  能力包内类型平铺，不按技术分层再拆。落点按「平台能力 / 外部系统 / 业务上下文 / 示例」判断，不因使用 Spring 把平台能力
  迁入 `infra`。
