# 架构与公共契约

本文件维护当前生效的架构与公共契约，只收录已确认、会影响长期维护的技术选择；普通实现细节不记录。
规则变更时就地更新对应章节，确认时间与历史沿革见 Git 提交记录。

## 技术路线

- 语言与运行时为 Java 25，应用框架为 Spring Boot 4.1.x，构建工具为 Maven，Web 栈为 Spring MVC 和嵌入式 Tomcat。
- 本仓库只维护后端，前端作为独立项目。
- 使用与当前版本匹配的正式语言特性和框架能力，不为兼容旧示例而降低 Java 或 Spring Boot 版本。

## 依赖纪律

- 依赖和插件版本优先由 Spring Boot Parent 管理，不重复声明已管理的版本。
- JDK 或 Spring 已有清晰方案时不引入功能重叠的第三方依赖；新增依赖必须用途明确，并与当前版本兼容。
- Lombok 只用于编译期减少样板代码，不进入运行时依赖。

## 模块化单体架构

- **整体形态**：业务按模块内聚，单进程部署；保持单 Maven 模块，模块化通过包边界与依赖纪律实现。
- **基础包**：`com.wang.platform`；启动类 `PlatformApplication` 放根包并扫描整个包树。
- **顶层包**：`common/` 放不依赖 Spring 的共享定义；`infra/` 放依赖 Spring 或第三方框架的横切能力；业务模块直接放在
  根包下，不再额外包裹 `modules/`。
- **模块内部**：按需包含 `controller/`、`service/`、`mapper/`、`entity/`、`dto/`、`vo/`、`query/` 以及模块内的
  `constant/`、`enums/`、`exception/`、`job/`、`config/`。单实现的 Service 直接使用实现类，不为形式上的接口拆分。
- **避免过度分层**：当前不预设 `manager/`、`application/`、`domain/` 或无业务含义的通用基类、通用 Service、通用
  Controller、`utils`。确有复杂度证据时，先记录新的架构决策再引入。
- **边界职责**：业务 Controller 与外部适配器只负责协议转换、边界校验和调用用例；业务编排放在模块 Service，业务约束放在
  模块内业务代码。示例、探针等没有业务编排的端点可以直接返回固定结果。业务 Controller 不直接承载业务规则，也不绕过
  Service 操作 Mapper 或 Entity。Service 不依赖 Spring Web 类型，也不组装 `Result`。
- **依赖方向**：`common` 不依赖 Spring 或业务模块；`infra` 可依赖 `common`；业务模块只依赖公开的 `common` 定义和必要的
  基础设施契约；禁止循环依赖和跨模块直接访问 Mapper、Entity 等内部实现。
- **跨模块协作**：默认通过提供方的公开 Service 或明确的 `contract` 协作类型完成；调用方负责编排，不构造其他模块的
  Entity，也不依赖被调用方的持久化实现。
- **建包时机**：源码目录只在有源码文件时创建；删除或移动最后一个源码文件时同步删除空目录，不使用 `.gitkeep` 保留
  空包。`target/`、`.git/` 以及 IDE/AI 工具目录不属于源码包。当前尚无真实业务模块，API 版本示例控制器放在根包
  `controller/`；示例移除且该目录为空时同步删除目录。

## 接口与持久化模型的载体形式

- `DTO` 表示写入用例的请求模型，`Query` 表示查询条件，`VO` 表示面向 API 的输出模型，`Entity` 只表示持久化模型。
- `Entity` 不作为公共 API 的请求或响应模型；示例接口、测试接口、纯文本和文件流等特殊响应不强制使用 `VO`。
- DTO 不预留尚未使用的字段；新增与更新的共用规则与命名模板见「HTTP 请求方法与路径」。
- DTO、Query、VO 默认只服务所属模块，不跨模块复用。确有跨模块协作需求时，定义最小的公开契约并放在提供方的
  `contract/` 包中。
