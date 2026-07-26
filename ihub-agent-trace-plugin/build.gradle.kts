/*
 * Copyright (c) 2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
description = "IHub代理核心组件"

// 注意：此处直接使用 com.gradleup.shadow（shadow 官方延续分支）而非 alias(ihub.plugins.shadow)。
// 原因：ihub-shadow 插件（ihub-settings 1.9.5）硬编码依赖 com.github.johnrengelman:shadow:8.1.1，
// 该版本在 Gradle 9 上 visitDir 访问已移除的 details.mode 属性导致 shadowJar 失败。
// com.gradleup.shadow 保留 com.github.jengelman.gradle.plugins.shadow 包，兼容 Gradle 9。
// TODO: 待 plugins 仓库 ihub-shadow 升级 shadow 版本后，恢复使用 alias(ihub.plugins.shadow)。
plugins {
    id("com.gradleup.shadow") version "8.3.6"
}

dependencies {
    implementation(project(":ihub-agent-core"))
    implementation("org.apache.tomcat.embed:tomcat-embed-core")
    implementation("io.opentelemetry:opentelemetry-api")
    implementation("io.opentelemetry:opentelemetry-sdk")
}

// 内联 ihub-shadow 插件的 Java agent manifest 注入逻辑（原 IHubShadowPlugin 自动检测 premain/agentmain）。
tasks.named<Jar>("shadowJar") {
    manifest.attributes("Premain-Class" to "pub.ihub.integration.agent.trace.IHubTraceAgent")
    manifest.attributes("Can-Redefine-Classes" to true)
    manifest.attributes("Can-Retransform-Classes" to true)
}
