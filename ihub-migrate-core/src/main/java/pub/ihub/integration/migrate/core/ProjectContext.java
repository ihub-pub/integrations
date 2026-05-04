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
package pub.ihub.integration.migrate.core;

import java.util.List;
import java.util.Map;

/**
 * 项目上下文，封装待分析项目的元数据。
 *
 * <p>由项目扫描器（ProjectScanner）构建，包含：
 * <ul>
 *   <li>依赖列表（GAV 坐标）</li>
 *   <li>源码模式摘要（注解、API 调用统计）</li>
 *   <li>构建工具及版本</li>
 *   <li>Spring Boot / Java 版本</li>
 * </ul>
 *
 * @param projectName   项目名称
 * @param projectPath   项目根目录
 * @param buildTool     构建工具，如 {@code "gradle"} / {@code "maven"}
 * @param javaVersion   Java 版本，如 {@code "17"}
 * @param dependencies  依赖列表，键为 {@code "group:artifact"}，值为版本字符串
 * @param metadata      扩展元数据（键值对，供规则使用）
 * @author IHub
 * @since 0.1.0
 */
public record ProjectContext(
        String projectName,
        String projectPath,
        String buildTool,
        String javaVersion,
        Map<String, String> dependencies,
        Map<String, Object> metadata
) {}