- DTO、Query、VO、Entity 统一使用 `class`，不使用 `record`。
- 载体类使用 Lombok 时以明显减少样板代码为前提，避免无差别使用 `@Data`；持久化模型谨慎生成 `equals`、`hashCode`、
  `toString`。

## 公共 API 版本管理

- 版本化接口把版本放在应用路径首段，格式为 `/vN/...`，其中 `N` 是一至三位正整数；当前示例提供 `GET /v1/test` 与
  `GET /v2/test`，`GET /hello` 明确为未版本化接口。网关统一前缀属于部署层选择，未引入网关前不写入后端公共契约。
- 使用 Spring MVC 官方 API Versioning：通过 `WebMvcConfigurer#configureApiVersioning` 配置路径段版本解析，通过映射注解的
  `version` 属性声明版本；不创建自定义版本注解或解析器。
- 项目同时允许版本化与未版本化接口。`spring.mvc.apiversion.required=false` 放在 YAML；版本段格式与版本化路径判定
  集中在 `common/api` 的 `ApiVersions`，配置类把它作为 `usePathSegment` 的路径 Predicate 传入，
  仅将 `/vN` 路径交给版本解析，使 `/hello` 等普通路径保持未版本化。
- 路径是唯一版本来源，不同时启用请求头、查询参数或媒体类型版本。支持的版本由 Controller 映射声明并由 Spring MVC 自动探测，
  不在 YAML 维护重复清单。
- 只有不兼容的公共契约变更才新增版本；兼容性修复继续维护原版本。
- 官方参考：Spring MVC API Versioning。
  https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-config/api-version.html

## HTTP 请求方法与路径

- 公共 API 只使用 GET 与 POST 两种请求方法：GET 用于查询，遵守安全且幂等的语义；POST 用于创建、更新、删除与其他
  业务动作。不使用 PUT、PATCH、DELETE 等其他方法。
- POST 默认不承诺幂等；确需幂等保证的接口在具体契约中说明实现方式。
- 业务接口路径为「资源/动作」形式，路径中携带资源标识时置于末段；资源使用复数名词，动作使用统一动词，
  不混用 add、save、remove、get、find 等同义词。路径段使用小驼峰，不使用连字符和下划线。
- 增删改查接口统一使用以下模板：`<Resource>` 与资源的 Entity 类名一致，`<resources>` 为其小驼峰复数形式，
  `{resourceId}` 为资源标识路径参数；如 Entity `Article` 对应 `articles` 与 `articleId`。DTO、Query、VO 均以
  `<Resource>` 为前缀命名，即 `<Resource>DTO`、`<Resource>Query`、`<Resource>VO`。

| 操作 | 接口模板 | 参数载体 |
| --- | --- | --- |
| 新增 | `POST /vN/<resources>/create` | `<Resource>DTO`，资源标识置空 |
| 批量新增 | `POST /vN/<resources>/batchCreate` | `List<<Resource>DTO>`，资源标识置空 |
| 更新 | `POST /vN/<resources>/update` | `<Resource>DTO`，资源标识必填 |
| 批量更新 | `POST /vN/<resources>/batchUpdate` | `List<<Resource>DTO>`，资源标识必填 |
| 删除 | `POST /vN/<resources>/delete/{resourceId}` | 路径参数 |
| 批量删除 | `POST /vN/<resources>/batchDelete` | 资源标识的 JSON 数组 |
| 条件删除 | `POST /vN/<resources>/delete` | `<Resource>DeleteDTO` |
| 详情 | `GET /vN/<resources>/detail/{resourceId}` | 路径参数 |
| 全量列表 | `GET /vN/<resources>/list` | 查询参数，条数规则见下 |
| 分页列表 | `GET /vN/<resources>/page` | `Query` |

