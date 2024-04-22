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
plugins {
    alias(ihub.plugins.root)
    alias(ihub.plugins.copyright)
    alias(ihub.plugins.git.hooks)
    alias(ihub.plugins.java) apply false
    alias(ihub.plugins.test) apply false
    alias(ihub.plugins.verification) apply false
    alias(ihub.plugins.publish) apply false
    id("pub.ihub.plugin.ihub-boot") apply false
//    id("net.bytebuddy.byte-buddy-gradle-plugin") version "1.14.11"
}

subprojects {
    !project.pluginManager.hasPlugin("java-platform") || return@subprojects
    apply {
        plugin("pub.ihub.plugin.ihub-java")
        plugin("pub.ihub.plugin.ihub-test")
        plugin("pub.ihub.plugin.ihub-verification")
        plugin("pub.ihub.plugin.ihub-publish")
    }

    dependencies {
        if (project.name != "ihub-core") {
            "api"(project(":ihub-core"))
        }
    }

    // TODO 插件配置doc编码
    tasks.withType(Javadoc::class.java) {
        options.encoding = "UTF-8"
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
