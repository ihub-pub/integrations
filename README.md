<p align="center">
    <a target="_blank" href="https://ihub.pub/">
        <img src="https://doc.ihub.pub/ihub.svg" height="150" alt="IHub">
        <img src="https://doc.ihub.pub/ihub_libs.svg" height="150" alt="IHub">
    </a>
</p>

---

<p align="center">
    <a target="_blank" href="https://github.com/ihub-pub/integrations/actions/workflows/gradle-build.yml">
        <img src="https://badge.ihub.pub/github/actions/workflow/status/ihub-pub/integrations/gradle-build.yml?branch=main&label=Build&logo=GitHub+Actions&logoColor=white" alt="Gradle Build"/>
    </a>
    <a title="Test Cases" href="https://ihub-pub.testspace.com/spaces/267303?utm_campaign=metric&utm_medium=referral&utm_source=badge">
        <img alt="Space Metric" src="https://badge.ihub.pub/testspace/tests/ihub-pub/ihub-pub:integrations/main?compact_message&label=Tests&logo=GitHub+Actions&logoColor=white" />
    </a>
    <a target="_blank" href="https://www.codefactor.io/repository/github/ihub-pub/integrations">
        <img src="https://badge.ihub.pub/codefactor/grade/github/ihub-pub/integrations/main?color=white&label=Codefactor&labelColor=F44A6A&logo=CodeFactor&logoColor=white" alt="CodeFactor"/>
    </a>
    <a target="_blank" href="https://codecov.io/gh/ihub-pub/integrations">
        <img src="https://badge.ihub.pub/codecov/c/github/ihub-pub/integrations?token=ZQ0WR3ZSWG&color=white&label=Codecov&labelColor=F01F7A&logo=Codecov&logoColor=white" alt="Codecov"/>
    </a>
    <a target="_blank" href="https://github.com/ihub-pub/integrations">
        <img src="https://badge.ihub.pub/github/stars/ihub-pub/integrations?color=white&style=flat&logo=GitHub&labelColor=181717&label=Stars" alt="IHubPub"/>
    </a>
    <a target="_blank" href="https://gitee.com/ihub-pub/integrations">
        <img src="https://badge.ihub.pub/badge/dynamic/json?url=https%3A%2F%2Fgitee.com%2Fapi%2Fv5%2Frepos%2Fihub-pub%2Fintegrations&query=%24.stargazers_count&style=flat&logo=gitee&label=stars&labelColor=c71d23&color=white&cacheSeconds=5000" alt="IHubPub"/>
    </a>
    <a target="_blank" href="https://javadoc.io/doc/pub.ihub.integration">
        <img alt="Java Doc" src="https://javadoc.io/badge2/pub.ihub.integration/ihub-core/javadoc.svg?color=white&labelColor=8CA1AF&label=Docs&logo=readthedocs&logoColor=white">
    </a>
    <a target="_blank" href="https://mvnrepository.com/artifact/pub.ihub.integration">
        <img src="https://badge.ihub.pub/maven-central/v/pub.ihub.integration/ihub-bom?color=white&labelColor=C71A36&label=Maven&logo=Apache+Maven&logoColor=white" alt="Maven Central"/>
    </a>
</p>

## IHub 动态适配层（L2 Capability Board）

IHub 三层架构中的 **动态适配层**，提供两类核心能力：

### 🔄 旧系统迁移工具（P2 新增）

帮助存量 Java 系统完成技术现代化升级，核心理念：**丝滑迁移，而非推倒重来**。

| 模块 | 定位 |
|------|------|
| `ihub-migrate-core` | 迁移分析引擎：规则接口、项目上下文、AI 可读报告 |
| `ihub-migrate-rewrite` | OpenRewrite 集成：Spring Boot 3.x 迁移 Recipe |
| `ihub-migrate-analyzer` | 依赖图分析：与 libs catalog 联动，识别过时依赖 |

### ⚡ 运行时字节码增强

基于 ByteBuddy 的 Java Agent，无侵入地为旧系统添加链路追踪、方法拦截等能力。

| 模块 | 定位 |
|------|------|
| `ihub-agent-core` | Java Agent 基础设施 |
| `ihub-agent-trace-plugin` | 链路追踪增强（OpenTelemetry） |
| `ihub-bytebuddy-core` | ByteBuddy DSL 封装 |
| `ihub-bytebuddy-plugin` | IHub 特定增强插件 |
| `ihub-process-core` | 注解处理器（APT）基础设施 |

> 详细设计：[P2 战略重定向文档](https://github.com/ihub-pub/ihub/blob/main/docs/strategy/2026-05-04-ihub-integrations-p2-design.md)

## 🧭 开源贡献指南

请阅读 [贡献指南](https://github.com/ihub-pub/.github/blob/main/CONTRIBUTING.md) 为该项目做出贡献

## 👨‍💻 Contributors

![Alt](https://repobeats.axiom.co/api/embed/10b52c85a6a8d23a2601bd26bd16716deddbc073.svg "Repobeats analytics image")

[![Contributors](https://contrib.rocks/image?repo=ihub-pub/integrations)](https://github.com/ihub-pub/integrations/graphs/contributors "Contributors")