- 批量接口的请求体统一为对应单个操作参数的 JSON 数组，不另建批量专用 DTO。
- 新增与更新是两个独立接口，操作意图由路径决定，但默认共用 `<Resource>DTO`：资源标识字段使用包装类型表达
  「未设置 / 已设置」二态，新增时必须为空、更新时必须有值，由接口边界校验。除资源标识外出现操作独有字段时，
  再拆分为 `<Resource>CreateDTO` 与 `<Resource>UpdateDTO`，拆分后资源标识只保留在 `<Resource>UpdateDTO` 中。
- 条件删除的筛选字段与写入字段语义不同，`<Resource>DeleteDTO` 始终独立，不并入 `<Resource>DTO`。
- 其他业务动作沿用同一模式，先按语义选择请求方法：查询类动作用 GET，仅凭资源标识即可执行时用动作加路径参数，
  其余条件按下方查询条件规则承载；写操作用 POST，仅凭资源标识即可执行时用动作加路径参数，例如
  `POST /v1/articles/publish/{articleId}`，需要更多参数时省略路径参数，全部参数由请求体 DTO 承载。
- 除版本段与动作段外，单个接口的业务参数只使用一种载体，不混用。载体与用途一一对应：路径参数只承载资源标识，
  查询参数（含对象形式 `Query`）只承载 GET 的查询条件，请求体只承载 POST 的操作参数（DTO、DTO 数组或资源标识
  数组）；GET 不使用请求体，POST 不使用查询参数。
- 跨资源查询不嵌套路径，改用目标资源的顶层路径加查询条件，例如按文章查评论使用 `GET /v1/comments/page`
  （`articleId` 与分页字段由 `Query` 承载），不使用 `GET /v1/articles/{articleId}/comments`。
- 查询条件按数量二选一：一至两个用 `@RequestParam` 逐个声明，三个及以上统一由模块内 `Query` 承载；分页列表
  无论条件多少一律使用 `Query`。
- 版本段 `/vN` 由 API Versioning 从路径解析、不注入方法参数，不计入参数载体的判断。
- 密码、验证码和超长内容不得放在路径或查询字符串中，只通过 HTTPS 请求体传递。
- 手机号等个人信息按普通查询条件处理。

## 公共 API 统一结构化响应

- `Result<T>` 是本项目的响应约定，不是 Spring MVC 的内置格式；它放在 `common/response/`，不引入 Jackson 等 Web 框架注解；
  纯文本、文件流和无响应体等特殊响应不套用该类型。
- `Result` 只包含 `code`、`data`、`msg` 三个字段，不增加 `success` 等派生字段。`code` 表示项目应用码，`data` 表示业务
  载荷，`msg` 只提供安全、可读的说明，调用方不得依赖文案判断逻辑。
- 失败响应可通过 `Result.fail(ResultEnum, String)` 自定义说明文案，用于校验失败等需要指出具体原因的场景；成功响应固定
  使用统一成功文案，不开放自定义，避免调用方在响应文案中承载业务语义。
- 当前通用应用码集中在 `ResultEnum`。它们借用常见 HTTP 数值便于识别，但不替代 HTTP 响应状态；不在 Controller 中
  散落裸数字。

## HTTP 状态码与响应体

- HTTP 状态码表达协议结果：按 HTTP 语义使用 2xx、4xx、5xx；创建资源可使用 201，无响应体的成功响应可使用 204，不能用
  HTTP 200 掩盖失败。
- 有结构化业务响应体时使用 `Result<T>`；纯文本、文件流和 204 响应不套 `Result`。`Result.code` 是项目应用码，与 HTTP 状态码
  由 API 边界分别决定，不要求数值始终相同。
- 参数绑定或格式错误通常使用 400；422 只有在项目明确约定「语法正确但语义不可处理」并实现对应映射时才使用。
- Controller 负责 HTTP 状态与响应体的映射。
- HTTP 方法和状态语义以 [RFC 9110](https://www.rfc-editor.org/rfc/rfc9110.html) 为依据；本节只补充项目边界，不替代标准。
