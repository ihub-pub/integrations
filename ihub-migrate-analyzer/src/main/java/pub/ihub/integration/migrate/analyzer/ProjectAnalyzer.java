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
package pub.ihub.integration.migrate.analyzer;

import pub.ihub.integration.migrate.core.AnalysisReport;
import pub.ihub.integration.migrate.core.MigrationAnalyzer;
import pub.ihub.integration.migrate.core.MigrationRule;
import pub.ihub.integration.migrate.core.ProjectContext;

import java.util.List;

/**
 * 默认项目分析器，组合所有内置规则。
 *
 * <p>自动包含：
 * <ul>
 *   <li>{@link ObsoleteDependencyRule} - 过时依赖检测</li>
 *   <li>{@link JavaVersionRule} - Java 版本检测</li>
 * </ul>
 *
 * <p>使用示例：
 * <pre>{@code
 * ProjectAnalyzer analyzer = new ProjectAnalyzer();
 * ProjectContext ctx = new ProjectContext("my-app", "/path", "gradle", "11",
 *     Map.of("junit:junit", "4.13"), Map.of());
 * AnalysisReport report = analyzer.analyze(ctx);
 * }</pre>
 *
 * @author IHub
 * @since 0.1.0
 */
public class ProjectAnalyzer {

    private final MigrationAnalyzer analyzer;

    /** 创建包含所有内置规则的分析器。 */
    public ProjectAnalyzer() {
        this(defaultRules());
    }

    /** 创建使用自定义规则集的分析器。 */
    public ProjectAnalyzer(List<MigrationRule> rules) {
        this.analyzer = new MigrationAnalyzer(rules);
    }

    /**
     * 分析项目并返回报告。
     *
     * @param context 项目上下文
     * @return 聚合后的分析报告
     */
    public AnalysisReport analyze(ProjectContext context) {
        return analyzer.analyze(context);
    }

    /**
     * 返回默认内置规则列表。
     */
    public static List<MigrationRule> defaultRules() {
        return List.of(
            new ObsoleteDependencyRule(),
            new JavaVersionRule()
        );
    }
}
