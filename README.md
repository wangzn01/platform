# 项目说明

Platform 是基于 Spring Boot 4.1.0 与 JDK 25 构建的企业级 Web 后端项目骨架，使用 Spring MVC 和嵌入式 Tomcat，业务能力按实际需求逐步添加。

## 技术栈

| 技术 | 版本 | 说明 |
| --- | --- | --- |
| JDK | 25 | Java 开发与运行环境 |
| Spring Boot | 4.1.0 | 应用框架与依赖管理 |
| Maven | 3.9.16 | 项目构建与依赖管理 |
| Spring MVC | 随 Spring Boot 管理 | Web 与 REST API 框架 |
| Tomcat | 随 Spring Boot 管理 | 默认嵌入式 Web 容器 |
| Lombok | 随 Spring Boot 管理 | 编译期代码生成工具 |
| JUnit Jupiter | 随 Spring Boot 管理 | 单元测试框架 |
| AssertJ | 随 Spring Boot 管理 | 测试断言库 |

## 项目结构

```text
platform/
├── src/
│   ├── main/
│   │   ├── java/com/wang/platform/
│   │   │   ├── common/
│   │   │   │   └── response/
│   │   │   │       ├── Result.java            # 公共 API 全局返回参数
│   │   │   │       └── ResultEnum.java        # 全局返回参数业务状态码
│   │   │   ├── controller/
│   │   │   │   └── HelloController.java       # 示例接口
│   │   │   └── PlatformApplication.java       # 启动类
│   │   └── resources/
│   │       └── application.yml                # 应用配置
│   └── test/java/com/wang/platform/
│       ├── common/
│       │   └── response/
│       │       └── ResultTests.java           # 全局返回参数测试
│       ├── controller/
│       │   └── HelloControllerTests.java      # 接口测试
│       └── PlatformApplicationTests.java      # 启动测试
├── docs/
│   ├── decisions.md                           # 项目技术决策记录
│   ├── style.md                               # 代码格式与风格规范
│   └── todo.md                                # 技术待办与未决事项
├── .editorconfig                              # 编辑器基础格式
├── .gitattributes                             # Git 换行与文件类型规则
├── .gitignore                                 # Git 忽略规则
├── AGENTS.md                                  # AI 协作规则
├── CHANGELOG.md                               # 版本更新记录
├── CLAUDE.md                                  # Claude Code 规则入口
├── pom.xml                                    # Maven 项目配置
└── README.md                                  # 项目说明
```

## 环境要求

- JDK 25
- Maven 3.9.16
- Windows（示例命令均为 PowerShell）

## 使用说明

### 1. 运行项目

日常开发推荐直接在 IDEA 中运行或调试 `PlatformApplication`，也可以通过 Maven 启动：

```powershell
mvn spring-boot:run
```

JDK 25 默认使用 UTF-8，Spring Boot Parent 也已配置 Maven 构建编码，无需额外传入 `-Dfile.encoding=UTF-8`。

应用默认监听 `9999` 端口。启动后访问 `http://localhost:9999/hello`，响应内容为 `Hello, World!`。

### 2. 构建与测试

| 命令 | 用途 |
| --- | --- |
| `mvn test` | 运行全部测试 |
| `mvn -Dtest=ResultTests test` | 只运行指定测试类 |
| `mvn package` | 打包并运行测试 |
| `mvn package -DskipTests` | 跳过测试快速打包 |
| `mvn verify` | 完整验证，用于依赖、配置或公共代码变更后 |
| `mvn clean verify` | 清理后完整验证，用于发布前或重大依赖升级 |

具体改动该选哪一档验证，见 `AGENTS.md`「测试与验证」。

### 3. 运行 JAR

打包产物位于 `target/platform-0.1.0.jar`，运行方式：

```powershell
java -jar target/platform-0.1.0.jar
```

## Git 提交规范

项目采用 [Conventional Commits](https://www.conventionalcommits.org/zh-hans/v1.0.0/) 规范：

```text
<type>(<scope>): <subject>
```

`scope` 可省略，常用 `type` 如下：

| 类型 | 说明 |
| --- | --- |
| `feat` | 新增功能 |
| `fix` | 修复缺陷 |
| `docs` | 修改文档 |
| `style` | 调整格式，不改变程序行为 |
| `refactor` | 重构代码，不新增功能或修复缺陷 |
| `test` | 新增或修改测试 |
| `perf` | 性能优化 |
| `build` | 修改 Maven、依赖或构建配置 |
| `ci` | 修改持续集成配置 |
| `chore` | 其他辅助性修改 |

示例：

```text
feat(config): 增加应用配置读取
fix(test): 修复上下文启动测试
docs: 更新项目使用说明
```

## AI 协作

使用 Codex、Claude、Trae、Qoder 等工具时，先让 AI 阅读 `AGENTS.md`。其中的「文档分工」说明了各文档的职责边界，规则只维护在对应文件中，不跨文件重复。
