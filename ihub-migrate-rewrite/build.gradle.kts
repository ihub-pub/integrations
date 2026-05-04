/*
 * Copyright (c) 2026 the original author or authors.
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
description = "IHub 迁移工具 OpenRewrite 集成"

dependencies {
    api(project(":ihub-migrate-core"))
    api("org.openrewrite:rewrite-core:8.40.2")
    api("org.openrewrite:rewrite-java:8.40.2")
    compileOnly("org.openrewrite.recipe:rewrite-spring:5.25.0")
    compileOnly("org.openrewrite.recipe:rewrite-migrate-java:2.31.0")
    compileOnly("org.springframework.boot:spring-boot")

    testImplementation("org.openrewrite.recipe:rewrite-spring:5.25.0")
    testImplementation("org.openrewrite.recipe:rewrite-migrate-java:2.31.0")
    testImplementation("org.springframework.boot:spring-boot")
}
