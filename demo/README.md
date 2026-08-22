# IHub 迁移工具 Demo

本目录演示 IHub 迁移分析工具对**真实遗留项目**的端到端分析能力
（P2 完成标准：真实项目通过迁移工具分析）。

## legacy-app：遗留系统夹具

一个模拟 2022 年技术栈的 Spring Boot 2.x 项目：

| 技术 | 版本 | 迁移目标 |
|------|------|---------|
| Spring Boot | 2.7.18（EOL） | 3.x → 4.x |
| Java | 1.8 | 17 → 21 |
| JSON | fastjson 1.2.83 | fastjson2 / Jackson（IHub catalog: utilities） |
| 测试 | JUnit 4.13 | JUnit 5 |
| Web | javax.servlet | jakarta.servlet |

## 运行分析

```bash
# 在 integrations 仓库根目录
./gradlew :ihub-migrate-analyzer:test --tests '*LegacySystemAnalysisDemoTest*'
```

测试执行完整链路：**解析 pom.xml → 构建 ProjectContext → 运行规则集 → 输出迁移报告**，
报告内容见测试控制台输出。

## 迁移路径（报告结论）

1. **构建工具先行**：Maven → Gradle Kotlin DSL（`gradle-groovy-to-kotlin-dsl` 规则）
2. **Boot 2.7 → 3.x**：`spring-boot-2-to-3` 规则，OpenRewrite `UpgradeSpringBoot_3_4`
   + `JavaxMigrationToJakarta`
3. **Boot 3.x → 4.x**：`spring-boot-3-to-4` 规则（Framework 7 + Jackson 3），
   OpenRewrite `UpgradeSpringBoot_4_0`
4. **依赖替换**：`ObsoleteDependencyRule` 给出 IHub catalog 替代建议
   （fastjson → jackson，junit4 → junit5 等）

## 与 MCP Server 集成

agents/mcp-server 的 `analyzeProject(projectPath)` 工具将本目录的分析能力
暴露为 MCP 接口，AI 编码代理可直接调用完成「扫描 → 分析 → 迁移计划」闭环。
