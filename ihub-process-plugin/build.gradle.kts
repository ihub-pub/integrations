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
    alias(ihub.plugins.publish)
}

val autoService: Provider<MinimalExternalModuleDependency> = libs.auto.service
val compileTesting: Provider<MinimalExternalModuleDependency> = libs.compile.testing
val incap = libs.incap
val incapProcessor = libs.incap.processor

dependencies {
    implementation(project(":ihub-process-core"))
    compileOnly(autoService)
    compileOnly(incap)
    annotationProcessor(autoService)
    annotationProcessor(incapProcessor)
    testImplementation(compileTesting)
    testImplementation("org.mockito:mockito-core")
}

dependencies {
    implementation("org.jmolecules.integrations:jmolecules-bytebuddy")
    implementation("jakarta.persistence:jakarta.persistence-api")
    implementation("io.swagger.core.v3:swagger-annotations-jakarta:2.2.19")


    implementation("org.springframework.boot:spring-boot")
    testImplementation("org.springframework.boot:spring-boot-autoconfigure")
}
