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
import org.gradle.api.plugins.JavaPluginExtension

plugins {
    alias(ihub.plugins.root)
    alias(ihub.plugins.copyright)
    alias(ihub.plugins.git.hooks)
    alias(ihub.plugins.java) apply false
    alias(ihub.plugins.test) apply false
    alias(ihub.plugins.verification) apply false
    alias(ihub.plugins.publish) apply false
}

subprojects {
    !project.pluginManager.hasPlugin("java-platform") || return@subprojects
    apply {
        plugin("pub.ihub.plugin.ihub-java")
        plugin("pub.ihub.plugin.ihub-test")
        plugin("pub.ihub.plugin.ihub-verification")
        plugin("pub.ihub.plugin.ihub-publish")
    }

    // 字节码基线固定为 Java 17，与 libs 等消费方（含 Java 17 CI）保持一致。
    // 0.2.4 在 JDK 21 环境发布时默认编译为 Java 21 基线（org.gradle.jvm.version=21），
    // 导致 Java 17 消费方依赖解析失败（libs CI: JVM runtime 17 vs ihub-core:0.2.4 需 21+）。
    extensions.configure<JavaPluginExtension> {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    dependencies {
        if (project.name != "ihub-core") {
            "api"(project(":ihub-core"))
        }
    }
}

iHubGitHooks {
    hooks.set(
        mapOf(
            "pre-commit" to "./gradlew build",
            "commit-msg" to "./gradlew commitCheck"
        )
    )
}
