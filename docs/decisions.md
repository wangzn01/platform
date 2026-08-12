# 项目技术决策记录

本文件只记录已确认、会影响长期维护的技术选择，按确认日期升序排列。普通实现细节不记录；未决事项见 `docs/todo.md`。

## 2026-08-10：建立项目基础技术方案

- 语言与运行时 Java 25，应用框架 Spring Boot 4.1.x，构建工具 Maven，Web 栈为 Spring MVC + 嵌入式 Tomcat。
- Lombok 仅用于编译期减少样板代码，不进入运行时依赖。
- 本仓库只维护后端，前端作为独立项目。
- 依赖与插件版本优先由 Spring Boot Parent 管理，不重复声明版本。
- 使用与当前版本匹配的正式语言特性和框架能力，不降级 Java 或 Spring Boot 以迁就旧示例。
- 数据库、缓存、认证、消息队列、微服务与部署方案，在有实际需求并确认后再引入。
- 不单独维护需求文档；各文档的职责分工见 `AGENTS.md`「文档分工」。

## 2026-08-11：统一接口与持久化模型的载体形式

- `DTO` 接收前端写入类操作参数，`Query` 接收查询参数，`VO` 返回给前端的业务公共 API 结构化数据，`Entity` 仅用于数据库持久化映射。
- 不得使用 Entity 作为公共 API 的请求或响应模型。
- 面向前端的 DTO / Query / VO 不作为跨模块协作类型。
- 示例接口、测试接口，以及纯文本、文件流等特殊响应，不受 VO 约束。
- 四者默认使用普通 `class`，不主动使用 `record`；仅在用户明确要求且不可变语义适合该场景时使用 `record`。
- Lombok 按需选用，避免无差别使用 `@Data`；Entity 优先 `@Getter` / `@Setter`，谨慎生成 `equals`、`hashCode`、`toString`。

## 2026-08-11：采用模块化单体架构

- **整体形态**：业务按模块内聚，单进程部署；保持单 Maven 模块，模块化通过包边界与依赖纪律实现。
- **基础包**：`com.wang.platform`；启动类 `PlatformApplication` 放根包并扫描整个包树。
- **顶层包**：`common/` 放无框架依赖的纯定义与工具；`infra/` 放依赖 Spring 或第三方框架的横切能力；业务模块（如 `user/`、`blog/`）直接放根包下，不用 `modules/` 包裹。
- **模块内部**：按需包含 `controller/`、`service/`、持久化访问、`entity/`、`dto/`、`vo/`、`query/`，以及模块内 `constant/`、`enums/`、`exception/`、`job/`、`config/`。`service` 不强制接口与实现分离，单实现时直接写实现类。
- **不使用的结构**：不采用 `manager/` 层，不建立 `application/`、`domain/` 包层，不抽取无业务含义的通用 `Base*`、通用 Service、通用 Controller 与杂乱的 `utils`。
- **依赖方向**：业务模块 → `infra` → `common`；业务模块 → `common`；`common` 不依赖 `infra` 与业务模块；`infra` 不依赖业务模块；禁止循环依赖。
- **跨模块协作**：只通过被调用方 Service 公共方法；参数与返回值限于 JDK 基本/标准库类型、`common` 共享定义、被调用方明确对外的协作类型；对外协作类型默认放被调用方 `dto/`，并以注释或命名标明跨模块可用；不直接依赖其他模块的持久化访问、`entity` 及内部实现细节，也不构造其他模块的 Entity；跨模块编排放在调用方 Service。
- **定义归属**：只服务单模块的定义放该模块，多模块共享的纯定义放 `common/`；第三方封装只服务单一业务时放该业务模块，多业务共享的基础设施适配放 `infra/`。
- **异常体系**：可作为 API 错误来源的业务异常类型放 `common/exception/`，全局 `@RestControllerAdvice` 放 `infra/exception/`，不在 controller 各自处理异常；模块内 `exception/` 仅内部使用，对外错误须转为 `common/exception/` 类型或由全局处理统一映射。
- **安全边界**：`infra/security` 只负责登录态与鉴权横切，只暴露最小身份信息，不依赖业务 Entity、持久化访问或 Service；用户详情与权限业务规则由业务模块负责。
- **建包时机**：按需建包，不预建空的 `common/`、`infra/` 或业务模块目录。现有示例接口可暂留根包 `controller/`，出现真实业务模块后再迁入或隔离为 demo。

## 2026-08-11：公共 API 全局返回参数

- 公共 API 使用全局返回参数，不在 controller 自行拼装互不相同的 JSON 外壳。
- `Result<T>` 放在 `common/response/`，为无框架依赖的纯定义，不引入 Jackson 等框架注解。
- 字段只有三个，响应体也只输出这三个，不额外输出 `success` 等派生标志，成功与否由 `code` 判断：
  - `code`：业务状态码；沿用 HTTP 语义，200 表示成功，4xx 表示请求方问题，5xx 表示服务端问题；其他取值见 `ResultEnum` 与业务子码扩展。
  - `data`：业务载荷；成功时返回数据，失败时为 null。
  - `msg`：给前端展示的提示文案；失败时通常携带错误描述。
- 通用错误码集中在 `ResultEnum`，声明顺序与代码一致：SUCCESS=200、BAD_REQUEST=400、VALIDATION_FAILED=422、UNAUTHORIZED=401、FORBIDDEN=403、NOT_FOUND=404、METHOD_NOT_ALLOWED=405、SERVER_ERROR=500。
- 业务子码按需在控制器或服务层用 `Result.fail(code, msg)` 扩展，不强制全部收入枚举。
