import net.bytebuddy.build.gradle.Adjustment

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
description = "字节码增强插件"

dependencies {
    implementation(project(":ihub-bytebuddy-core"))
    implementation("net.bytebuddy:byte-buddy")
    implementation("org.jmolecules:jmolecules-ddd")
//    implementation("org.jmolecules:jmolecules-events")
    implementation("jakarta.persistence:jakarta.persistence-api")
    implementation("io.swagger.core.v3:swagger-core-jakarta")
    implementation("org.springframework:spring-web")
    implementation("cn.hutool:hutool-all")
    implementation("com.thoughtworks.qdox:qdox:2.1.0")

    testImplementation("org.assertj:assertj-core")
}

byteBuddy {
//extensions.configure(AbstractByteBuddyTaskExtension::class.java) {
//    transformation {
//        plugin = pub.ihub.integration.bytebuddy.IHubControllerDocPlugin::class.java
//    }
//    discovery = Discovery.UNIQUE
//    discoverySet = files("META-INF/net.bytebuddy/build.plugins")
    adjustment = Adjustment.ACTIVE
}
